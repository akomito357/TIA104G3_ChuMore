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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/emp")
public class EmpController {
	private static final Logger log = LoggerFactory.getLogger(EmpController.class);
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
	public String updateProfile(@Valid EmpBasicUpdateDTO dto, BindingResult result, Authentication auth, Model model,
			RedirectAttributes redirectAttr) {
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

	@GetMapping("admin/add")
	public String addEmp(ModelMap model) {
		// 初始化一個新的EmpFullDTO對象
		EmpFullDTO empDTO = new EmpFullDTO();
		model.addAttribute("empDTO", empDTO);
		return "emp/admin/add";
	}

	// 新增員工
	@PostMapping("/admin/add")
	public String insert(@Valid @ModelAttribute("empDTO") EmpFullDTO dto, BindingResult result, Model model,
			RedirectAttributes redirectAttr) {

		// 驗證錯誤處理
		if (result.hasErrors()) {
			// 保留用户输入的数据
			model.addAttribute("empDTO", dto);
			return "emp/admin/add"; // 返回到新增頁面
		}

		try {
			// 检查是否存在重复数据
			if (empRepository.existsByEmpAccount(dto.getEmpAccount())) {
				model.addAttribute("error", "此帳號已存在");
				return "emp/admin/add"; // 返回到新增頁面
			}
			if (empRepository.existsByEmpPhone(dto.getEmpPhone())) {
				model.addAttribute("error", "此手機號碼已被使用");
				return "emp/admin/add"; // 返回到新增頁面
			}
			if (empRepository.existsByEmpEmail(dto.getEmpEmail())) {
				model.addAttribute("error", "此Email已被使用");
				return "emp/admin/add"; // 返回到新增頁面
			}

			// 新增員工
			empService.addEmp(dto);

			// 新增成功訊息
			redirectAttr.addFlashAttribute("message", "員工新增成功！預設密碼為：A1234567，請通知員工盡快修改密碼。");

			// 修改這一行，確保使用正確的重定向路徑
			return "redirect:/emp/admin/list"; // 確保這個路徑是正確的

		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return "emp/admin/add"; // 發生異常時返回到新增頁面
		} catch (Exception e) {
			// 添加通用異常處理
			model.addAttribute("error", "發生未預期的錯誤: " + e.getMessage());
			return "emp/admin/add";
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


	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/admin/edit/{id}")
	public String updateEmp(@PathVariable("id") Integer empId, 
	                       @Valid @ModelAttribute("empDTO") EmpFullDTO dto,
	                       BindingResult result, 
	                       Model model,
	                       RedirectAttributes redirectAttr) {
	    
	    log.info("Updating employee with ID: {}", empId); // 添加日誌
	    
	    if (result.hasErrors()) {
	        log.warn("Validation errors: {}", result.getAllErrors());
	        model.addAttribute("empDTO", dto);
	        return "emp/admin/edit";
	    }

	    try {
	        dto.setEmpId(empId);
	        empService.updateEmp(empId, dto);
	        redirectAttr.addFlashAttribute("message", "員工資料更新成功！");
	        return "redirect:/emp/admin/list";
	    } catch (DataIntegrityViolationException e) {
	        log.error("Data integrity violation while updating employee", e);
	        model.addAttribute("error", "資料重複或違反限制: " + e.getMessage());
	        model.addAttribute("empDTO", dto);
	        return "emp/admin/edit";
	    } catch (Exception e) {
	        log.error("Error updating employee", e);
	        model.addAttribute("error", "更新失敗: " + e.getMessage());
	        model.addAttribute("empDTO", dto);
	        return "emp/admin/edit";
	    }
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

	// 處理例外
	@ExceptionHandler(Exception.class)
	public String handleError(Exception e, RedirectAttributes redirectAttr) {
		redirectAttr.addFlashAttribute("error", "發生錯誤：" + e.getMessage());
		return "redirect:/emp/error";
	}
}