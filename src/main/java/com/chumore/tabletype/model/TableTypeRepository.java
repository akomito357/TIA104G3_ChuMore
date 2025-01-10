package com.chumore.tabletype.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.ordertable.model.OrderTableVO;

public interface TableTypeRepository extends JpaRepository<TableTypeVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from table_type where table_type_id =?1", nativeQuery = true)
	void deleteByTableTypeId(int tableTypeId);
	
	@Query("SELECT o FROM TableTypeVO o WHERE o.rest.restId = :restId")
	List<TableTypeVO> findByRestId(@Param("restId") Integer restId);
}

