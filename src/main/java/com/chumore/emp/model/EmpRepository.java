package com.chumore.emp.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpRepository extends JpaRepository<EmpVO, Integer> {
	
	// 找員工姓名的模糊查詢，忽略大小寫
    List<EmpVO> findByEmpNameContainingIgnoreCase(String name);

}
