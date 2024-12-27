package com.chumore.approval.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.approval.model.ApprovalService;
import com.chumore.approval.model.ApprovalVO;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/result/{approvalResult}")
    public List<ApprovalVO> getApprovalsByResult(@PathVariable Integer approvalResult) {
        return approvalService.findByApprovalResult(approvalResult);
    }

    @PostMapping
    public ApprovalVO saveApproval(@RequestBody ApprovalVO approvalVO) {
        return approvalService.saveApproval(approvalVO);
    }

}
