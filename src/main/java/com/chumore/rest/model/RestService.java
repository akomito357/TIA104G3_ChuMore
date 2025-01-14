package com.chumore.rest.model;

import java.util.List;
import java.util.Map;

public interface RestService {
	
	void addRest(RestVO rest);
	void updateRest(RestVO rest);
	RestVO getOneById(Integer restId);
	List<RestVO> getAll();
	List<String> getFormattedBusinessHours(Integer restId);
	List<Integer> getBusinessHours(Integer restId);
	List<Integer> getRestIdsByOptionalFields(String city, String district, Integer cuisineTypeId);



}
