package com.chumore.reviewimg.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImageVO, Integer> {

    @Query("SELECT ri FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId ORDER BY ri.uploadDatetime DESC")
    List<ReviewImageVO> findByReviewOrderByUploadDatetimeDesc(@Param("reviewId") Integer reviewId);

    @Query("SELECT COUNT(ri) FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId")
    Long countByReviewId(@Param("reviewId") Integer reviewId);

    @Query("DELETE FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId")
    void deleteByReview_ReviewId(@Param("reviewId") Integer reviewId);
    
    @Transactional
    @Modifying
    @Query(value = "delete from review_image where review_id = ?1", nativeQuery = true)
    void deleteByReviewIdNative(Integer reviewId);
    
    @Transactional
    @Modifying
    @Query(value = "delete from review_image where review_img_id = ?1", nativeQuery = true)
    void deleteByReviewImageId(Integer reviewImageId);
    
    
}