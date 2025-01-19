package com.chumore.approval.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalVO, Integer> {
    
    // 根據approvalResult查詢
    List<ApprovalVO> findByApprovalResult(Integer approvalResult);

    // 根據餐廳ID查詢
    List<ApprovalVO> findByRestRestId(Integer restId);
}
