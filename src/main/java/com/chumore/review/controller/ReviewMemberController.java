package com.chumore.review.controller;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.member.model.MemberService;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.review.model.ReviewService;
import com.chumore.review.model.ReviewVO;
import com.chumore.reviewimg.model.ReviewImageService;
import com.chumore.reviewimg.model.ReviewImageVO;
import com.chumore.util.ResponseUtil;

@Controller
@RequestMapping("/member/reviews")
public class ReviewMemberController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewImageService reviewImageService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private OrderMasterService orderSvc;
    
    @Autowired
    private Product_Service productSvc;
    
    @Autowired
    private RestService restSvc;

    /**
     * 獲取訂單資訊用於評論頁面初始化
     * 對應前端：用餐資訊區域的自動填充
     */
    @GetMapping("/order/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderForReview(@PathVariable Integer orderId) {
        try {
            Map<String, Object> orderInfo = new HashMap<>();
            // 需要與Order服務整合，獲取：
            // - 餐廳資訊（名稱、圖片、地址）
            // - 用餐日期時間
            // - 用餐人數
            // - 訂單金額
            return ResponseEntity.ok(orderInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 創建新評論
     * 對應前端：評論表單提交
     * 處理：星級評分、文字評論、推薦料理、平均消費
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createReview(
            @RequestHeader("Member-Id") Integer memberId,
            @Valid @RequestBody ReviewVO review,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos) {
        try {
            review.setMember(memberService.getOneMember(memberId).orElse(null));
            ReviewVO createdReview = reviewService.createReview(review);

            // 處理圖片上傳
            if (photos != null && photos.length > 0) {
                for (MultipartFile photo : photos) {
                    reviewImageService.uploadImage(photo, createdReview.getReviewId(), memberId);
                }
            }

            return ResponseEntity.ok(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 檢查訂單是否已評論
     * 對應前端：進入評論頁面時的檢查
     */
    @GetMapping("/check/{orderId}")
    @ResponseBody
    public ResponseEntity<?> checkReviewExists(@PathVariable Integer orderId) {
        try {
            boolean exists = reviewService.existsByOrderId(orderId);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 取得餐廳菜品列表
     * 對應前端：推薦料理下拉選單
     */
    @GetMapping("/restaurant/{restId}/dishes")
    @ResponseBody
    public ResponseEntity<?> getRestaurantDishes(@PathVariable Integer restId) {
        try {
            return ResponseEntity.ok(new HashMap<>());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 單張圖片上傳處理
     * 對應前端：照片上傳區的即時上傳
     */
    @PostMapping("/{reviewId}/upload-image")
    @ResponseBody
    public ResponseEntity<?> uploadImage(
            @PathVariable Integer reviewId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Member-Id") Integer memberId) {
        try {
            // 檢查圖片大小
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("圖片大小不可超過5MB");
            }

            // 檢查圖片格式
            String contentType = file.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                return ResponseEntity.badRequest().body("僅支援JPG、PNG格式");
            }

            ReviewImageVO uploadedImage = reviewImageService.uploadImage(file, reviewId, memberId);
            return ResponseEntity.ok(uploadedImage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 刪除評論圖片
     * 對應前端：照片預覽區的刪除功能
     */
    @DeleteMapping("/images/delete/{reivewImgId}")
    @ResponseBody
    public ResponseEntity<?> deleteImage(@PathVariable Integer reivewImgId) {
        try {
            reviewImageService.deleteImage(reivewImgId);
            return ResponseEntity.ok("圖片已成功刪除, id = " + reivewImgId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 準備新增評論 - thymeleaf
    @PostMapping("addReview")
    public String addReview(ModelMap model, HttpSession session, @RequestParam Integer orderId) {
    	Integer memberId = (Integer)session.getAttribute("memberId");
    	
    	if ((Integer)session.getAttribute("memberId") == null) {    		
    		memberId = 1002;
    	} 
    	
    	OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
    	
    	ReviewVO review = new ReviewVO();
    	review.setRest(orderMaster.getRest());
    	review.setMember(orderMaster.getMember());
//    	review.setMember(memberService.getOneMember(memberId));
    	review.setOrderMaster(orderMaster);
    	
    	session.setAttribute("reviewedRest", orderMaster.getRest());
    	session.setAttribute("reviewedOrder", orderMaster);
    	
    	model.addAttribute("orderMaster", orderMaster);
    	model.addAttribute("review", review);
    	model.addAttribute("productListData", 
    			productSvc.getAllProductByActiveStatus(orderMaster.getRest().getRestId()));
//    	model.addAttribute("restId", review.getRest().getRestId());
    	
    	System.out.println("origin order = " + orderMaster);
//    	System.out.println("req = " + (OrderMasterVO)req.getAttribute("orderMaster"));
    	return "secure/member/review/member_review_adding_page";
    }
    
    @PostMapping("insert")
    public String insert(@Valid @ModelAttribute("review") ReviewVO review, BindingResult result, ModelMap model, 
    		HttpSession session, HttpServletRequest req, @RequestParam(value = "upfiles", required = false) MultipartFile[] reviewImgs) throws IOException {
    	RestVO reviewedRest = (RestVO)session.getAttribute("reviewedRest");
    	OrderMasterVO reviewedOrder = (OrderMasterVO)session.getAttribute("reviewedOrder");
    	
    	result = removeFieldError(review, result, "member");
    	result = removeFieldError(review, result, "rest");
    	result = removeFieldError(review, result, "orderMaster");

    	if (session.getAttribute("memberId")  == null) {    		
    		session.setAttribute("memberId", 1002);
    	}
    	
    	if (result.hasErrors()) {
    		System.out.println("Errors: " + result.getFieldErrors());
    		model.addAttribute("orderMaster", reviewedOrder);
        	model.addAttribute("review", review);
        	model.addAttribute("productListData", 
        			productSvc.getAllProductByActiveStatus(reviewedOrder.getRest().getRestId()));
        	
    		return "secure/member/review/member_review_adding_page";
    	}
    	
    	review.setMember(memberService.getOneMember((Integer)session.getAttribute("memberId")).orElse(null));
    	review.setRest(reviewedRest);
    	review.setOrderMaster(reviewedOrder);

//    	System.out.println("review = " + review);    	   		
    	review = reviewService.createReviewWithImg(review, reviewImgs);
    	
    	return "redirect:/member/dining_and_review/history";
    }
    
	@PostMapping("update") // getOneForUpdate
	public String getOneForUpdate(@RequestParam String reviewId, ModelMap model, HttpSession session) {
		ReviewVO review = reviewService.getReviewById(Integer.valueOf(reviewId), session);
		List<ReviewImageVO> imgList = reviewImageService.getReviewImages(Integer.valueOf(reviewId));
		model.addAttribute("review", review);
		model.addAttribute("reviewImgs", imgList);
		
		List<ProductVO> availableProduct = 
				productSvc.getAllProductByActiveStatus(review.getRest().getRestId());
		ProductVO selectedProduct = review.getProduct();
		Boolean isAvailable = false;
		
		if (availableProduct != null) {
			for (ProductVO product: availableProduct) {
				if (product.getProductId().equals(selectedProduct.getProductId())) {
					isAvailable = true;
					break;
				}
			}
		}
		
		if (!isAvailable || availableProduct.isEmpty()) {
			availableProduct.add(selectedProduct);
		}
		model.addAttribute("productListData", availableProduct);
//		System.out.println("review = " + review);
		
		return "secure/member/review/member_review_update_page";
	}
	
	@PostMapping("confirmUpdate")
	public String update(@Valid @ModelAttribute("review") ReviewVO review, BindingResult result, 
			ModelMap model, @RequestParam(value = "upfiles", required = false) MultipartFile[] reviewImgs, HttpSession session) throws IOException {
		
		result = removeFieldError(review, result, "member");
    	result = removeFieldError(review, result, "rest");
    	result = removeFieldError(review, result, "orderMaster");
    	
    	review.setOrderMaster(orderSvc.getOneById(review.getOrderMaster().getOrderId()));
    	review.setMember(memberService.getOneMember(review.getMember().getMemberId()).orElse(null));
    	review.setRest(restSvc.getOneById(review.getRest().getRestId()));
    	
//    	System.out.println(review);
    	
		if (result.hasErrors()) {
    		System.out.println("Errors: " + result.getFieldErrors());
    		List<ReviewImageVO> imgList = reviewImageService.getReviewImages(review.getReviewId());
    		review.setReviewImages(imgList);
        	List<ProductVO> availableProduct = 
    				productSvc.getAllProductByActiveStatusAndSelected(review.getRest().getRestId(), review.getProduct());
//    		model.addAttribute("orderMaster", reviewedOrder);
        	model.addAttribute("review", review);
        	model.addAttribute("reviewImgs", imgList);
    		model.addAttribute("productListData", availableProduct);
        	
    		return "secure/member/review/member_review_update_page";
    	}
		
		reviewService.updateReviewWithImgs(review, session, reviewImgs);
		
		return "redirect:/member/dining_and_review/history";
	}
    
    // 顯示單筆評論 - thymeleaf
    @PostMapping("getReview")
    public String getOneReview(@RequestParam String orderId, ModelMap model, HttpSession session) {
    	ReviewVO review = reviewService.getReviewByOrderId(Integer.valueOf(orderId));
    	OrderMasterVO orderMaster = orderSvc.getOneById(Integer.valueOf(orderId));
    	model.addAttribute("review", review);
    	model.addAttribute("orderMaster", orderMaster);
//    	System.out.println(session.getAttribute("order"));
    	return "secure/member/review/member_review_show_page";
    }
    
	@GetMapping("/images/{reviewId}")
	@ResponseBody
	public ResponseEntity<List<Integer>> getRevImgIds(@PathVariable Integer reviewId){
		List<Integer> imageIds = reviewImageService.findImgIdByReview(reviewId);
		return ResponseEntity.ok(imageIds);
	}
    
	@GetMapping("/image/{reviewImgId}")
	@ResponseBody
	public ResponseEntity<byte[]> getOneRevImg(@PathVariable Integer reviewImgId) {
		ReviewImageVO reviewImage = reviewImageService.getOneRevImg(reviewImgId);
//		System.out.println(reviewImage.getReviewImage());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(reviewImage.getReviewImage());
	}
	
	@PostMapping("getMemberReviews")
	@ResponseBody
	public ResponseEntity<ResponseUtil> getMemberReview(HttpSession session){
		Integer memberId = (Integer)session.getAttribute("memberId");
		if (memberId == null) {
			memberId = 1002;
		}
		System.out.print("member:" + session.getAttribute("memberId"));
		
		List<ReviewVO> list = reviewService.getMemberReviews(memberId);
		ResponseUtil res = new ResponseUtil("success", 200, list);
		
		return ResponseEntity.ok(res);
	}
	
	@PostMapping("deleteReview")
	@ResponseBody
	public ResponseEntity<ResponseUtil> deleteReview(@RequestBody Map <String, String> map, HttpSession session){
		Integer memberId = (Integer)session.getAttribute("memberId");
		if (memberId == null) {
			memberId = 1001;
		}
		reviewService.deleteReview(Integer.valueOf(map.get("reviewId")), memberId);
		ResponseUtil res = new ResponseUtil("success", 200, null);
		
		return ResponseEntity.ok(res);
	}
	

	
	public BindingResult removeFieldError(ReviewVO review, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		
		result = new BeanPropertyBindingResult(review, "review");
//		result.getFieldErrors().clear();
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

	@GetMapping("/reviewCounts/{restId}")
	@ResponseBody
	public ResponseEntity<?> getReviewCountsByRestId(@PathVariable Integer restId){
		return ResponseEntity.ok(reviewService.getReviewCount(restId));
	}

	@GetMapping("/averageRating/{restId}")
	@ResponseBody
	public ResponseEntity<?> getAverageRatingByRestId(@PathVariable Integer restId){
		return ResponseEntity.ok(reviewService.calculateAverageRating(restId));
	}


	@GetMapping("/averageCost/{restId}")
	@ResponseBody
	public ResponseEntity<?> getAverageCostByRestId(@PathVariable Integer restId){
		return ResponseEntity.ok(reviewService.getAverageCostByRestId(restId));
	}
	
	@GetMapping("/restaurant/{restId}")
	@ResponseBody
	public ResponseEntity<Page<ReviewVO>> getRestaurantReviews(
	        @PathVariable Integer restId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size) {
	    try {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<ReviewVO> reviews = reviewService.getRestReviews(restId, pageable);
	        return ResponseEntity.ok(reviews);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}

	@GetMapping("/restaurant/{restId}/summary")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getReviewSummary(@PathVariable Integer restId) {
	    try {
	        Map<String, Object> summary = new HashMap<>();
	        summary.put("averageRating", reviewService.calculateAverageRating(restId));
	        summary.put("totalReviews", reviewService.getReviewCount(restId));
	        
	        return ResponseEntity.ok(summary);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	
	}
	
	
    
}