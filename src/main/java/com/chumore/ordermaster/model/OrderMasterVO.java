package com.chumore.ordermaster.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

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
import javax.validation.constraints.NotEmpty;

import com.chumore.member.model.MemberVO;
import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.rest.model.RestVO;
import com.chumore.review.model.ReviewVO;
import com.chumore.usepoints.model.UsePointsVO;

@Entity
@Table(name = "order_master")
public class OrderMasterVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;
	
//	@ManyToOne
//	@JoinColumn(name = "order_table_id", referencedColumnName = "order_table_id")
//	private OrderTableVO orderTable;
	@Column(name = "order_table_id")
	@NotEmpty(message = "點餐桌位：請填寫點餐桌位")
	private Integer orderTableId;
	
	@ManyToOne
	@JoinColumn(name = "rest_id", referencedColumnName = "rest_id")
	private RestVO rest;
//	private Integer restId;
	
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private MemberVO member;
//	private Integer memberId;
	
	@Column(name = "order_status", columnDefinition = "TINYINT")
	private Integer orderStatus;
	
	@Column(name = "subtotal_price", columnDefinition = "DECIMAL")
	private Double subtotalPrice;
	
	@Column(name = "total_price", columnDefinition = "DECIMAL")
	private Double totalPrice;
	
	@Column(name = "served_datetime", columnDefinition = "DATETIME")
	private LocalDateTime servedDatetime;
	
	@Column(name = "point_earned")
	private Integer pointEarned;
	
	@Column(name = "point_used")
	private Integer pointUsed;
	
	@Column(name = "checkout_datetime", columnDefinition = "DATETIME")
	private LocalDateTime checkoutDatetime;
	
	@OneToMany(mappedBy = "orderMaster", cascade = CascadeType.ALL)
	private Set<UsePointsVO> usePoints;
	
//	@OneToMany(mappedBy = "orderMaster", cascade = CascadeType.ALL)
//	private Set<OrderItemVO> orderItems;
	
	@OneToMany(mappedBy = "orderMaster", cascade = CascadeType.ALL)
	private Set<ReviewVO> reviews;
	
	public OrderMasterVO() {
		
	}
	
	public Integer getOrderId(){
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
//	public OrderTableVO getOrderTable() {
//		return orderTable;
//	}
//
//	public void setOrderTable(OrderTableVO orderTable) {
//		this.orderTable = orderTable;
//	}

	public Integer getOrderTableId() {
		return orderTableId;
	}
	public void setOrderTableId(Integer orderTableId) {
		this.orderTableId = orderTableId;
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
	
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public Double getSubtotalPrice() {
		return subtotalPrice;
	}
	public void setSubtotalPrice(Double subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
	}
	
	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public LocalDateTime getServedDatetime() {
		return servedDatetime;
	}

	public void setServedDatetime(LocalDateTime servedDatetime) {
		this.servedDatetime = servedDatetime;
	}
	
	public Integer getPointEarned() {
		return pointEarned;
	}

	public void setPointEarned(Integer pointEarned) {
		this.pointEarned = pointEarned;
	}

	public Integer getPointUsed() {
		return pointUsed;
	}

	public void setPointUsed(Integer pointUsed) {
		this.pointUsed = pointUsed;
	}

	public LocalDateTime getCheckoutDatetime() {
		return checkoutDatetime;
	}
	public void setCheckoutDatetime(LocalDateTime checkoutDatetime) {
		this.checkoutDatetime = checkoutDatetime;
	}
	
	
	
	public Set<UsePointsVO> getUsePoints() {
		return usePoints;
	}

	public void setUsePoints(Set<UsePointsVO> usePoints) {
		this.usePoints = usePoints;
	}

//	public Set<OrderItemVO> getOrderItems() {
//		return orderItems;
//	}
//
//	public void setOrderItems(Set<OrderItemVO> orderItems) {
//		this.orderItems = orderItems;
//	}

	public Set<ReviewVO> getReviews() {
		return reviews;
	}

	public void setReviews(Set<ReviewVO> reviews) {
		this.reviews = reviews;
	}

	public String toString() {
		return "[order_id = " + orderId 
				+ ", order_table_id = " + orderTableId 
				+ ", rest_id = " + rest
				+ ", member_id = " + member
				+ ", order_status = " + orderStatus
				+ ", subtotal_price = " + subtotalPrice
				+ ", total_price = " + totalPrice
				+ ", served_datetime = " + servedDatetime
				+ ", point_earned = " + pointEarned
				+ ", point_used = " + pointUsed
				+ ", checkout_datetime = " + checkoutDatetime
				+ "]";
	}

}
