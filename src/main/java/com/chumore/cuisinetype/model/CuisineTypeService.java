package com.chumore.cuisinetype.model;

import java.util.List;

public interface CuisineTypeService {
	List<CuisineTypeVO> getAllTypes();
	CuisineTypeVO getOneCuisineType(Integer cuisineTypeId);
	List<CuisineTypeVO> getTypesByName(String cuisineDescr);
	
	CuisineTypeVO addCuisineType(String cuisineDescr);
	CuisineTypeVO updateCuisineType(Integer cuisineTypeId, String cuisineDescr);
}
