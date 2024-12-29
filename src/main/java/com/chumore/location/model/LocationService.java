package com.chumore.location.model;

import java.util.List;

public interface LocationService {

	LocationVO getOneById(Integer locationId);
	List<LocationVO> getAll();
	List<String> getDistByCity(String city);
	List<String> getCity(String city);

}
