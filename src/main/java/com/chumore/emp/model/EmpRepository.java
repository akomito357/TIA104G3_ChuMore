package com.chumore.emp.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {
    
    // 根據帳號查找員工
    Optional<EmpVO> findByEmpAccount(String empAccount);
    
    // 根據帳號狀態查找員工列表
    List<EmpVO> findByEmpAccountStatus(Integer empAccountStatus);
    
    // 根據員工姓名模糊查詢
    List<EmpVO> findByEmpNameContaining(String empName);
    
    // 檢查帳號是否已存在
    boolean existsByEmpAccount(String empAccount);
    
    // 根據帳號狀態和姓名模糊查詢
    List<EmpVO> findByEmpAccountStatusAndEmpNameContaining(Integer empAccountStatus, String empName);
}