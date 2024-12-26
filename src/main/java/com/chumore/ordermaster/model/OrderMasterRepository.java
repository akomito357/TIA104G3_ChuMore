package com.chumore.ordermaster.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OrderMasterRepository extends JpaRepository<OrderMasterVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from order_master where order_id = ?1", nativeQuery = true)
	void deleteByOrderId(Integer orderId);
	
}
