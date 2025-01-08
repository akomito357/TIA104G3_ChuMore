package com.chumore.menuimg.model;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.envimg.model.EnvImgVO;

@Repository
public interface MenuImgRepository extends JpaRepository<MenuImgVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from menu_img where menu_img_id =?1", nativeQuery = true)
	void deleteByMenuImgId(int menuImgId);
	
	@Query("SELECT o FROM MenuImgVO o WHERE o.rest.restId = :restId")
	List <MenuImgVO> findByRestId(@Param("restId") Integer restId);
}
