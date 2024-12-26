package com.chumore.cuisinetype.model;

import java.util.List;

public interface CuisineTypeService {
	List<CuisineTypeVO> getAllTypes();
	CuisineTypeVO getOneCuisineType(Integer cuisineTypeId);
	List<CuisineTypeVO> getTypesByDescr(String cuisineDescr);
	
	void addCuisineType(CuisineTypeVO cuisineType);
	void updateCuisineType(CuisineTypeVO cuisineType);
}
