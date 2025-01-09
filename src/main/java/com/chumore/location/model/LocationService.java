package com.chumore.location.model;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface LocationService {

	LocationVO getOneById(Integer locationId);
	List<LocationVO> getAll();
	List<String> getDistByCity(String city);
	List<String> getCitys();
	List<String[]> getCityAndDistByCity(String city);

}
