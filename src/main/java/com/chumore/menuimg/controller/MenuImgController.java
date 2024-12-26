package com.chumore.menuimg.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.chumore.menuimg.model.MenuImgService;
import com.chumore.menuimg.model.MenuImgVO;
import com.chumore.rest.model.RestVO;




@Controller
@RequestMapping("/menuImg")
@MultipartConfig
public class MenuImgController {
	
	@Autowired
	MenuImgService menuImgSvc;
	
	
	@PostMapping("addMenuImg")
	public String addMenuImg(@Valid MenuImgVO menuImg, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(menuImg , result, "upFiles");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "員工照片: 請上傳照片");
		} else {
			
			RestVO rest = new RestVO();
			rest.setRestId(2001);//先預設一個restId之後要從關聯式資料庫帶入
				
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				
				MenuImgVO img = new MenuImgVO();
				img.setImage(buf);	
				img.setRest(rest);
				/*************************** 2.開始新增資料 *****************************************/
				menuImgSvc.addMenuImg(img);
				
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "index";
		}
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<MenuImgVO> menuImgList = menuImgSvc.getAll();
		model.addAttribute("menuImg", menuImgList); // 傳遞到前端
//		model.addAttribute("Succesful", "- (新增成功)");
		return "/secure/rest/resturant_registration_form"; 
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("menu_img_id") String menuImgId, ModelMap model) {
		MenuImgService menuImgSvc = new MenuImgService();
		menuImgSvc.deleteMenuImg(Integer.valueOf(menuImgId));
		
		List<MenuImgVO> menuImgList = menuImgSvc.getAll();
		model.addAttribute("menuImg",menuImgList); 
		
		return "/secure/rest/resturant_registration_form"; //重導回原本頁面
	}
	
	@GetMapping("getAll")
	public String getAll(Model model) {
		
		List<MenuImgVO> menuImgList = menuImgSvc.getAll();
		model.addAttribute("menuImg",menuImgList); 
		return "/secure/rest/resturant_registration_form"; 
	} 
		
	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(MenuImgVO menuImgVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(menuImgVO, "envImgVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

}
