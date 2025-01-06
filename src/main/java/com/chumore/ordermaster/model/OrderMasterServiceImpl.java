package com.chumore.ordermaster.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.orderitem.dto.OrderItemForOrderDto;
import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.orderlineitem.dto.OrderLineItemForOrderDto;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.orderlineitem.model.OrderLineItem_Service;
import com.chumore.ordermaster.compositequery.OrderMasterCompositeQuery;
import com.chumore.ordermaster.dto.RestDiningDto;
import com.chumore.product.model.Product_Service;
import com.chumore.review.model.ReviewVO;
import com.chumore.usepoints.model.UsePointsVO;

@Transactional
@Service("OrderMasterService")
public class OrderMasterServiceImpl implements OrderMasterService {

	@Autowired
	OrderMasterRepository repository;
	
	@Autowired
	OrderItem_Service orderItemSvc;
	
	@Autowired
	OrderLineItem_Service orderLineItemSvc;
	
	@Autowired
	Product_Service productSvc;

	@Autowired
	SessionFactory factory;

	@Override
	@Transactional(readOnly = true)
	public OrderMasterVO getOneById(Integer orderId) {
		Optional<OrderMasterVO> optional = repository.findById(orderId);
		return optional.orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderMasterVO> getAllOrder() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
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

	@Transactional(readOnly = true)
	public Set<UsePointsVO> getUsePointsByOrderId(Integer orderId) {
		return getOneById(orderId).getUsePoints();
	}

//	public Set<OrderItemVO> getOrderItemByOrderId(Integer orderId){
//		return getOneById(orderId).getOrderItems();
//	}

	@Transactional(readOnly = true)
	public Set<ReviewVO> getReviewByOrderId(Integer orderId) {
		return getOneById(orderId).getReviews();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderMasterVO> getByMemberId(Integer memberId) {
		return repository.getByMemberId(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrderMasterVO> findByMemberId(Integer memberId, Pageable pageable) {
		return repository.findByMemberId(memberId, pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<RestDiningDto>findOrderByRestId(Integer restId, Pageable pageable){
		return repository.findOrderByRestId(restId, pageable);
	}
	
	
	public void submitOrder(OrderItemForOrderDto item, HttpSession session) {
		String memo = item.getMemo();
		System.out.println(memo);
		
		// 建立新點餐
		OrderItemVO orderItem = new OrderItemVO();
		orderItem.setOrderId((Integer)session.getAttribute("orderId"));
		orderItem.setMemo(memo);
		orderItem.setCreatedDatetime(new Timestamp(System.currentTimeMillis()));
		orderItem.setUpdatedDatetime(new Timestamp(System.currentTimeMillis()));
		orderItem = orderItemSvc.addOrUpdateOrderItem(orderItem);
		
		// 獲取點餐細項並將資料放入
		Integer orderItemId = orderItem.getOrderItemId();
		List<OrderLineItemForOrderDto> orders = item.getOrder();
		BigDecimal thisSubTotalPrice = new BigDecimal("0");
		
		for (OrderLineItemForOrderDto lineDto : orders) {
			OrderLineItemVO orderLineItem = new OrderLineItemVO();
			
			orderLineItem.setOrderItemId(orderItemId);
			orderLineItem.setProduct(productSvc.getProductById(lineDto.getProductId()));
			
			Integer count = lineDto.getCount();
			BigDecimal priceForOne = lineDto.getOrigPriceForOne();
			
			orderLineItem.setQuantity(count);
			orderLineItem.setPrice(priceForOne);
			
//			priceForOne.multiply(BigDecimal.valueOf(count));
			thisSubTotalPrice = thisSubTotalPrice.add(priceForOne.multiply(BigDecimal.valueOf(count)));
			
			System.out.println(orderLineItem);
			orderLineItemSvc.addOrderLineItem(orderLineItem);
		}
		
		// 更新orderMaster的小計
		OrderMasterVO order = getOneById((Integer)session.getAttribute("orderId"));
		BigDecimal subtotalPrice = order.getSubtotalPrice();
		subtotalPrice = subtotalPrice.add(thisSubTotalPrice);
		order.setSubtotalPrice(subtotalPrice);
		updateOrderMaster(order);
		
		
	}

}
