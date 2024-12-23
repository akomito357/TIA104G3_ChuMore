package com.chumore.cuisinetype.model;

import java.util.List;

public class CuisineTypeServiceImpl implements CuisineTypeService{
	private CuisineTypeDAO dao;
	
	public CuisineTypeServiceImpl() {
		dao = new CuisineTypeJDBCDAO();
	}

	public List<CuisineTypeVO> getAllTypes() {
		return dao.getAll();		
	}

	public CuisineTypeVO getOneCuisineType(Integer cuisineTypeId) {
		return dao.getById(cuisineTypeId);
	}

	public List<CuisineTypeVO> getTypesByName(String cuisineDescr) {
		return dao.getByName(cuisineDescr);
	}

	public CuisineTypeVO addCuisineType(String cuisineDescr) {
		CuisineTypeVO cuisineTypeVO = new CuisineTypeVO();
		cuisineTypeVO.setCuisineDescr(cuisineDescr);
		
		dao.insert(cuisineTypeVO);
		return cuisineTypeVO;
	}

	public CuisineTypeVO updateCuisineType(Integer cuisineTypeId, String cuisineDescr) {
		CuisineTypeVO cuisineTypeVO = new CuisineTypeVO();
		cuisineTypeVO.setCuisineTypeId(cuisineTypeId);
		cuisineTypeVO.setCuisineDescr(cuisineDescr);
		dao.update(cuisineTypeVO);
		
		return cuisineTypeVO;
	}

}
