package com.chumore.ordermaster.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.ordermaster.compositequery.OrderMasterCompositeQuery;
import com.chumore.review.model.ReviewVO;
import com.chumore.usepoints.model.UsePointsVO;

@Service("OrderMasterService")
public class OrderMasterServiceImpl implements OrderMasterService{

	@Autowired
	OrderMasterRepository repository;
	
	@Autowired
	SessionFactory factory;
	
	@Override
	public OrderMasterVO getOneById(Integer orderId) {
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
	public OrderMasterVO addOrderMaster(OrderMasterVO orderMaster) {
		return repository.save(orderMaster);
	}

	@Override
	public OrderMasterVO updateOrderMaster(OrderMasterVO orderMaster) {
		return repository.save(orderMaster);
	}

	@Override
	public Integer deleteOrderMasterById(Integer orderId) {
		if (repository.existsById(orderId)) {
			repository.deleteByOrderId(orderId);
			return 1;
		} else {
			return -1;
		}
	}
	
	public Set<UsePointsVO> getUsePointsByOrderId(Integer orderId){
		return getOneById(orderId).getUsePoints();
	}
	
//	public Set<OrderItemVO> getOrderItemByOrderId(Integer orderId){
//		return getOneById(orderId).getOrderItems();
//	}
	
	public Set<ReviewVO> getReviewByOrderId(Integer orderId){
		return getOneById(orderId).getReviews();
	}
	
	

}
