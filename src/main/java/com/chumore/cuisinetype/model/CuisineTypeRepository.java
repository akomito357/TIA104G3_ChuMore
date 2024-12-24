package com.chumore.cuisinetype.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CuisineTypeRepository extends JpaRepository<CuisineTypeVO, Integer>{
	
	@Query(value = "from CuisineTypeVO ct where ct.cuisineDescr like ?1")
	List<CuisineTypeVO> findByDescr(String cuisineTypeDescr);
	
}
