package com.chumore.rest.controller;


import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.util.ResponseUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/restaurants")
public class RestPublicController {

    @Autowired
    private RestService restService;

    @GetMapping("/restaurant/{restId}/formattedBusinessHours")
    public ResponseEntity<?> getFormattedBusinessHours(@PathVariable Integer restId){
        return ResponseEntity.ok(restService.getFormattedBusinessHours(restId));
    }


    @GetMapping("/restaurant/{restId}/businessHours")
    public ResponseEntity<?> getBusinessHours(@PathVariable Integer restId){
        List<Integer> businessHours  = restService.getBusinessHours(restId);
        return ResponseEntity.ok(businessHours);
    }

    @GetMapping("/restaurant/{restId}")
    public ResponseEntity<?> getRest(@PathVariable Integer restId){
        return ResponseEntity.ok(restService.getOneById(restId));
    }


    @GetMapping("/search")
    public ResponseEntity<?> getRestsByOptionalFields(@RequestParam(name = "city", required = false) List<String> cities,
                                                      @RequestParam(name = "district", required = false) List<String> districts,
                                                      @RequestParam(name = "cuisineTypeId", required = false) List<Integer> cuisineTypeIds){
        return ResponseEntity.ok(restService.getRestIdsByOptionalFields(cities, districts, cuisineTypeIds));
    }


    @PostMapping
    public ResponseEntity<?> getRestsByRestIds(@RequestBody List<Integer> restIds){
        return ResponseEntity.ok(restService.getRestsByRestIds(restIds));
    }
    
    @PostMapping("/getRandomRest")
	@ResponseBody
	public ResponseEntity<?> findRandomRest(@RequestBody Map<String, Integer> map){
		List<RestVO> list = restService.getRandomRests(map.get("count"));
		ResponseUtil res = new ResponseUtil("success", 200, list);
		return ResponseEntity.ok(res);
	}
	


}
