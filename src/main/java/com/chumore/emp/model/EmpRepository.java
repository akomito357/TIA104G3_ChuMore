package com.chumore.emp.model;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chumore.emp.dto.EmpBasicViewDTO;
import com.chumore.emp.dto.EmpFullDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {
    
    // 一般員工查詢自己的資料
    @Query("SELECT NEW com.chumore.emp.dto.EmpBasicViewDTO(e.empName, e.empAccount, e.empPhone, e.empEmail) " +
           "FROM EmpVO e WHERE e.empId = :empId")
    Optional<EmpBasicViewDTO> findBasicInfoById(@Param("empId") Integer empId);
    
    // 管理員查詢所有員工完整資料
    @Query("SELECT NEW com.chumore.emp.dto.EmpFullDTO(e.empId, e.empName, e.empAccount, e.empPassword, " +
           "e.empPhone, e.empEmail, e.empAccountStatus, e.empRole, e.empHireDate, e.empResignDate) " +
           "FROM EmpVO e")
    List<EmpFullDTO> findAllFullInfo();
    
    // 檢查帳號是否存在
    boolean existsByEmpAccount(String empAccount);
    
    // 檢查Email是否存在（用於更新時確認）
    boolean existsByEmpEmailAndEmpIdNot(String empEmail, Integer empId);
    
    // 檢查手機號碼是否存在（用於更新時確認）
    boolean existsByEmpPhoneAndEmpIdNot(String empPhone, Integer empId);
    
    // 根據帳號查找員工
    Optional<EmpVO> findByEmpAccount(String empAccount);
    
    // 查詢在職員工
    @Query("SELECT e FROM EmpVO e WHERE e.empResignDate IS NULL")
    List<EmpVO> findAllActiveEmps();
    
    // 根據角色查詢員工
    List<EmpVO> findByEmpRole(Integer empRole);
    
    boolean existsByEmpPhone(String empPhone);
    
    boolean existsByEmpEmail(String empEmail);
    
}