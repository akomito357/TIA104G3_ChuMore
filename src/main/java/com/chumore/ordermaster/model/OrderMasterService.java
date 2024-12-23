package com.chumore.ordermaster.model;

import java.sql.Timestamp;
import java.util.List;

public class OrderMasterService implements OrderMasterService_interface{
	
	private OrderMasterDAO_interface dao;
	private Integer earnRatio = 100; // 消費X元得1點
	private Integer discountRatio = 10; // 1點抵X元
	
	public OrderMasterService(){
		dao = new OrderMasterJDBCDAO();
	}

	public OrderMasterVO addOrder(Integer orderTableId, Integer restId, Integer memberId, Integer orderStatus,
			Double subtotalPrice, Timestamp servedDatetime, Integer pointUsed,
			Timestamp checkoutDatetime) {
		
		OrderMasterVO order = new OrderMasterVO(); 
		
		order.setOrderTableId(orderTableId);
		order.setRestId(restId);
		order.setMemberId(memberId);
		order.setOrderStatus(orderStatus);
		order.setSubtotalPrice(subtotalPrice);
		
		// totalPrice = subtotalPrice - pointUsed * 10
		Double totalPrice = subtotalPrice - pointUsed * discountRatio;
		order.setTotalPrice(totalPrice);
		
		order.setServedDatetime(servedDatetime);
		
		// pointEarned = totalPrice / 100
		Double pointEarnedDouble = (totalPrice / earnRatio);
		Integer pointEarned = pointEarnedDouble.intValue();
		order.setPointEarned(pointEarned);
		
		order.setPointUsed(pointUsed);
		order.setCheckoutDatetime(checkoutDatetime);
		
		dao.insert(order);
		return order;
	}

	public OrderMasterVO updateOrder(Integer orderId, Integer orderTableId, Integer restId, Integer memberId,
			Integer orderStatus, Double subtotalPrice, Timestamp servedDateTime, 
			Integer pointUsed, Timestamp checkoutDatetime) {
		
		OrderMasterVO order = new OrderMasterVO();
		
		order.setOrderId(orderId);
		order.setOrderTableId(orderTableId);
		order.setRestId(restId);
		order.setMemberId(memberId);
		order.setOrderStatus(orderStatus);
		order.setSubtotalPrice(subtotalPrice);
		
		// totalPrice = subtotalPrice - pointUsed * 10
		Double totalPrice = subtotalPrice - pointUsed * discountRatio;
		order.setTotalPrice(totalPrice);
		
		order.setServedDatetime(servedDateTime);

		// pointEarned = totalPrice / 100
		Double pointEarnedDouble = (totalPrice / earnRatio);
		Integer pointEarned = pointEarnedDouble.intValue();
		order.setPointEarned(pointEarned);
		
		order.setPointUsed(pointUsed);
		order.setCheckoutDatetime(checkoutDatetime);
		
		dao.update(order);
		
		return order;
	}

	public OrderMasterVO getOneOrder(Integer orderId) {		
		return dao.getById(orderId);
	}

	public List<OrderMasterVO> getAllOrder() {
		return dao.getAll();
	}

	public void deleteOrder(Integer orderId) {
		dao.delete(orderId);
	}
}
