package com;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequestMapping("/rests")
public class RestInfoTestController {
	
	@GetMapping("restInfo")
	public String test() {
		return "secure/rest/rest_information";
	}
	
	@GetMapping("reservation_setting")
	public String test2() {
		return "secure/rest/reservation_setting";
	}
	
	@GetMapping("reservation")
	public String test3() {
		return "secure/rest/reservation";
	}
	
	@GetMapping("change_password")
	public String testˋ() {
		return "secure/change_password.html";
	}
}
