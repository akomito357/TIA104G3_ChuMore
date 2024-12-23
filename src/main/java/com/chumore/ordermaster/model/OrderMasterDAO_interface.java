package com.chumore.ordermaster.model;

import java.util.List;

public interface OrderMasterDAO_interface {
	public void insert(OrderMasterVO orderMasterVO);
	public void update(OrderMasterVO orderMasterVO);
	public void delete(Integer orderId);
	public OrderMasterVO getById(Integer orderId);
	public List<OrderMasterVO> getAll();
}
