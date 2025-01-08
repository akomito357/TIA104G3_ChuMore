package com.chumore.ordermaster.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chumore.ordermaster.model.OrderMasterVO;
import com.fasterxml.jackson.annotation.JsonFormat;

public class RestDiningDto {
	private Integer restId;
	private String servedDatetime;
	private Integer orderTableId;
	private String tableNumber;
	private String memberName;
	private BigDecimal totalPrice;
	private Integer orderId;
	private Integer orderStatus;
	
	public RestDiningDto() {
		super();
	}
	
	public RestDiningDto(OrderMasterVO orderMaster, String tableNumber) {
		
		setRestId(orderMaster.getRest().getRestId());
		
		LocalDateTime date = orderMaster.getServedDatetime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formatterDatetime = date.format(formatter);
		setServedDatetime(formatterDatetime);
		
//		setTableNumber(orderTable.getTableNumber());
		setTableNumber(tableNumber);
		
		if(orderMaster.getMember()!= null) {
			setMemberName(orderMaster.getMember().getMemberName());
		}
		
		setTotalPrice(orderMaster.getTotalPrice());
		setOrderId(orderMaster.getOrderId());
		setOrderStatus(orderMaster.getOrderStatus());
	}
	
	
	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	public String getServedDatetime() {
		return servedDatetime;
	}
	public void setServedDatetime(String servedDatetime) {
		this.servedDatetime = servedDatetime;
	}
	
	public Integer getOrderTableId() {
		return orderTableId;
	}

	public void setOrderTableId(Integer orderTableId) {
		this.orderTableId = orderTableId;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
}
