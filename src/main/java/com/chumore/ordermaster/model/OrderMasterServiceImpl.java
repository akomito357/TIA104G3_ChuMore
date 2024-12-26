package com.chumore.ordermaster.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.chumore.ordermaster.compositequery.OrderMasterCompositeQuery;

public class OrderMasterServiceImpl implements OrderMasterService{

	@Autowired
	OrderMasterRepository repository;
	
	@Autowired
	SessionFactory factory;
	
	@Override
	public OrderMasterVO getById(Integer orderId) {
		Optional<OrderMasterVO> optional = repository.findById(orderId);
		return optional.orElse(null);
	}

	@Override
	public List<OrderMasterVO> getAllOrder() {
		return repository.findAll();
	}

	@Override
	public List<OrderMasterVO> getAllOrder(Map<String, String[]> map) {
		return OrderMasterCompositeQuery.getAllC(map, factory.openSession());
	}

	@Override
	public void addOrderMaster(OrderMasterVO orderMaster) {
		repository.save(orderMaster);
	}

	@Override
	public void updateOrderMaster(OrderMasterVO orderMaster) {
		repository.save(orderMaster);
	}

	@Override
	public void deleteOrderMasterById(Integer orderId) {
		repository.deleteByOrderId(orderId);
	}

}
