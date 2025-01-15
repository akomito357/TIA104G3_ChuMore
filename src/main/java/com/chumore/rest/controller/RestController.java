package com.chumore.rest.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.cuisinetype.model.CuisineTypeService;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;

// @SessionAttributes(names={"rest","member"})

@CrossOrigin
@Controller
@RequestMapping("/rests")
public class RestController {
	
	@Autowired
	HttpSession session;
	
	@Autowired
	RestService restSvc;

	@Autowired 
	CuisineTypeService cuisineTypeSvc;
	// add
	@GetMapping("addRest")
	public String addRest(ModelMap model) {
		RestVO rest = new RestVO();
		model.addAttribute("rest", rest);
		return "";
	}
	
	// insert
	@PostMapping("insert")
	public String insert(@Valid RestVO rest, BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return ""; // 返回原頁面顯示錯誤訊息
		}
		restSvc.addRest(rest);
		rest = restSvc.getOneById(Integer.valueOf(rest.getRestId()));
		model.addAttribute("rest", rest);
		return "";
	}
	// @SessionAttribute("rest") RestVO rest 
	
	// update
	@PostMapping("update")
	public String update(@Valid RestVO rest, BindingResult result, ModelMap model,HttpSession session) {
		if(result.hasErrors()) {
			return "";
		}
		restSvc.updateRest(rest);
		rest = restSvc.getOneById(Integer.valueOf(rest.getRestId()));
		model.addAttribute("rest", session.getAttribute("rest"));
		return "n";
	}
	
	// getOne - for thymeleaf
	@GetMapping("findRest")
	public String findRestById(@RequestParam("restId") String restId, ModelMap model,HttpSession session) {
		RestVO rest = restSvc.getOneById(Integer.valueOf(restId));
		session.setAttribute("rest",rest);
		model.addAttribute("rest", session.getAttribute("rest"));
		return "";
	}

	
	@GetMapping("getOneRest")
	@ResponseBody
	public ResponseEntity<RestVO> getOneData(@RequestParam Integer restId){
		RestVO rest = restSvc.getOneById(restId);
		return ResponseEntity.ok(rest);
	}
	
	@PutMapping("updateRest")
	public ResponseEntity<Map<String, Object>> updateRest(@RequestBody Map<String, Object> requestData) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        Integer restId = Integer.parseInt(requestData.get("restId").toString());
	        Integer cuisineTypeId = Integer.parseInt(requestData.get("cuisineTypeId").toString());

	        // 獲取原有的餐廳資料
	        RestVO existingRest = restSvc.getOneById(restId);
	        if (existingRest == null) {
	            response.put("success", false);
	            response.put("message", "找不到餐廳資料");
	            return ResponseEntity.ok(response);
	        }

	        CuisineTypeVO cuisineType = cuisineTypeSvc.getOneCuisineType(cuisineTypeId);
	        if (cuisineType == null) {
	            response.put("success", false);
	            response.put("message", "找不到料理類型");
	            return ResponseEntity.ok(response);
	        }

	        existingRest.setMerchantName((String) requestData.get("merchantName"));
	        existingRest.setPhoneNumber((String) requestData.get("phoneNumber"));
	        existingRest.setRestName((String) requestData.get("restName"));
	        existingRest.setRestRegist((String) requestData.get("restRegist"));
	        existingRest.setRestPhone((String) requestData.get("restPhone"));
	        existingRest.setRestCity((String) requestData.get("restCity"));
	        existingRest.setRestDist((String) requestData.get("restDist"));
	        existingRest.setRestAddress((String) requestData.get("restAddress"));
	        existingRest.setRestIntro((String) requestData.get("restIntro"));
	        existingRest.setCuisineType(cuisineType);
	        existingRest.setBusinessStatus(Integer.parseInt(requestData.get("businessStatus").toString()));

	        restSvc.updateRest(existingRest);
	        
	        response.put("success", true);
	        response.put("message", "更新成功");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "更新失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
	@GetMapping
	public ResponseEntity<?> getBusinessHoursToUpdate(@RequestParam Integer restId){
		try {
			List<Integer>bussinessHours =restSvc.getBusinessHours(restId);
			
		}catch(Exception e){
			
		};
		
		return ResponseEntity.ok(null);
		}
	
	// 獲取根本原因的輔助方法
	private Throwable getRootCause(Throwable e) {
	    Throwable cause = e;
	    while (cause.getCause() != null) {
	        cause = cause.getCause();
	    }
	    return cause;
	}
}
