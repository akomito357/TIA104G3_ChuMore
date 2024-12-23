package com.chumore.tabletype.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TableTypeRepository extends JpaRepository<TableTypeVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from table_type where table_type_id =?1", nativeQuery = true)
	void deleteByTableTypeId(int tableTypeId);
}
