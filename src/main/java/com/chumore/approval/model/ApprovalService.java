package com.chumore.approval.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ApprovalService {

	@Autowired
    private  ApprovalRepository approvalRepository;
    
    //透過審核結果列出餐廳
    public List<ApprovalVO> findByApprovalResult(Integer approvalResult) {
        return approvalRepository.findByApprovalResult(approvalResult);
    }
    //新增修改
    public ApprovalVO saveApproval(ApprovalVO approvalVO) {
        return approvalRepository.save(approvalVO);
    }
}
