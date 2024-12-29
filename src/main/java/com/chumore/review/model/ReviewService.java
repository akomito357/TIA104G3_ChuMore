package com.chumore.review.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;
import com.chumore.reviewimg.model.ReviewImageService;
import com.chumore.reviewimg.model.ReviewImageVO;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ReviewImageService reviewImageService;

    @Transactional
    public ReviewVO addReview(ReviewVO review) {
//        validateNewReview(review);
//        review.setReviewDatetime(new Timestamp(System.currentTimeMillis()));
        return reviewRepository.save(review);
    }

    @Transactional
    public ReviewVO updateReview(ReviewVO review) {
//        Optional<ReviewVO> existingReviewOpt = reviewRepository.findById(reviewId);
        
//        if (existingReviewOpt.isEmpty()) {
//            throw new RuntimeException("評論不存在");
//        }
//        
//        ReviewVO review = existingReviewOpt.get();
//        if (!review.getMember().equals(memberId)) {
//            throw new RuntimeException("無權限修改此評論");
//        }

//        validateReviewUpdate(existingReview);
        
//        review.setReviewText(updateRequest.getReviewText());
//        review.setReviewRating(updateRequest.getReviewRating());
//        review.setAvgCost(updateRequest.getAvgCost());
//        review.setProduct(updateRequest.getProduct());
        
        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Integer reviewId, Integer memberId) {
//        Optional<ReviewVO> reviewOpt = reviewRepository.findById(reviewId);
//        if (reviewOpt.isEmpty()) {
//            throw new RuntimeException("評論不存在");
//        }
//        ReviewVO review = reviewOpt.get();
//        if (!review.getMember().equals(memberId)) {
//            throw new RuntimeException("無權限刪除此評論");
//        }
        
    	if (reviewRepository.existsById(reviewId)){
    		reviewImageService.deleteAllByReviewId(reviewId);
            reviewRepository.deleteByReviewId(reviewId);
    	}
    	
    }

    public List<ReviewVO> getRestReviews(RestVO rest) {
    	return reviewRepository.getReviewFromRest(rest);
    	    	
//        if (rest == null) {
//            throw new RuntimeException("餐廳ID不能為空");
//        }
//        return reviewRepository.findByRestIdOrderByReviewDatetimeDesc(restId, pageable);
    }

    public List<ReviewVO> getMemberReviews(MemberVO member) {
//        if (memberId == null) {
//            throw new RuntimeException("會員ID不能為空");
//        }
        return reviewRepository.getReviewFromMember(member);
        /**  member記得加上相應的get set方法在service
         */
    }
    
    public ReviewVO getOneReviewById(Integer reviewId) {
    	Optional<ReviewVO> optional = reviewRepository.findById(reviewId);
    	return optional.orElse(null);
    }
    
    public Set<ReviewImageVO> getImgsByReviewId(Integer reviewId){
    	return getOneReviewById(reviewId).getReviewImages();
    }

    
//    public boolean existsByOrderId(Integer orderId) {
//        if (orderId == null) {
//            throw new RuntimeException("訂單ID不能為空");
//        }
//        return reviewRepository.existsByOrderId(orderId);
//    }
//
//    public BigDecimal calculateAverageRating(Integer restId) {
//        if (restId == null) {
//            throw new RuntimeException("餐廳ID不能為空");
//        }
//        return reviewRepository.calculateAverageRating(restId);
//    }
//
//    public Long getReviewCount(Integer restId) {
//        if (restId == null) {
//            throw new RuntimeException("餐廳ID不能為空");
//        }
//        return reviewRepository.countByRestId(restId);
//    }
    


//    private void validateNewReview(ReviewVO review) {
//        if (review == null) {
//            throw new RuntimeException("評論資料不能為空");
//        }
//
//        if (review.getReviewText() == null || review.getReviewText().trim().isEmpty()) {
//            throw new RuntimeException("評論內容不能為空");
//        }
//        
//        if (review.getReviewRating() == null || 
//            review.getReviewRating().compareTo(BigDecimal.ZERO) < 0 || 
//            review.getReviewRating().compareTo(new BigDecimal("5.0")) > 0) {
//            throw new RuntimeException("評分必須在0-5之間");
//        }
//        
//        if (review.getAvgCost() == null || review.getAvgCost() < 0) {
//            throw new RuntimeException("平均消費金額必須大於0");
//        }

//        if (reviewRepository.existsByOrderId(review.getOrderMaster())) {
//            throw new RuntimeException("此訂單已經評價過");
//        }
//    }

//    private void validateReviewUpdate(ReviewVO review) {
//        Timestamp deadline = new Timestamp(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
//        if (review.getReviewDatetime().before(deadline)) {
//            throw new RuntimeException("評論已超過可修改期限(7天)");
//        }
//    }
}