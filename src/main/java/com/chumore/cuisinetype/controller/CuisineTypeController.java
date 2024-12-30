package com.chumore.cuisinetype.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.cuisinetype.model.CuisineTypeService;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.rest.model.RestService;

//@Controller
@RequestMapping("/cuisineType")
public class CuisineTypeController {

//	@Autowired
	CuisineTypeService cuisineTypeSvc;
	
//	@Autowired
	RestService restSvc;
	
	@GetMapping("/addCuisineType")
	public String addCuisineType(ModelMap model) {
		CuisineTypeVO cuisineType = new CuisineTypeVO();
		model.addAttribute(cuisineType);
		return "";
	}
	
	
	
}
