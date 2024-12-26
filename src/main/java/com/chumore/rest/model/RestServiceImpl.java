package com.chumore.rest.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.rest.compositequery.RestCompositeQuery;

@Service("restService")
public class RestServiceImpl implements RestService{
	
	@Autowired
	RestRepository repository;
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public void addRest(RestVO rest) {
		repository.save(rest);
	}

	@Override
	public void updateRest(RestVO rest) {
		repository.save(rest);
	}

	@Override
	public RestVO getById(Integer restId) {
		Optional<RestVO> optional = repository.findById(restId);
		return optional.orElse(null);
	}

	@Override
	public List<RestVO> getAll() {
		return repository.findAll();
	}

	@Override
	public List<RestVO> getAllCompos(Map<String, String[]> map) {
		return RestCompositeQuery.getAllC(map, sessionFactory.openSession());
	}

	

}
