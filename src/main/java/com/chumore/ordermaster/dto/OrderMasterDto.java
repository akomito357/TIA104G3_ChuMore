package com.chumore.ordermaster.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.review.model.ReviewVO;

public class OrderMasterDto {
	
	private String restName;
	private String servedDatetime;
	private Double totalPrice;
	private Integer orderId;
	private Integer reviewId;
	private Integer pointEarned;
	
	
	public OrderMasterDto(OrderMasterVO data, ReviewVO review) {
		setRestName(data.getRest().getRestName());
		
		LocalDateTime time = data.getServedDatetime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formattedDate = time.format(formatter);
		setServedDatetime(formattedDate);
		
		setTotalPrice(data.getTotalPrice());
		setOrderId(data.getOrderId());
		
		if(review != null) {
			setReviewId(review.getReviewId());			
		}
		
		setPointEarned(data.getPointEarned());
		
	}
	public String getRestName() {
		return restName;
	}
	public void setRestName(String restName) {
		this.restName = restName;
	}
	public String getServedDatetime() {
		return servedDatetime;
	}
	public void setServedDatetime(String servedDatetime) {
		this.servedDatetime = servedDatetime;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getReviewId() {
		return reviewId;
	}
	public void setReviewId(Integer reviewId) {
		this.reviewId = reviewId;
	}
	public Integer getPointEarned() {
		return pointEarned;
	}
	public void setPointEarned(Integer pointEarned) {
		this.pointEarned = pointEarned;
	}
	
	

}
