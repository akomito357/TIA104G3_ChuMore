package com.chumore.orderlineitem.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.orderitem.model.OrderItemRepository;
import com.chumore.product.model.ProductRepository;

@Service("orderLineItemSvc")
public class OrderLineItem_Service {

	@Autowired
	OrderLineItemRepository repository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	public OrderLineItemVO addOrderLineItem(OrderLineItemVO orderLineItem) {
//		ProductVO productId = orderLineItem.getProduct();
//		OrderItemVO orderItemId = orderLineItem.getOrderItem();
		
//		orderLineItem.setOrderItem(orderItemId);
//		orderLineItem.setProduct(productId);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		orderLineItem.setCreatedDatetime(now);
		orderLineItem.setUpdatedDatetime(now);

//		if(orderLineItem.getProductVO()!=null) {
//			orderLineItem.setProductVO(orderLineItem.getProductVO().getProductId());
//		}
		return repository.save(orderLineItem);
	}

	public OrderLineItemVO updateOrderLineItem(OrderLineItemVO orderLineItem) {
		if (orderLineItem.getOrderLineItemId() == null) {
			throw new IllegalArgumentException("ORDER LINE ITEM ID CANNOT BE NULL");
		} else {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			OrderLineItemVO existingOrderLineItem = repository.findById(orderLineItem.getOrderLineItemId()).orElse(null);
			if(existingOrderLineItem != null) {
				orderLineItem.setOrderItemId(existingOrderLineItem.getOrderItemId());
				orderLineItem.setCreatedDatetime(existingOrderLineItem.getCreatedDatetime());
			}
			orderLineItem.setUpdatedDatetime(now);
			return repository.save(orderLineItem);
			
		}
	}

	public Integer deleteOrderLineItem(Integer orderLineItemId) {
		if (repository.existsById(orderLineItemId)) {
			repository.deleteById(orderLineItemId);
			return 1;
		}
		return -1;
	}

//	public List<OrderLineItemVO> getAll(){
//		return repository.findAll();
//	}

	public List<OrderLineItemVO> getOrderLineItemByOrderItemId(Integer orderItemId) {
		return repository.findOrderLineItemByOrderItemId(orderItemId);
	}

}
