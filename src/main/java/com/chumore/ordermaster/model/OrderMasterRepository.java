package com.chumore.ordermaster.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.ordermaster.dto.RestDiningDto;

public interface OrderMasterRepository extends JpaRepository<OrderMasterVO, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from order_master where order_id = ?1", nativeQuery = true)
	OrderMasterVO deleteByOrderId(Integer orderId);
	
	@Query(value= "SELECT * FROM order_master om WHERE om.member_id = ?", nativeQuery = true)
	List<OrderMasterVO> getByMemberId(Integer memberId);
    
	
    @Query(value= "SELECT * FROM order_master om WHERE om.member_id = ?", 
            countQuery = "SELECT COUNT(*) FROM order_master om WHERE om.member_id = :memberId"
            , nativeQuery = true)
    Page<OrderMasterVO> findByMemberId(Integer memberId, Pageable pageable);
    
    @Query(value= "SELECT om.served_datetime as servedDatetime, ot.table_number as tableNumber, "+ 
    		"m.member_name as memberName, om.total_price as totalPrice "+
    		"FROM order_master om "+
    		"LEFT JOIN order_table ot on om.order_table_id = ot.order_table_id "+
    		"LEFT JOIN member m on om.member_id = m.member_id "+
    		"WHERE om.rest_id = :restId",
		    countQuery = "SELECT COUNT(*) FROM order_master om WHERE om.rest_id = :restId"
		    , nativeQuery = true)
    Page<RestDiningDto> findOrderByRestId(Integer restId, Pageable pageable);
}
