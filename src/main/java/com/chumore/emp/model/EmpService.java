package com.chumore.emp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpService {
    
    @Autowired
    private EmpRepository empRepository;
    
    // 新增員工
    public EmpVO addEmployee(EmpVO empVO) {
        // 檢查帳號是否已存在
        if (empRepository.existsByEmpAccount(empVO.getEmpAccount())) {
            throw new IllegalArgumentException("帳號已存在");
        }
        
        // 設置創建時間和更新時間
        LocalDateTime now = LocalDateTime.now();
        empVO.setEmpCreateTime(now);
        empVO.setEmpUpdateTime(now);
        
        // 如果沒有設置帳號狀態，預設為啟用(1)
        if (empVO.getEmpAccountStatus() == null) {
            empVO.setEmpAccountStatus(1);
        }
        
        return empRepository.save(empVO);
    }
    
    // 更新員工資訊
    public EmpVO updateEmployee(EmpVO empVO) {
        // 確認員工存在
        EmpVO existingEmp = empRepository.findById(empVO.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("找不到該員工"));
        
        // 如果要更改帳號，檢查新帳號是否已存在
        if (!existingEmp.getEmpAccount().equals(empVO.getEmpAccount()) &&
            empRepository.existsByEmpAccount(empVO.getEmpAccount())) {
            throw new IllegalArgumentException("新帳號已存在");
        }
        
        return empRepository.save(empVO);
    }
    
    // 根據ID查詢員工
    public Optional<EmpVO> findById(Integer empId) {
        return empRepository.findById(empId);
    }
    
    // 根據帳號查詢員工
    public Optional<EmpVO> findByAccount(String account) {
        return empRepository.findByEmpAccount(account);
    }
    
    // 查詢所有員工
    public List<EmpVO> findAllEmployees() {
        return empRepository.findAll();
    }
    
    // 根據狀態查詢員工
    public List<EmpVO> findByStatus(Integer status) {
        return empRepository.findByEmpAccountStatus(status);
    }
    
    // 根據姓名模糊查詢
    public List<EmpVO> searchByName(String name) {
        return empRepository.findByEmpNameContaining(name);
    }
    
    // 根據狀態和姓名查詢
    public List<EmpVO> searchByStatusAndName(Integer status, String name) {
        return empRepository.findByEmpAccountStatusAndEmpNameContaining(status, name);
    }
    
    // 停用員工帳號
    public EmpVO deactivateEmployee(Integer empId) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該員工"));
        emp.setEmpAccountStatus(0);
        return empRepository.save(emp);
    }
    
    // 啟用員工帳號
    public EmpVO activateEmployee(Integer empId) {
        EmpVO emp = empRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("找不到該員工"));
        emp.setEmpAccountStatus(1);
        return empRepository.save(emp);
    }
    
    // 檢查帳號是否存在
    public boolean isAccountExists(String account) {
        return empRepository.existsByEmpAccount(account);
    }
}