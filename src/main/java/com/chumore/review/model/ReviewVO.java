package com.chumore.review.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.chumore.member.model.MemberVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.product.model.ProductVO;
import com.chumore.rest.model.RestVO;
import com.chumore.reviewimg.model.ReviewImageVO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "review")
public class ReviewVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    
	//  @Column(name = "rest_id", nullable = false)
	//  private Integer restId;
	  @ManyToOne
	  @JoinColumn(name = "rest_id", referencedColumnName = "rest_id")
	  @NotNull(message = "餐廳ID不能為空")
	  @JsonBackReference("review-rest")
	  private RestVO rest;
	  
	  @ManyToOne
	  @JoinColumn(name = "member_id", referencedColumnName = "member_id")
	  @NotNull(message = "會員ID不能為空")
	  @JsonBackReference("member-review")
	  private MemberVO member;
	//  @Column(name = "member_id", nullable = false)
	//  private Integer memberId;
	  
	  @ManyToOne
	  @JoinColumn(name = "order_id", referencedColumnName = "order_id")
	  @NotNull(message = "訂單ID不能為空")
	  @JsonBackReference("order-review")
	  private OrderMasterVO orderMaster;
	//  @Column(name = "order_id", nullable = false)
	//  private Integer orderId;
	  
	//  @Column(name = "product_id")
	//  private Integer productId;
	  @ManyToOne
	  @JoinColumn(name = "product_id", referencedColumnName = "product_id")
//	  @JsonBackReference("product-review")
	  private ProductVO product;
	    
//    @NotBlank(message = "評論內容不能為空")
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
    @JsonManagedReference("review-reviewImg")
    private List<ReviewImageVO> reviewImages;

    // Getters and Setters
    public Integer getReviewId() {
        return reviewId;
    }
    
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
    
//    public Integer getMemberId() {
//		return memberId;
//	}
//
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
//
//	public Integer getOrderId() {
//		return orderId;
//	}
//
//	public void setOrderId(Integer orderId) {
//		this.orderId = orderId;
//	}
//
//	public Integer getProductId() {
//		return productId;
//	}
//
//	public void setProductId(Integer productId) {
//		this.productId = productId;
//	}

    
    
	public String getReviewText() {
		return reviewText;
	}

	public RestVO getRest() {
		return rest;
	}

	public void setRest(RestVO rest) {
		this.rest = rest;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public OrderMasterVO getOrderMaster() {
		return orderMaster;
	}

	public void setOrderMaster(OrderMasterVO orderMaster) {
		this.orderMaster = orderMaster;
	}

	public ProductVO getProduct() {
		return product;
	}

	public void setProduct(ProductVO product) {
		this.product = product;
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
	
	public String getRestName() {
		return rest.getRestName();
	}
	
	public String getFormattedServedDatetime() {
		return orderMaster.getFormattedServedDatetime();
	}
	
	public String getFormattedReviewDatetime() {
		if (reviewDatetime != null) {			
			return reviewDatetime.toLocalDateTime().format(FORMATTER);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "ReviewVO [reviewId=" + reviewId + ", rest=" + rest + ", member=" + member + ", orderMaster="
				+ orderMaster + ", product=" + product + ", reviewText=" + reviewText + ", reviewRating=" + reviewRating
				+ ", avgCost=" + avgCost + ", reviewDatetime=" + reviewDatetime + ", reviewImages=" + reviewImages
				+ "]";
	}

}