package com.chumore.favrest.model;

import java.util.List;

import com.chumore.member.model.MemberVO;

public interface FavRestService {
	
	FavRestVO addFavRest(FavRestVO favRest) ;
	Integer deleteFavRest(Integer favRestId);
	List<FavRestVO> getFavRestByMember(MemberVO member);
	FavRestVO getOneFavRestById(Integer favRestId);
	

}
