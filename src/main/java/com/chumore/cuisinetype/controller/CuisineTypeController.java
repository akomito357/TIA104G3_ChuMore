package com.chumore.cuisinetype.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.cuisinetype.model.CuisineTypeService;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.rest.model.RestService;

@Controller
@RequestMapping("/cuisineTypes")
public class CuisineTypeController {

	@Autowired
	CuisineTypeService cuisineTypeSvc;
	
	@Autowired
	RestService restSvc;
	
	
	
	@GetMapping("/addCuisineType")
	public String addCuisineType(ModelMap model) {
		CuisineTypeVO cuisineType = new CuisineTypeVO();
		model.addAttribute(cuisineType);
		return "";
	}
	
	@GetMapping("getAllCuisineType")
	public ResponseEntity<?>getAllCuisineType(){
		List <CuisineTypeVO> list = cuisineTypeSvc.getAllTypes();
		System.out.println("Hello");
		return ResponseEntity.ok(list);
	}
	
	
}
