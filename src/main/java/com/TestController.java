package com;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;

@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private RestService restSvc;

	@GetMapping
	public String test(HttpSession session,ModelMap model) {
		RestVO rest = restSvc.getOneById(2001);
		session.setAttribute("rest",rest);
		model.addAttribute("rest", session.getAttribute("rest"));
//		model.addAttribute("rest",rest);
		return "merchant_restaurant_information_modification";
		
	}
}
