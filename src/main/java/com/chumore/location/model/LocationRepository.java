package com.chumore.location.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<LocationVO, Integer>{
	
	@Query(value = "select dist from LocationVO where city = ?1")
	List<LocationVO> findDistByCity(String city);
	
}
