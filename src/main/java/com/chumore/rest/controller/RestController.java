package com.chumore.rest.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	// getBy...
	@GetMapping("findRestBy")
	public String findRestBy(HttpServletRequest req, ModelMap model) {
		Map<String, String[]> map = req.getParameterMap();
		List<RestVO> list = restSvc.getAllCompos(map);
		model.addAttribute("rests", list);
		return "";
	}
	
	@GetMapping("getOneRest")
	@ResponseBody
	public ResponseEntity<RestVO> getOneData(@RequestParam Integer restId){
		RestVO rest = restSvc.getOneById(restId);
		return ResponseEntity.ok(rest);	
	}
	
}
