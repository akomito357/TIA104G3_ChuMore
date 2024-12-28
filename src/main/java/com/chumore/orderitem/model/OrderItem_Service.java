package com.chumore.orderitem.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderItemSvc")
public class OrderItem_Service {

	@Autowired
	OrderItemRepository repository;

	public OrderItemVO addOrUpdateOrderItem(OrderItemVO orderItem) throws Exception {
		if (orderItem.getOrderId() == null) {
			throw new IllegalArgumentException("ORDER ID CANNOT BE NULL");
		}
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		if (orderItem.getOrderItemId() == null) {

			orderItem.setCreatedDatetime(now);
		}
		OrderItemVO existingOrderItem = repository.findById(orderItem.getOrderItemId()).orElse(null);
		
		if(existingOrderItem != null) {
			orderItem.setCreatedDatetime(existingOrderItem.getCreatedDatetime());
		} else {
			throw new Exception("ORDER ITEM NOT FOUND");
		}
		orderItem.setUpdatedDatetime(now);
		return repository.save(orderItem);
	}

	
	public Integer deleteOrderItem(Integer orderItemId) {
		if (repository.existsById(orderItemId)) {
			repository.deleteById(orderItemId);
			return 1;
		}
		return -1;
	}

	public List<OrderItemVO> getAll() {
		return repository.findAll();
	}

	public OrderItemVO getOrderItemListByOrderItemId(Integer orderItemId) {
		Optional<OrderItemVO> optional = repository.findById(orderItemId);
		return optional.orElse(null);
	}

	public List<OrderItemVO> getOrderItemListByOrderId(Integer orderId) {
		return repository.findByOrderId(orderId);
	}
	
	public List<Integer> findAllDistinctOrderIds(){
		return repository.getOrderIdList();
	}
}
