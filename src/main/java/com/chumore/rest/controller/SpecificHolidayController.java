package com.chumore.rest.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.SpecificHolidayService;
import com.chumore.rest.model.SpecificHolidayVO;

@Controller
@RequestMapping("/specificHoliday")
public class SpecificHolidayController {
	@Autowired
	HttpSession session;
	@Autowired
	SpecificHolidayService specificHolidaySvc;
	@Autowired
	private RestService restSvc;
	
	@PostMapping("addSpecificHoliday")
	public ResponseEntity<Map<String, Object>>addHoliday(@RequestBody Map<String, Object> requestData){
		Map<String, Object> response = new HashMap<>();
	    try {
	        Integer restId = Integer.parseInt(requestData.get("restId").toString());
	        String dayStr = requestData.get("day").toString(); // 假設前端傳入的日期格式為 "YYYY-MM-DD"
	        
	        // 將字串轉換為 Date 物件
	        LocalDate day = LocalDate.parse(dayStr);
	        
	        SpecificHolidayVO holiday = new SpecificHolidayVO();
            holiday.setRestId(restId);
            holiday.setDay(day);
            
            specificHolidaySvc.addHoliday(holiday);
            
	        response.put("success", true);
	        response.put("message", "新增成功");
	        return ResponseEntity.ok(response);
	        
	    } catch (DateTimeParseException e) {
	        response.put("success", false);
	        response.put("message", "日期格式錯誤");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "新增失敗：" + e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
	
	@GetMapping("/getSpecialClosedDays")
	public ResponseEntity<List<Map<String, Object>>> getSpecialClosedDays(@RequestParam Integer restId) {
	    try {
	        List<SpecificHolidayVO> holidays = specificHolidaySvc.getHolidaysByRestId(restId);
	        
	        List<Map<String, Object>> result = holidays.stream()
	            .map(holiday -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("day", holiday.getDay().toString());
	                map.put("specificHolidayId", holiday.getSpecificHolidayId());
	                return map;
	            })
	            .collect(Collectors.toList());
	        
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	    
	    @DeleteMapping("/deleteSpecialClosedDay")
	    public ResponseEntity<Map<String, Object>> deleteSpecialClosedDay(
	            @RequestParam Integer specificHolidayId) {
	        Map<String, Object> response = new HashMap<>();
	        
	        try {
	            specificHolidaySvc.deleteHoliday(specificHolidayId);
	            
	            response.put("success", true);
	            response.put("message", "刪除成功");
	            return ResponseEntity.ok(response);
	            
	        } catch (Exception e) {
	            response.put("success", false);
	            response.put("message", "刪除失敗：" + e.getMessage());
	            return ResponseEntity.ok(response);
	        }
	    }
	}
	

