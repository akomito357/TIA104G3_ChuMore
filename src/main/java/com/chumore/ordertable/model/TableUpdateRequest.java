package com.chumore.ordertable.model;

public class TableUpdateRequest {

	private String tableNumber;
	private String orderTableUrl;
	private Integer restId;

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getOrderTableUrl() {
		return orderTableUrl;
	}

	public void setOrderTableUrl(String orderTableUrl) {
		this.orderTableUrl = orderTableUrl;
	}

	public Integer getRestId() {
		return restId;
	}

	public void setRestId(Integer restId) {
		this.restId = restId;
	}

}
