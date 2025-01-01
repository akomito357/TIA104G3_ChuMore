package com.chumore.ordermaster.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OrderMasterRepository extends JpaRepository<OrderMasterVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from order_master where order_id = ?1", nativeQuery = true)
	void deleteByOrderId(Integer orderId);
	
	@Query(value= "SELECT * FROM order_master om WHERE om.member_id = ?", nativeQuery = true)
	List<OrderMasterVO> getByMemberId(Integer memberId);
}
