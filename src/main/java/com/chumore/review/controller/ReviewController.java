package com.chumore.review.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.review.model.ReviewService;
import com.chumore.review.model.ReviewVO;
import com.chumore.reviewimg.model.ReviewImageService;
import com.chumore.reviewimg.model.ReviewImageVO;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewImageService reviewImageService;

    /**
     * 獲取訂單資訊用於評論頁面初始化
     * 對應前端：用餐資訊區域的自動填充
     */
    @GetMapping("/order/{orderId}")
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
    public ResponseEntity<?> createReview(
            @RequestHeader("Member-Id") Integer memberId,
            @Valid @RequestBody ReviewVO review,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos) {
        try {
            review.setMemberId(memberId);
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
    public ResponseEntity<?> getRestaurantDishes(@PathVariable Integer restId) {
        try {
            // 需要與Restaurant服務整合，獲取菜品列表
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
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(
            @PathVariable Integer imageId,
            @RequestHeader("Member-Id") Integer memberId) {
        try {
            reviewImageService.deleteImage(imageId, memberId);
            return ResponseEntity.ok("圖片已成功刪除");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}