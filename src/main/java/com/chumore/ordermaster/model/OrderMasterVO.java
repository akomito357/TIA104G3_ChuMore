package com.chumore.ordermaster.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;

@Entity
@Table(name = "order_master")
public class OrderMasterVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;
	
	@ManyToOne
	@JoinColumn(name = "order_table_id", referencedColumnName = "order_table_id")
//	private OrderTableVO orderTable;
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
	
	@Column(name = "subtotal_price")
	private Double subtotalPrice;
	
	@Column(name = "total_price")
	private Double totalPrice;
	
	@Column(name = "served_datetime", columnDefinition = "DATETIME")
	private Timestamp servedDatetime;
	
	@Column(name = "point_earned")
	private Integer pointEarned;
	
	@Column(name = "point_used")
	private Integer pointUsed;
	
	@Column(name = "checkout_datetime", columnDefinition = "DATETIME")
	private Timestamp checkoutDatetime;
	
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
	
//	public Integer getRestId() {
//		return restId;
//	}
//	public void setRestId(Integer restId) {
//		this.restId = restId;
//	}
	
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

//	public Integer getMemberId() {
//		return memberId;
//	}

//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
	
	
	
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
	
	public Timestamp getServedDatetime() {
		return servedDatetime;
	}

	public void setServedDatetime(Timestamp servedDatetime) {
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

	public Timestamp getCheckoutDatetime() {
		return checkoutDatetime;
	}
	public void setCheckoutDatetime(Timestamp checkoutDatetime) {
		this.checkoutDatetime = checkoutDatetime;
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
