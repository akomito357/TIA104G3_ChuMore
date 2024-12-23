package com.chumore.location.model;

import java.util.List;

public interface LocationService {

	LocationVO getById(Integer locationId);
	List<LocationVO> getAll();
	List<LocationVO> getDistByCity(String city);

}
