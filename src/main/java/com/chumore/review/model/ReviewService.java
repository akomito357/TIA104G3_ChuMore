package com.chumore.review.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.exception.ResourceNotFoundException;
import com.chumore.member.model.MemberVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.reviewimg.model.ReviewImageService;
import com.chumore.reviewimg.model.ReviewImageVO;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ReviewImageService reviewImageService;
    
    @Autowired
    private RestService restSvc;

    @Transactional
    public ReviewVO createReview(ReviewVO review) {
//        validateNewReview(review);
    	
        review.setReviewDatetime(new Timestamp(System.currentTimeMillis()));
        return reviewRepository.save(review);
    }
    
    @Transactional
    public ReviewVO createReviewWithImg(ReviewVO review, MultipartFile... reviewImgs) throws IOException {
//    	review.setReviewDatetime(new Timestamp(System.currentTimeMillis()));
    	review = createReview(review);
    	System.out.println("creat with img:" + review);
    	
    	if (reviewImgs.length != 0) {
    		System.out.println("reviewImgs" + reviewImgs);
    		List<ReviewImageVO> imgList = new ArrayList<>();
    		for (MultipartFile mutipartFile : reviewImgs) {
    			if(!mutipartFile.isEmpty()) {    				
    				byte[] buf = mutipartFile.getBytes();
    				System.out.println("buf" + buf);
    				
    				ReviewImageVO img = new ReviewImageVO();
    				img.setReviewImage(buf);
    				img.setReview(review);
    				
    				reviewImageService.addImg(img);
    				imgList.add(img);
    			}
    		}
    		
    		review.setReviewImages(imgList);
    	}
    	
    	// 處理餐廳星數更新
    	RestVO rest = review.getRest();
    	Double originReviewStars = review.getReviewRating().doubleValue();
    	Double newRestStars = rest.getRestStars() + originReviewStars;
    	Integer newRestReviewers = rest.getRestReviewers() + 1;
    	
    	System.out.println(rest);
    	System.out.println(originReviewStars);
    	System.out.println(newRestStars);
    	System.out.println(newRestReviewers);
    	
    	
    	rest.setRestStars(newRestStars);
    	rest.setRestReviewers(newRestReviewers);
    	System.out.println(rest);
    	
    	restSvc.updateRest(rest);
    	System.out.println(rest);
    	
    	
        return review;
    }
    
    @Transactional
    public ReviewVO updateReviewWithImgs(ReviewVO review, HttpSession session, MultipartFile... newReviewImgs) throws IOException {
    	review.setReviewDatetime(new Timestamp(System.currentTimeMillis()));
    	
    	if (newReviewImgs.length != 0) {
    		System.out.println("reviewImgs" + newReviewImgs);
    		List<ReviewImageVO> imgList = review.getReviewImages();
    		Boolean newImgList = false;
    		if (imgList == null) {
    			imgList = new ArrayList<>();
    			newImgList = true;
    		}
    		
    		for (MultipartFile mutipartFile : newReviewImgs) {
    			if(!mutipartFile.isEmpty()) {    				
    				byte[] buf = mutipartFile.getBytes();
    				System.out.println("buf" + buf);
    				
    				ReviewImageVO img = new ReviewImageVO();
    				img.setReviewImage(buf);
    				img.setReview(review);
    				
    				reviewImageService.addImg(img);
    				
    				imgList.add(img);
    			}
    		}
    		if (newImgList) {
    			review.setReviewImages(imgList);
    		}
    	}
    	
    	// 處理餐廳星數更新
    	Double newReviewRating = review.getReviewRating().doubleValue();
    	Double orinigReviewRating = Double.valueOf((session.getAttribute("originReviewRating").toString()));
    	
    	System.out.println("newReviewRating: " + newReviewRating);
    	System.out.println("orinigReviewRating: " + orinigReviewRating);
    	
    	if (Double.compare(newReviewRating, orinigReviewRating) != 0) {
    		// 評價星數被更動過，需要更新餐廳總星數
    		RestVO rest = review.getRest();
    		rest.setRestStars(rest.getRestStars() - orinigReviewRating + newReviewRating);
    		restSvc.updateRest(rest);
    	}
    	
    	return reviewRepository.save(review);
    }
    

    @Transactional
    public ReviewVO updateReview(Integer reviewId, ReviewVO updateRequest, Integer memberId) {
        Optional<ReviewVO> existingReviewOpt = reviewRepository.findById(reviewId);
        
        if (existingReviewOpt.isEmpty()) {
            throw new RuntimeException("評論不存在");
        }
        
        ReviewVO existingReview = existingReviewOpt.get();
//        if (!existingReview.getMember().getMemberId().equals(memberId)) {
//            throw new RuntimeException("無權限修改此評論");
//        }

        validateReviewUpdate(existingReview);
        
        existingReview.setReviewText(updateRequest.getReviewText());
        existingReview.setReviewRating(updateRequest.getReviewRating());
        existingReview.setAvgCost(updateRequest.getAvgCost());
        existingReview.setProduct(updateRequest.getProduct());
        
        
        
        return reviewRepository.save(existingReview);
    }

    @Transactional
    public void deleteReview(Integer reviewId, Integer memberId) {
        Optional<ReviewVO> reviewOpt = reviewRepository.findById(reviewId);
        
//        if (reviewOpt.isEmpty()) {
//            throw new RuntimeException("評論不存在");
//        }
        
        ReviewVO review = reviewOpt.get();
//        if (!review.getMember().getMemberId().equals(memberId)) {
//            throw new RuntimeException("無權限刪除此評論");
//        }
        
        RestVO rest = review.getRest();
        Double originReviewRating = review.getReviewRating().doubleValue();
        
        rest.setRestReviewers(rest.getRestReviewers() - 1);
        rest.setRestStars(rest.getRestStars() - originReviewRating);
        restSvc.updateRest(rest);
        
        reviewImageService.deleteAllByReviewId(reviewId);
//      reviewRepository.deleteById(reviewId);
        reviewRepository.deleteByReviewId(reviewId);
    }

    public Page<ReviewVO> getRestReviews(Integer restId, Pageable pageable) {
        if (restId == null) {
            throw new RuntimeException("餐廳ID不能為空");
        }
        return reviewRepository.findByRestIdOrderByReviewDatetimeDesc(restId, pageable);
    }

    public List<ReviewVO> getMemberReviews(Integer memberId) {
        if (memberId == null) {
            throw new ResourceNotFoundException("會員ID不能為空");
        }
        return reviewRepository.findByMemberIdOrderByReviewDatetimeDesc(memberId);
    }

    public boolean existsByOrderId(Integer orderId) {
        if (orderId == null) {
            throw new RuntimeException("訂單ID不能為空");
        }
        return reviewRepository.existsByOrderId(orderId);
    }

    public BigDecimal getAverageCostByRestId(Integer restId){
        BigDecimal averageCost = reviewRepository.calculateAverageCostByRestId(restId);
        return averageCost != null ? averageCost : BigDecimal.ZERO;
    }

    public BigDecimal calculateAverageRating(Integer restId) {
        if (restId == null) {
            throw new RuntimeException("餐廳ID不能為空");
        }
        BigDecimal averageRating = reviewRepository.calculateAverageRating(restId);
        return averageRating != null ? averageRating : BigDecimal.ZERO;
    }

    public Long getReviewCount(Integer restId) {
        if (restId == null) {
            throw new RuntimeException("餐廳ID不能為空");
        }
        return reviewRepository.countByRestId(restId);
    }

    private void validateNewReview(ReviewVO review) {
        if (review == null) {
            throw new RuntimeException("評論資料不能為空");
        }

        if (review.getReviewText() == null || review.getReviewText().trim().isEmpty()) {
            throw new RuntimeException("評論內容不能為空");
        }
        
        if (review.getReviewRating() == null || 
            review.getReviewRating().compareTo(BigDecimal.ZERO) < 0 || 
            review.getReviewRating().compareTo(new BigDecimal("5.0")) > 0) {
            throw new RuntimeException("評分必須在0-5之間");
        }
        
        if (review.getAvgCost() == null || review.getAvgCost() < 0) {
            throw new RuntimeException("平均消費金額必須大於0");
        }

        if (reviewRepository.existsByOrderId(review.getOrderMaster().getOrderId())) {
            throw new RuntimeException("此訂單已經評價過");
        }
    }

    private void validateReviewUpdate(ReviewVO review) {
        Timestamp deadline = new Timestamp(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
        if (review.getReviewDatetime().before(deadline)) {
            throw new RuntimeException("評論已超過可修改期限(7天)");
        }
    }
    
    public ReviewVO getReviewByOrderId(Integer orderId) {
    	return reviewRepository.getReviewByOrderId(orderId);
    }
    
    // this one is for update
    public ReviewVO getReviewById(Integer reviewId, HttpSession session) {
    	ReviewVO review = reviewRepository.findById(reviewId).orElse(null);
    	if (review == null) {
    		throw new ResourceNotFoundException("review id = " + reviewId + "not found.");
    	}
    	
    	// for restStars update
    	session.setAttribute("originReviewRating", review.getReviewRating());
    	
    	return review;
    }
    
    public ReviewVO setReviewBasicData(ReviewVO review, MemberVO member, RestVO rest, OrderMasterVO orderMaster) {
    	review.setMember(member);
    	review.setRest(rest);
    	review.setOrderMaster(orderMaster);
    	
    	return review;
    }
    
}