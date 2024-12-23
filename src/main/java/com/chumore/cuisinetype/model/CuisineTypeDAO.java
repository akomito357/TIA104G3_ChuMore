package com.chumore.cuisinetype.model;

import java.util.List;

public interface CuisineTypeDAO {
	
	public List<CuisineTypeVO> getAll();
	public CuisineTypeVO getById(Integer cuisineTypeId);
	public List<CuisineTypeVO> getByName(String cuisineDescr);
	public int insert(CuisineTypeVO cuisineTypeVO);
	public int update(CuisineTypeVO cuisineTypeVO);

}
