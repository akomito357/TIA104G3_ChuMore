package com.chumore.rest.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RestRepository extends JpaRepository<RestVO, Integer>{
	
	@Query(value = "from RestVO r where r.city = ?1 and r.district = ?2")
	List<RestVO> findByCityAndDist(String city, String district);
	

}
