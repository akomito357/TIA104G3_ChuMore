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
	
	public Set<UsePointsVO> getUsePointsByOrderId(Integer orderId){
		return getOneById(orderId).getUsePoints();
	}
	
//	public Set<OrderItemVO> getOrderItemByOrderId(Integer orderId){
//		return getOneById(orderId).getOrderItems();
//	}
	
	public Set<ReviewVO> getReviewByOrderId(Integer orderId){
		return getOneById(orderId).getReviews();
	}
	
	

	@Override
	public List<OrderMasterVO> getByMemberId(Integer memberId) {
		return repository.getByMemberId(memberId);
	}

}
