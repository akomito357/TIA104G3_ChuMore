package com;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {
	
	/*
	 * 這支class除了引導到首頁外，可以用來測試已引入thymeleaf的html，
	 * 請自由新增欲測試的頁面。但請注意有一定機率導致git衝突。
	 */

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
		return "header_member";
	}
	
	@GetMapping("/check") // test
	public String checktest() {
		return "secure/rest/order/restCheckout";
	}
	
}
