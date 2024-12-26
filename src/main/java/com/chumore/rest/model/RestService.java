package com.chumore.rest.model;

import java.util.List;
import java.util.Map;

public interface RestService {
	
	void addRest(RestVO rest);
	void updateRest(RestVO rest);
	RestVO getById(Integer restId);
	List<RestVO> getAll();
	List<RestVO> getAllCompos(Map<String, String[]> map);
	
	

}
