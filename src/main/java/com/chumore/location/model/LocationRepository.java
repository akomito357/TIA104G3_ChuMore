package com.chumore.location.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<LocationVO, Integer>{

	@Query("select l.district from LocationVO l where l.city = ?1")
	List<String> findDistByCity(String city);
<<<<<<< Upstream, based on branch 'master' of https://github.com/akomito357/TIA104G3_ChuMore.git
	
	@Query(value = "select l.city from LocationVO l where l.city = ?1")
	List<String> findCity(String city);
=======
>>>>>>> 66f000b fix: resolve the conflict due to configuration of hibernate and spring data jpa
	
	@Query(value = "select l.city from LocationVO l where l.city = ?1")
	List<String> findCity(String city);
	
}
