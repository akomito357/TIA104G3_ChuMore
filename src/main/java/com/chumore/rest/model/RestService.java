package com.chumore.rest.model;

import java.util.List;
import java.util.Map;

public interface RestService {
	
	void addRest(RestVO rest);
	void updateRest(RestVO rest);
	RestVO getOneById(Integer restId);
	List<RestVO> getAll();
	List<RestVO> getRestsByRestIds(List<Integer> restIds);

	List<String> getFormattedBusinessHours(Integer restId);
	List<Integer> getBusinessHours(Integer restId);

	RestVO getOneByEmail(String email);

	List<Integer> getRestIdsByOptionalFields(List<String> cities,List<String> districts,List<Integer> cuisineTypeIds);
	List<Integer[]> getBusinessHoursFor(Integer restId);
	String getBusinessDays(Integer restId);
//	boolean updatePassword(Integer restId, String oldPassword, String newPassword);
	
	public List<RestVO> getRandomRests(Integer count);
}
