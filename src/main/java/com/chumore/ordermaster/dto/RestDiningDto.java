package com.chumore.ordermaster.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordertable.model.OrderTableVO;

public class RestDiningDto {
	private Integer restId;
	private String servedDatetime;
	private String tableNumber;
	private String memberName;
	private Double totalPrice;
	
	public RestDiningDto() {
		super();
	}
	
	public RestDiningDto(OrderMasterVO orderMaster, OrderTableVO orderTable) {
		
		setRestId(orderMaster.getRest().getRestId());
		LocalDateTime date = orderMaster.getServedDatetime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		String formatterDatetime = date.format(formatter);
		setServedDatetime(formatterDatetime);
		setTableNumber(orderTable.getTableNumber());
		
		if(orderMaster.getMember()!= null) {
			setMemberName(orderMaster.getMember().getMemberName());
		}
		
		setTotalPrice(orderMaster.getTotalPrice());
		
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

	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}