package com.chumore.cuisinetype.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.chumore.rest.model.RestVO;

public class CuisineTypeServiceImpl implements CuisineTypeService{

	@Autowired
	CuisineTypeRepository repository;
	
	
	@Override
	public List<CuisineTypeVO> getAllTypes() {
		return repository.findAll();
	}

	@Override
	public CuisineTypeVO getOneCuisineType(Integer cuisineTypeId) {
		Optional<CuisineTypeVO> optional = repository.findById(cuisineTypeId);
		return optional.orElse(null);
	}

	@Override
	public List<CuisineTypeVO> getTypesByDescr(String cuisineDescr) {
		return repository.findByDescr(cuisineDescr);
	}

	@Override
	public void addCuisineType(CuisineTypeVO cuisineType) {
		repository.save(cuisineType);
		
	}

	@Override
	public void updateCuisineType(CuisineTypeVO cuisineType) {
		repository.save(cuisineType);
	}
	
	public Set<RestVO> getRestsByCuisineTypeId(Integer cuisineTypeId){
		return getOneCuisineType(cuisineTypeId).getRests();
	}

}
