package com.chumore.ordermaster.model;

import java.util.List;
import java.util.Map;

public interface OrderMasterService {
	
	OrderMasterVO getById(Integer orderId);
	List<OrderMasterVO> getAllOrder();
	List<OrderMasterVO> getAllOrder(Map<String, String[]> map);
	
	void addOrderMaster(OrderMasterVO orderMaster);
	void updateOrderMaster(OrderMasterVO orderMaster);
	void deleteOrderMasterById(Integer orderId);

}
