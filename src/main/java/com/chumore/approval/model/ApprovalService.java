package com.chumore.approval.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ApprovalService {

	@Autowired
	private ApprovalRepository approvalRepository;

	// 透過審核結果列出餐廳
	public List<ApprovalVO> findByApprovalResult(Integer approvalResult) {
		return approvalRepository.findByApprovalResult(approvalResult);
	}

	// 新增修改
	public ApprovalVO saveApproval(ApprovalVO approvalVO) {
		return approvalRepository.save(approvalVO);
	}

	// 查找所有審核紀錄
	public List<ApprovalVO> findAll() {
		return approvalRepository.findAll();
	}

	
	 // 根據ID查詢單筆審核記錄
    public ApprovalVO findById(Integer approvalId) {
        return approvalRepository.findById(approvalId)
                               .orElseThrow(() -> new RuntimeException("找不到審核記錄"));
    }
	// 根據餐廳ID查詢
    public List<ApprovalVO> findByRest_RestId(Integer restId) {
        return approvalRepository.findByRestRestId(restId);
    }
}
