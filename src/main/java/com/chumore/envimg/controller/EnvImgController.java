package com.chumore.envimg.controller;

import java.util.List;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.envimg.model.EnvImgService;
import com.chumore.envimg.model.EnvImgVO;




@Controller
@RequestMapping("/envImg")
@MultipartConfig
public class EnvImgController {
	
	@Autowired
	EnvImgService envImgSvc;
	
	
	@PostMapping("addMultipleEnvImgs")
	public String addMutipleEnvImg( @RequestParam("file") MultipartFile file, @RequestParam("restId") Integer restId) {
		EnvImgService empSvc = new EnvImgService();
		envImgSvc.addMultipleEnvImgs(file, restId);
			
		return "back-end/secure/rest/resturant_registration_form";
	} 
	
	
	@PostMapping("delete")
	public String delete(@RequestParam("emv_img_id") String envImgId, ModelMap model) {
		EnvImgService empSvc = new EnvImgService();
		empSvc.deleteEnvImg(Integer.valueOf(envImgId));
		
		return "back-end/secure/rest/resturant_registration_form"; 
	}
	
	@GetMapping("getAll")
	public String getAll(Model model) {
		
		List<EnvImgVO> envImgs = envImgSvc.getAll();
		model.addAttribute("envImgs", envImgs);
		return "back-end/secure/rest/resturant_registration_form"; 
	} 
	
	

}
