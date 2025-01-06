package com.chumore.emp.controller;

import com.chumore.emp.model.EmpVO;
import com.chumore.emp.model.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 顯示員工列表
    @GetMapping("/list")
    public String listEmployees(Model model,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) String name) {
        List<EmpVO> employees;
        if (status != null && name != null) {
            employees = empService.searchByStatusAndName(status, name);
        } else if (status != null) {
            employees = empService.findByStatus(status);
        } else if (name != null) {
            employees = empService.searchByName(name);
        } else {
            employees = empService.findAllEmployees();
        }
        model.addAttribute("employees", employees);
        return "emp/list";
    }

    // 顯示新增員工表單
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("empVO", new EmpVO());
        return "emp/add";
    }

    // 處理新增員工
    @PostMapping("/add")
    public String addEmployee(@Valid @ModelAttribute("empVO") EmpVO empVO,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "emp/add";
        }
        
        try {
            empService.addEmployee(empVO);
            redirectAttributes.addFlashAttribute("message", "員工新增成功！");
            return "redirect:/emp/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("empAccount", "error.empVO", e.getMessage());
            return "emp/add";
        }
    }

    // 顯示編輯員工表單
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        empService.findById(id).ifPresent(emp -> model.addAttribute("empVO", emp));
        return "emp/edit";
    }

    // 處理編輯員工
    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable Integer id,
                               @Valid @ModelAttribute("empVO") EmpVO empVO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "emp/edit";
        }

        try {
            empVO.setEmpId(id);
            empService.updateEmployee(empVO);
            redirectAttributes.addFlashAttribute("message", "員工資料更新成功！");
            return "redirect:/emp/list";
        } catch (IllegalArgumentException e) {
            result.rejectValue("empAccount", "error.empVO", e.getMessage());
            return "emp/edit";
        }
    }

    // 處理停用/啟用員工
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Integer id,
                             @RequestParam Boolean activate,
                             RedirectAttributes redirectAttributes) {
        try {
            if (activate) {
                empService.activateEmployee(id);
                redirectAttributes.addFlashAttribute("message", "員工帳號已啟用！");
            } else {
                empService.deactivateEmployee(id);
                redirectAttributes.addFlashAttribute("message", "員工帳號已停用！");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/emp/list";
    }
}