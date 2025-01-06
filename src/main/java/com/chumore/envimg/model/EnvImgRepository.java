package com.chumore.envimg.model;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.ordertable.model.OrderTableVO;

@Repository
public interface EnvImgRepository extends JpaRepository<EnvImgVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from env_img where env_img_id =?1", nativeQuery = true)
	void deleteByEnvImgId(int envImgId);
	
	@Query("SELECT o FROM EnvImgVO o WHERE o.rest.restId = :restId")
	List<EnvImgVO> findByRestId(@Param("restId") Integer restId);
}
