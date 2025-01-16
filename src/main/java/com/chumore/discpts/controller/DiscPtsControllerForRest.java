package com.chumore.discpts.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.discpts.dto.DiscPtsAvailableDTO;
import com.chumore.discpts.model.DiscPtsService;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.util.ResponseUtil;

@Controller
@RequestMapping("/rest/points")
public class DiscPtsControllerForRest {
	
	@Autowired
    private DiscPtsService discPtsService;
	
	@Autowired
	private OrderMasterService orderSvc;

	@PostMapping("getPointsByMemberAndRest")
    @ResponseBody
    public ResponseEntity<?> getPointsByMemberAndRest(@RequestBody Map<String, String> map){
    	Integer memberId = Integer.valueOf(map.get("memberId"));
    	Integer restId = Integer.valueOf(map.get("restId"));
    	
    	DiscPtsAvailableDTO discPtsAvail = discPtsService.getAvailablePointsByMemberAndRest(memberId, restId);
    	ResponseUtil res = new ResponseUtil("success", 200, discPtsAvail);
    	
    	return ResponseEntity.ok(res);
    }
	
	@PostMapping("verifyUsage")
	@ResponseBody
	public ResponseEntity<?> calcPointsAndTotal(@RequestBody Map<String, Integer> verifyMap, HttpSession session){
		Integer memberId = Integer.valueOf(verifyMap.get("memberId"));
		Integer orderId = Integer.valueOf(verifyMap.get("orderId"));
		Integer usePoints = Integer.valueOf(verifyMap.get("usePoints"));
		
		Map<String, BigDecimal> map = new HashMap<>();
		if (discPtsService.checkPointsSufficiency(memberId, usePoints, session)) {
			BigDecimal discounts = orderSvc.calcDiscount(usePoints);
			BigDecimal totalPrice =  orderSvc.calcTotalPrice(orderId, discounts);
			map.put("usePoints", BigDecimal.valueOf(usePoints));
			map.put("discounts", discounts);
			map.put("totalPrice", totalPrice);
			map.put("subTotalPrice", orderSvc.getOneById(orderId).getSubtotalPrice());
		}
		ResponseUtil res = new ResponseUtil("success", 200, map);
		
		return ResponseEntity.ok(res);
		
	}
	
}
