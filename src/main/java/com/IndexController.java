package com;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.util.ResponseUtil;


@Controller
public class IndexController {
	
	/*
	 * 這支class除了引導到首頁外，可以用來測試已引入thymeleaf的html，
	 * 請自由新增欲測試的頁面。但請注意有一定機率導致git衝突。
	 */
	
	@Autowired
	RestService restSvc;

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/sidebar") // test
	public String sidebartest() {
		return "sidebar_merchant";
	}
	
	@GetMapping("/header") // test
	public String headertest() {
		return "header_merchant";
	}
	
	@GetMapping("/testServiceBellOrder")
	public String testOrderServiceBell(){
		return "/testOrderServiceBell";
	}
	
	@GetMapping("/testRestNotifyServiceBell")
	public String testRestNotifyServiceBell(){
		return "/testRestNotifyServiceBell";
	}
	
	@GetMapping("/rest_reservation")
	public String testRestReservation() {
		return "secure/reservation/reserved_limit_setting";
	}
	
	@PostMapping("getRole")
    public String getRole(Authentication authentication, HttpSession session) {
    	String role = authentication.getAuthorities().iterator().next().getAuthority();
    	session.setAttribute("role", role);
    	System.out.println("role: " + role);
    	return role;
    }
	
	@GetMapping("/searchPage")
	public String login() {
		return "search_result";
	}
	
	@PostMapping("/getRandomRest")
	@ResponseBody
	public ResponseEntity<?> findRandomRest(@RequestBody Map<String, Integer> map){
		List<RestVO> list = restSvc.getRandomRests(map.get("count"));
		ResponseUtil res = new ResponseUtil("success", 200, list);
		return ResponseEntity.ok(res);
	}
	
}
