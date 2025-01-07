package com.chumore.ordertable.model;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.rest.model.RestVO;

@Repository
public interface OrderTableRepository extends JpaRepository<OrderTableVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from order_table where env_img_id =?1", nativeQuery = true)
	void deleteByOrderTableId(int orderTableId);
	
	@Query("SELECT o FROM OrderTableVO o WHERE o.rest.restId = :restId")
	List<OrderTableVO> findByRestId(@Param("restId") Integer restId);
	
	Optional<OrderTableVO> findByRest_RestIdAndTableNumber(Integer restId, String tableNumber);

	@Query(value = "select ot.table_name form order_table ot where ot.order_table_id =:orderTableId", nativeQuery = true)
	String getTableNumberById(Integer orderTableId);
	
	@Query(value = "SELECT ot FROM OrderTableVO ot where ot.rest.restId = ?1 and ot.tableNumber = ?2")
    OrderTableVO findByRestIdAndNumber(Integer restId, String tableNumber);
}
