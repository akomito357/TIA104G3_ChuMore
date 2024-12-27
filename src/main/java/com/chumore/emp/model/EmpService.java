package com.chumore.emp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmpService {

    private final EmpRepository empRepository;

    @Autowired
    public EmpService(EmpRepository empRepository) {
        this.empRepository = empRepository;
    }

    // 新增員工
    public EmpVO addEmployee(EmpVO empVO) {
        // 設置建立時間
        empVO.setEmpCreateTime(java.time.LocalDateTime.now());
        empVO.setEmpUpdateTime(java.time.LocalDateTime.now());
        return empRepository.save(empVO); // 自動執行新增
    }

    // 更新員工資料
    public EmpVO updateEmployee(EmpVO empVO) {
        Optional<EmpVO> existingEmployee = empRepository.findById(empVO.getEmpId());
        if (existingEmployee.isPresent()) {
            return empRepository.save(empVO); // 自動執行更新
        } else {
            throw new RuntimeException("員工未找到，ID：" + empVO.getEmpId());
        }
    }


    // 查詢單一員工
    public EmpVO getEmployeeById(Integer empId) {
        return empRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("員工未找到，ID：" + empId));
    }

    // 查詢所有員工
    public List<EmpVO> getAllEmployees() {
        return empRepository.findAll();
    }

    // 根據姓名模糊查詢
    public List<EmpVO> searchEmployeesByName(String name) {
        return empRepository.findByEmpNameContainingIgnoreCase(name);
    }
}
