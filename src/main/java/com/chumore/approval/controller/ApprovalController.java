package com.chumore.approval.controller;

import com.chumore.approval.model.ApprovalVO;
import com.chumore.approval.model.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/emp/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDateTime.parse(text, formatter));
            }
        });
    }
    // 顯示審核清單頁面
    @GetMapping("/list")
    public String list(Model model) {
        // 取得待審核清單(approvalResult = 2 表示待審核)
        List<ApprovalVO> pendingApprovals = approvalService.findByApprovalResult(2);
        model.addAttribute("approvals", pendingApprovals);
        return "emp/approval/list";
    }

    // 顯示已審核清單頁面
    @GetMapping("/approved")
    public String approved(Model model) {
        // 取得已審核清單(approvalResult = 1 表示已審核通過)
        List<ApprovalVO> approvedList = approvalService.findByApprovalResult(1);
        model.addAttribute("approvals", approvedList);
        return "emp/approval/approved";
    }

    @PostMapping("/update")
    public String updateApproval(@ModelAttribute ApprovalVO approvalVO,
                               RedirectAttributes redirectAttributes) {
        try {
            // 獲取原有審核記錄
            ApprovalVO existingApproval = approvalService.findById(approvalVO.getApprovalId());
            
            // 設置審核時間為當前時間
            approvalVO.setApprovalReqDatetime(LocalDateTime.now());
            
            // 保留原有資料
            approvalVO.setRest(existingApproval.getRest());
            approvalVO.setEmp(existingApproval.getEmp());
            approvalVO.setSubmissionDatetime(existingApproval.getSubmissionDatetime());
            
            // 保存更新
            approvalService.saveApproval(approvalVO);
            redirectAttributes.addFlashAttribute("message", "審核更新成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "審核更新失敗: " + e.getMessage());
        }
        return "redirect:/emp/approval/list";
    }

    // 顯示更新表單頁面
    @GetMapping("/update/{approvalId}")
    public String showUpdateForm(@PathVariable Integer approvalId, Model model) {
        // 這裡需要在 Service 中添加 findById 方法
        ApprovalVO approval = approvalService.findById(approvalId);
        model.addAttribute("approval", approval);
        return "emp/approval/update";
    }

    // 根據餐廳ID查詢審核記錄
    @GetMapping("/rest/{restId}")
    public String getApprovalsByRestId(@PathVariable Integer restId, Model model) {
        List<ApprovalVO> approvals = approvalService.findByRest_RestId(restId);
        model.addAttribute("approvals", approvals);
        return "emp/approval/list";
    }

    // 顯示所有審核記錄
    @GetMapping("/all")
    public String showAll(Model model) {
        List<ApprovalVO> allApprovals = approvalService.findAll();
        model.addAttribute("approvals", allApprovals);
        return "emp/approval/all";
    }
}