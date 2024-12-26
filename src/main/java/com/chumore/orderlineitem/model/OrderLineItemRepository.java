package com.chumore.orderlineitem.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chumore.orderitem.model.OrderItemVO;

public interface OrderLineItemRepository extends JpaRepository<OrderLineItemVO, Integer>{

	
	@Query("FROM OrderLineItemVO oli WHERE oli.orderItemId = :orderItemId")
	List<OrderLineItemVO> findOrderLineItemByOrderItemId(Integer orderItemId);
}
