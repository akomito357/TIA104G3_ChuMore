package com.chumore.review.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewVO, Integer> {

    @Query("SELECT r FROM ReviewVO r WHERE r.rest.restId = :restId ORDER BY r.reviewDatetime DESC")
    Page<ReviewVO> findByRestIdOrderByReviewDatetimeDesc(@Param("restId") Integer restId, Pageable pageable);

    @Query("SELECT r FROM ReviewVO r WHERE r.member.memberId = :memberId ORDER BY r.reviewDatetime DESC")
    List<ReviewVO> findByMemberIdOrderByReviewDatetimeDesc(@Param("memberId") Integer memberId);

    @Query("SELECT COUNT(r) > 0 FROM ReviewVO r WHERE r.orderMaster.orderId = :orderId")
    boolean existsByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT COUNT(r) FROM ReviewVO r WHERE r.rest.restId = :restId")
    Long countByRestId(@Param("restId") Integer restId);

    @Query("SELECT AVG(r.reviewRating) FROM ReviewVO r WHERE r.rest.restId = :restId")
    BigDecimal calculateAverageRating(@Param("restId") Integer restId);

    @Query("SELECT AVG(r.avgCost) FROM ReviewVO r WHERE r.rest.restId= :restId")
    BigDecimal calculateAverageCostByRestId(@Param("restId") Integer restId);

    @Query("SELECT r FROM ReviewVO r WHERE r.orderMaster.orderId = :orderId")
    ReviewVO getReviewByOrderId(@Param("orderId") Integer orderId);
    
    @Transactional
	@Modifying
    @Query(value = "DELETE FROM review WHERE review_id = ?1", nativeQuery = true)
    void deleteByReviewId(Integer reviewId);


}