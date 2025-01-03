package com.chumore.ordertable.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderTableRepository extends JpaRepository<OrderTableVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from order_table where env_img_id =?1", nativeQuery = true)
	void deleteByOrderTableId(int orderTableId);

}
