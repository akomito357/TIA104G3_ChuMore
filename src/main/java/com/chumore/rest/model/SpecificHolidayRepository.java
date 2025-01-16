package com.chumore.rest.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SpecificHolidayRepository extends JpaRepository<SpecificHolidayVO, Integer>{
	@Transactional
	@Modifying
	@Query(value = "delete from specific_holiday where specific_holiday_id =?1", nativeQuery = true)
	void deleteBySpecificHolidayId(int specificHolidayId);
	
	List<SpecificHolidayVO> findByRestId(Integer restId);
	
}