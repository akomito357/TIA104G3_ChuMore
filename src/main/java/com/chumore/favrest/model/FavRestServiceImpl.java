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
	public void addFavRest(FavRestVO favRest) {
		repository.save(favRest);
	}

	@Override
	public void deleteFavRest(Integer favRestId) {
		if(repository.existsById(favRestId)) {
			repository.deleteByFavRestId(favRestId);
		}
	}

	@Override
	public List<String> getRestByMember(MemberVO member) {
		return repository.findRestByMember(member);
	}

	@Override
	public FavRestVO getOneFavRestById(Integer favRestId) {
		Optional<FavRestVO> optional = repository.findById(favRestId);
		return optional.orElse(null);
	}
	

}
