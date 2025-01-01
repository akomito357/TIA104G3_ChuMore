package com.chumore.location.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService{
	
	@Autowired
	LocationRepository repository;

	@Override
	public LocationVO getOneById(Integer locationId) {
		Optional<LocationVO> optional = repository.findById(locationId);
		return optional.orElse(null);
	}

	@Override
	public List<LocationVO> getAll() {
		return repository.findAll();
	}

	@Override
	public List<String> getDistByCity(String city) {
		return repository.findDistByCity(city);
	}

	@Override
	public List<String> getCitys() {
		return repository.findCitys();
	}
	
	@Override
	public List<String[]> getCityAndDistByCity(String city){
		return repository.findCityAndDistByCity(city);
	}
	

}
