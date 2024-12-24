package com.chumore.favrest.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.member.model.MemberVO;

public interface FavRestRepository extends JpaRepository<FavRestVO, Integer> {

	@Query(value = "SELECT f.rest FROM FavRestVO f WHERE f.member = ?1")
	List<String> findRestByMember(MemberVO member);	
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM fav_rest WHERE fav_rest_id = ?1", nativeQuery = true)
	void deleteByFavRestId(Integer favRestId);
	
}
