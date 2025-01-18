package com.chumore.ordermaster.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.chumore.orderitem.dto.OrderItemForOrderDto;

public interface OrderMasterService {
	
	OrderMasterVO getOneById(Integer orderId);
	List<OrderMasterVO> getAllOrder();
	List<OrderMasterVO> getAllOrder(Map<String, String[]> map);
	
	OrderMasterVO addOrderMaster(OrderMasterVO orderMaster);
	OrderMasterVO updateOrderMaster(OrderMasterVO orderMaster);
	Integer deleteOrderMasterById(Integer orderId);
	
	List<OrderMasterVO> getByMemberId(Integer memberId);
	Page<OrderMasterVO> findByMemberId(Integer memberId,  Pageable pageable);
	
	void submitOrder(OrderItemForOrderDto item, HttpSession session);
	BigDecimal calcDiscount(Integer usePoint);
	BigDecimal calcTotalPrice(Integer orderId, BigDecimal discounts);

}