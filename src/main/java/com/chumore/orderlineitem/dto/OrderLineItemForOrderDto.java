package com.chumore.orderlineitem.dto;

import java.math.BigDecimal;

public class OrderLineItemForOrderDto {

	Integer productId;
	String productName;
	Integer count;
	BigDecimal originPriceForOne;
	
	public OrderLineItemForOrderDto() {
		
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getOriginPriceForOne() {
		return originPriceForOne;
	}

	public void setOriginPriceForOne(BigDecimal originPriceForOne) {
		this.originPriceForOne = originPriceForOne;
	}
	
}
