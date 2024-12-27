package com.chumore.emp.controller;

import com.chumore.emp.model.EmpVO;
import com.chumore.emp.model.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")  
public class EmpController {

    private final EmpService empService;

    @Autowired
    public EmpController(EmpService empService) {
        this.empService = empService;
    }

    // 新增員工
    @PostMapping
    public EmpVO addEmployee(@RequestBody EmpVO empVO) {
        return empService.addEmployee(empVO);
    }

    // 更新員工資料
    @PutMapping("/{empId}")
    public EmpVO updateEmployee(@PathVariable Integer empId, @RequestBody EmpVO empVO) {
        empVO.setEmpId(empId); // 確保更新的員工 ID 是正確的
        return empService.updateEmployee(empVO);
    }

    // 查詢單一員工
    @GetMapping("/{empId}")
    public EmpVO getEmployeeById(@PathVariable Integer empId) {
        return empService.getEmployeeById(empId);
    }

    // 查詢所有員工
    @GetMapping
    public List<EmpVO> getAllEmployees() {
        return empService.getAllEmployees();
    }

    // 根據姓名模糊查詢
    @GetMapping("/search")
    public List<EmpVO> searchEmployeesByName(@RequestParam String name) {
        return empService.searchEmployeesByName(name);
    }
}
