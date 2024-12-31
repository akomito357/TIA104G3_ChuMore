package com.chumore.location.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<LocationVO, Integer>{

	@Query("select l.district from LocationVO l where l.city = ?1")
	List<String> findDistByCity(String city);
	
	@Query(value = "select l.city, l.district from LocationVO l where l.city = ?1")
	List<String[]> findCityAndDistByCity(String city);
	
	@Query(value = "select distinct l.city from LocationVO l")
	List<String> findCitys();
	
}
