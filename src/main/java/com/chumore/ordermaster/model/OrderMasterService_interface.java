package com.chumore.ordermaster.model;

import java.sql.Timestamp;
import java.util.List;

public interface OrderMasterService_interface {
	
	OrderMasterVO addOrder(
			Integer orderTableId, Integer restId, Integer memberId, 
			Integer orderStatus, Double subtotalPrice,  
			Timestamp servedDateTime, Integer pointUsed,
			Timestamp checkoutDatetime);
	
	OrderMasterVO updateOrder(Integer orderId, Integer orderTableId, Integer restId, 
			Integer memberId, Integer orderStatus, Double subtotalPrice, 
			 Timestamp servedDateTime, Integer pointUsed, Timestamp checkoutDatetime);
	
	OrderMasterVO getOneOrder(Integer orderId);
	
	void deleteOrder(Integer orderId);
	
	List<OrderMasterVO> getAllOrder();
	

}
