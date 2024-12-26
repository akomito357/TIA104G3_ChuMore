package com.chumore.menuimg.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MenuImgRepository extends JpaRepository<MenuImgVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from env_img where env_img_id =?1", nativeQuery = true)
	void deleteByMenuImgId(int menuImgId);

}
