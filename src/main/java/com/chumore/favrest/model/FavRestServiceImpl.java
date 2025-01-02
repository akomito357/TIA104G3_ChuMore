package com.chumore.favrest.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.member.model.MemberVO;

@Service("favRestService")
public class FavRestServiceImpl implements FavRestService{

	@Autowired
	FavRestRepository repository;
	
	@Override
	public FavRestVO addFavRest(FavRestVO favRest) {
		return repository.save(favRest);
	}

	@Override
	public Integer deleteFavRest(Integer favRestId) {
		if(repository.existsById(favRestId)) {
			repository.deleteByFavRestId(favRestId);
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public List<FavRestVO> getFavRestByMember(MemberVO member) {
		return repository.findFavRestByMember(member);
	}

	@Override
	public FavRestVO getOneFavRestById(Integer favRestId) {
		Optional<FavRestVO> optional = repository.findById(favRestId);
		return optional.orElse(null);
	}
	

}
