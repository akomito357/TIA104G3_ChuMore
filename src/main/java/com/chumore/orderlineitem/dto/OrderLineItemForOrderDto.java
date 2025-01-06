package com.chumore.orderlineitem.dto;

import java.math.BigDecimal;

public class OrderLineItemForOrderDto {

	Integer productId;
	String productName;
	Integer count;
	BigDecimal origPriceForOne;
	
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

	public BigDecimal getOrigPriceForOne() {
		return origPriceForOne;
	}

	public void setOrigPriceForOne(BigDecimal origPriceForOne) {
		this.origPriceForOne = origPriceForOne;
	}
	
}
