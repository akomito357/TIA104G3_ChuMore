package com.chumore.reviewimg.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImageVO, Integer> {

    @Query("SELECT ri FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId ORDER BY ri.uploadDatetime DESC")
    List<ReviewImageVO> findByReviewOrderByUploadDatetimeDesc(@Param("reviewId") Integer reviewId);

    @Query("SELECT COUNT(ri) FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId")
    Long countByReviewId(@Param("reviewId") Integer reviewId);

    @Query("DELETE FROM ReviewImageVO ri WHERE ri.review.reviewId = :reviewId")
    void deleteByReview_ReviewId(@Param("reviewId") Integer reviewId);
}