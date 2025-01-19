package com.chumore.ordermaster.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chumore.ordermaster.model.OrderMasterVO;


public class RestUnpaidDto {
	private Integer restId;
	private String servedDatetime;
	private Integer orderTableId;
	private String tableNumber;
	private BigDecimal subtotalPrice;
	private Integer orderId;
	private Integer orderStatus;
	
	public RestUnpaidDto() {
		super();
	}
	
	public RestUnpaidDto(OrderMasterVO orderMaster, String tableNumber) {
		
		setRestId(orderMaster.getRest().getRestId());
		
		LocalDateTime date = orderMaster.getServedDatetime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formatterDatetime = date.format(formatter);
		setServedDatetime(formatterDatetime);
		
//		setTableNumber(orderTable.getTableNumber());
		setTableNumber(tableNumber);
		
		
		
		setSubtotalPrice(orderMaster.getSubtotalPrice());
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

	public BigDecimal getSubtotalPrice() {
		return subtotalPrice;
	}

	public void setSubtotalPrice(BigDecimal subtotalPrice) {
		this.subtotalPrice = subtotalPrice;
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
