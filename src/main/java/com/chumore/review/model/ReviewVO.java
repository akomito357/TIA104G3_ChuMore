package com.chumore.review.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "review")
public class ReviewVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    
    @NotNull(message = "餐廳ID不能為空")
    @Column(name = "rest_id", nullable = false)
    private Integer restId;
    
    @NotNull(message = "會員ID不能為空")
    @Column(name = "member_id", nullable = false)
    private Integer memberId;
    
    @NotNull(message = "訂單ID不能為空")
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "product_id")
    private Integer productId;
    
    @NotBlank(message = "評論內容不能為空")
    @Column(name = "review_text", columnDefinition = "TEXT", nullable = false)
    private String reviewText;
    
    @NotNull(message = "評分不能為空")
    @DecimalMin(value = "0.0", message = "評分不能小於0")
    @DecimalMax(value = "5.0", message = "評分不能大於5")
    @Column(name = "review_rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal reviewRating;
    
    @NotNull(message = "平均消費不能為空")
    @Min(value = 0, message = "平均消費不能小於0")
    @Column(name = "avg_cost", nullable = false)
    private Integer avgCost;
    
    @Column(name = "review_datetime", nullable = false)
    private Timestamp reviewDatetime;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImageVO> reviewImages;

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
    
    public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public BigDecimal getReviewRating() {
		return reviewRating;
	}

	public void setReviewRating(BigDecimal reviewRating) {
		this.reviewRating = reviewRating;
	}

	public Integer getAvgCost() {
		return avgCost;
	}

	public void setAvgCost(Integer avgCost) {
		this.avgCost = avgCost;
	}

	public Timestamp getReviewDatetime() {
		return reviewDatetime;
	}

	public void setReviewDatetime(Timestamp reviewDatetime) {
		this.reviewDatetime = reviewDatetime;
	}

	public List<ReviewImageVO> getReviewImages() {
		return reviewImages;
	}

	public void setReviewImages(List<ReviewImageVO> reviewImages) {
		this.reviewImages = reviewImages;
	}


	public Integer getRestId() {
        return restId;
    }
    
    public void setRestId(Integer restId) {
        this.restId = restId;
    }
    
    
}