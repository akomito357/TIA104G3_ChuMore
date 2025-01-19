package com.chumore.emp.controller;

import com.chumore.emp.dto.EmpBasicUpdateDTO;
import com.chumore.emp.dto.EmpBasicViewDTO;
import com.chumore.emp.dto.EmpFullDTO;
import com.chumore.emp.model.EmpVO;
import com.chumore.member.model.MemberRepository;
import com.chumore.rest.model.RestRepository;
import com.chumore.emp.model.EmpRepository;
import com.chumore.emp.model.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/emp")
public class EmpController {

	@Autowired
	private EmpService empService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RestRepository restRepository;

	@Autowired
	private EmpRepository empRepository;
	// ========== 一般員工功能 ==========
	// 查看個人資料
	@GetMapping("/profile")
	public String getProfile(Model model, Authentication auth) {
		String account = auth.getName();
		EmpVO emp = empService.getEmpByAccount(account);
		EmpBasicViewDTO empInfo = empService.getOwnBasicInfo(emp.getEmpId());

		model.addAttribute("empInfo", empInfo);
		return "emp/profile";
	}

	// 前往編輯個人資料頁面
	@GetMapping("/edit")
	public String getEditForm(Model model, Authentication auth) {
		String account = auth.getName();
		EmpVO emp = empService.getEmpByAccount(account);
		EmpBasicViewDTO empInfo = empService.getOwnBasicInfo(emp.getEmpId());

		model.addAttribute("empInfo", empInfo);
		return "emp/edit";
	}

	// 更新個人資料
	@PostMapping("/edit")
	public String updateProfile(@Valid EmpBasicUpdateDTO dto, BindingResult result, 
	        Authentication auth, Model model, RedirectAttributes redirectAttr) {
	    if (result.hasErrors()) {
	        // 添加這行來回顯數據
	        model.addAttribute("empInfo", dto);
	        return "emp/edit";
	    }

	    try {
	        String account = auth.getName();
	        EmpVO emp = empService.getEmpByAccount(account);
	        empService.updateOwnBasicInfo(emp.getEmpId(), dto);
	        redirectAttr.addFlashAttribute("message", "資料更新成功！");
	        return "emp/profile";
	    } catch (IllegalArgumentException e) {
	        redirectAttr.addFlashAttribute("error", e.getMessage());
	        return "redirect:/emp/edit";
	    }
	}
	
	@GetMapping("/dashboard")
	public String showDashboard(Model model) {
	    // 取得各項數量
	    long totalMembers = memberRepository.count();
	    long totalRests = restRepository.count(); 
	    long totalEmps = empRepository.count();
	    
	    // 添加到model
	    model.addAttribute("totalMembers", totalMembers);
	    model.addAttribute("totalRests", totalRests); 
	    model.addAttribute("totalEmps", totalEmps);
	    
	    return "emp/dashboard";
	}
	// ========== 管理員功能 ==========
	// 顯示所有員工列表
	@GetMapping("/admin/list")
	public String listAll(Model model) {
		List<EmpFullDTO> emps = empService.getAllEmpsFullInfo();
		model.addAttribute("emps", emps);
		return "emp/admin/list";
	}

	// 前往新增員工頁面
	@GetMapping("/admin/add")
	public String showAddForm(Model model) {
	    if (!model.containsAttribute("empDTO")) {
	        model.addAttribute("empDTO", new EmpFullDTO());
	    }
	    return "emp/admin/add";  // 不需要前導斜線
	}
	// 修改密碼
	@PostMapping("/admin/reset-password/{id}")
	public String resetPassword(@PathVariable("id") Integer empId, RedirectAttributes redirectAttr) {
	    try {
	        String tempPassword = empService.resetPassword(empId);
	        redirectAttr.addFlashAttribute("message", "密碼重置成功，暫時密碼為：" + tempPassword);
	    } catch (Exception e) {
	        redirectAttr.addFlashAttribute("error", "密碼重置失敗：" + e.getMessage());
	    }
	    return "redirect:/emp/admin/list";
	}
	// 新增員工
	
	@PostMapping("/admin/add")
	public String addEmp(@Valid @ModelAttribute("empDTO") EmpFullDTO dto, 
	                    BindingResult result,
	                    RedirectAttributes redirectAttr) {
	    if (result.hasErrors()) {
	        return "/emp/admin/add";
	    }

	    try {
	        empService.addEmp(dto);
	        redirectAttr.addFlashAttribute("message", 
	            "員工新增成功！預設密碼為：A1234567，請通知員工盡快修改密碼。");
	        return "redirect:/emp/admin/list";
	    } catch (IllegalArgumentException e) {
	        redirectAttr.addFlashAttribute("error", e.getMessage());
	        return "redirect:/emp/admin/add";
	    }
	}
	
	

	// 前往編輯員工頁面
	@GetMapping("/admin/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer empId, Model model) {
		EmpFullDTO emp = empService.getEmpById(empId);
		model.addAttribute("empDTO", emp);
		return "emp/admin/edit";
	}

	// 更新員工資料
	@PostMapping("/admin/edit/{id}")
	public String updateEmp(@PathVariable("id") Integer empId, @Valid @ModelAttribute("empDTO") EmpFullDTO dto,
			BindingResult result, RedirectAttributes redirectAttr) {
		if (result.hasErrors()) {
			return "emp/admin/edit";
		}

		try {
			empService.updateEmp(empId, dto);
			redirectAttr.addFlashAttribute("message", "員工資料更新成功！");
			return "redirect:/emp/admin/list";
		} catch (IllegalArgumentException e) {
			redirectAttr.addFlashAttribute("error", e.getMessage());
			return "redirect:/emp/admin/edit/" + empId;
		}
	}

	// 處理例外
	@ExceptionHandler(Exception.class)
	public String handleError(Exception e, RedirectAttributes redirectAttr) {
		redirectAttr.addFlashAttribute("error", "發生錯誤：" + e.getMessage());
		return "redirect:/emp/error";
	}
}