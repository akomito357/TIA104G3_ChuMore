package com.chumore.envimg.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chumore.envimg.model.EnvImgService;
import com.chumore.envimg.model.EnvImgVO;
import com.chumore.rest.model.RestVO;



@CrossOrigin
@Controller
@RequestMapping("/envImg")
@MultipartConfig
public class EnvImgController {
	
	@Autowired
	EnvImgService envImgSvc;
	
	
	@PostMapping("addEnvImg")
	public String addEnvImg(@Valid EnvImgVO envImg, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		result = removeFieldError(envImg , result, "upFiles");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "請上傳照片");
		} else {
			
			RestVO rest = new RestVO();
			rest.setRestId(2001);
				
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				
				EnvImgVO img = new EnvImgVO();
				img.setImage(buf);	
				img.setRest(rest);
				/*************************** 2.開始新增資料 *****************************************/
				envImgSvc.addEnvImg(img);
				
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "";
		}
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<EnvImgVO> envImgList = envImgSvc.getAll();
		model.addAttribute("envImg", envImgList); // 傳遞到前端
		return ""; 
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("env_img_id") String envImgId, ModelMap model) {
		EnvImgService envImgSvc = new EnvImgService();
		envImgSvc.deleteEnvImg(Integer.valueOf(envImgId));
		
		List<EnvImgVO> envImgList = envImgSvc.getAll();
		model.addAttribute("envImg",envImgList); 
		
		return "/secure/rest/resturant_registration_form"; //重導回原本頁面
	}
	
	@GetMapping("getAll")
	public String getAll(Model model) {
		
		List<EnvImgVO> envImg = envImgSvc.getAll();
		model.addAttribute("envImgs", envImg);
		return "/secure/rest/resturant_registration_form"; 
	} 
		
	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(EnvImgVO envImgVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(envImgVO, "envImgVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	@GetMapping("getAllEnvImg")
	@ResponseBody
	public ResponseEntity<List<EnvImgVO>> getAllEnvImage(@RequestParam Integer restId){
			List<EnvImgVO> envImg = envImgSvc.getAllByRestId(restId);
			return ResponseEntity.ok(envImg);
	}

}
