package com.chumore.envimg.controller;

import java.io.IOException;
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

import com.chumore.envimg.model.EnvImgService;
import com.chumore.envimg.model.EnvImgVO;




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
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(envImg, result, "upFiles");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "員工照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				envImg.setImage(buf);
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {
			return "back-end/emp/addEmp";
		}
		/*************************** 2.開始新增資料 *****************************************/
		// EmpService empSvc = new EmpService();
		envImgSvc.addEnvImg(envImg);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<EnvImgVO> list = envImgSvc.getAll();
		model.addAttribute("empListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/emp/listAllEmp"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/emp/listAllEmp")
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

}
