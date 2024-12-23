package com.chumore.location.model;

import java.util.List;

public interface LocationDAO {
	
	List<LocationVO> getAll();
	
	// 用ID查詢
	LocationVO getById(Integer locationId);
	
	// 用縣市名查詢
	List<LocationVO> getByCity(String city); 
	
	// 用縣市名查詢取得底下的鄉鎮市區
	List<LocationVO> getDistByCity(String city);

}
