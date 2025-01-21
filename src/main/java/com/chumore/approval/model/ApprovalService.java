package com.chumore.approval.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;

@Service
@Transactional
public class ApprovalService {

	@Autowired
	private ApprovalRepository approvalRepository;

	@Autowired
    private RestService restService;
	
	// 透過審核結果列出餐廳
	public List<ApprovalVO> findByApprovalResult(Integer approvalResult) {
		return approvalRepository.findByApprovalResult(approvalResult);
	}

	// 新增修改

	@Transactional
    public ApprovalVO saveApproval(ApprovalVO approvalVO) {
        // 儲存審核記錄
        ApprovalVO savedApproval = approvalRepository.save(approvalVO);
        
        // 更新餐廳的審核狀態
        RestVO rest = savedApproval.getRest();
        if (savedApproval.getApprovalResult() == 1) {
            // 審核通過
            rest.setApprovalStatus(1);
        } else if (savedApproval.getApprovalResult() == 2) {
            // 審核不通過
            rest.setApprovalStatus(0);
        }
        
        // 更新餐廳狀態
        restService.updateRest(rest);
        
        return savedApproval;
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
