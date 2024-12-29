package com.chumore.favrest.model;

import java.util.List;

import com.chumore.member.model.MemberVO;

public interface FavRestService {
	
	void addFavRest(FavRestVO favRest) ;
	void deleteFavRest(Integer favRestId);
	List<String> getRestByMember(MemberVO member);
	FavRestVO getOneFavRestById(Integer favRestId);
	

}
