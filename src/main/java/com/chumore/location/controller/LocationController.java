package com.chumore.location.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.location.model.LocationService;
import com.chumore.location.res.LocationResponse;

@Controller
@RequestMapping("/location")
public class LocationController {

	@Autowired
	LocationService locationSvc;
	
	@GetMapping("findDistByCity")
	public ResponseEntity<LocationResponse> findDistByCity(@RequestParam Map<String, String> req){
		List<String> list = null;
		LocationResponse<List<String>> res = null;
		try {
			String city = req.get("city");
			list = locationSvc.getDistByCity(city);
			res = new LocationResponse<>(200, "success", list);
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			res = new LocationResponse<>(400, "failed", list);
			return ResponseEntity.badRequest().body(res);
		}
		
	}
	
	@GetMapping("findCitys")
	public ResponseEntity<LocationResponse> findCitys(){
		List<String> cityList = null;
		LocationResponse<List<String>> res = null;
		try {
			cityList = locationSvc.getCitys();
			res = new LocationResponse<>(200, "success", cityList);
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			res = new LocationResponse<>(400, "failed", cityList);
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	@GetMapping("findCityAndDist")
	public ResponseEntity<LocationResponse> findCityAndDistByCity(@RequestParam Map<String, String> req){
		List<String[]> list = null;
		LocationResponse<List<String[]>> res = null;
		try {
			String city = req.get("city");
			list = locationSvc.getCityAndDistByCity(city);
			res = new LocationResponse<>(200, "success", list);
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			res = new LocationResponse<>(400, "failed", list);
			return ResponseEntity.badRequest().body(res);
		}
		
	}
	
	
}
