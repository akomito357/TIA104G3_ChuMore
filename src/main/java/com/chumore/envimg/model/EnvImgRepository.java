package com.chumore.envimg.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EnvImgRepository extends JpaRepository<EnvImgVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from env_img where env_img_id =?1", nativeQuery = true)
	void deleteByEnvImgId(int envImgId);

}
