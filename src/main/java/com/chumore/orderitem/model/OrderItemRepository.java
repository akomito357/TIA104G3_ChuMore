package com.chumore.orderitem.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItemVO , Integer> {
	
	@Query("SELECT DISTINCT oi.orderId FROM OrderItemVO oi ORDER BY oi.orderId")
	List<Integer> getOrderIdList();
	
	@Query(value = "SELECT * FROM order_item oi WHERE oi.order_id = :orderId", nativeQuery = true)
	List<OrderItemVO> findByOrderId(Integer orderId);
	
}
