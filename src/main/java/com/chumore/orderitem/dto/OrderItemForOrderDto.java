package com.chumore.orderitem.dto;

import java.util.List;

import com.chumore.orderlineitem.dto.OrderLineItemForOrderDto;

public class OrderItemForOrderDto {
	
	List<OrderLineItemForOrderDto> order;
	String memo;
	
	public OrderItemForOrderDto() {
	}

	public List<OrderLineItemForOrderDto> getOrder() {
		return order;
	}

	public void setOrder(List<OrderLineItemForOrderDto> order) {
		this.order = order;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
}
