package com.chumore.review.controller;

import com.chumore.review.model.ReviewService;
import com.chumore.review.model.ReviewVO;
import com.chumore.reviewimg.model.ReviewImageService;
import com.chumore.reviewimg.model.ReviewImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewPublicController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewImageService reviewImageService;

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

    @GetMapping("/image/{reviewImgId}")
    @ResponseBody
    public ResponseEntity<byte[]> getOneRevImg(@PathVariable Integer reviewImgId) {
        ReviewImageVO reviewImage = reviewImageService.getOneRevImg(reviewImgId);
//		System.out.println(reviewImage.getReviewImage());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(reviewImage.getReviewImage());
    }

}
