package com.chumore.emp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.chumore.emp.model.EmpVO;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpBasicViewDTO {
    private String empName;
    private String empAccount;
    private String empPhone;
    private String empEmail;

    // 從 EmpVO 轉換為 DTO 的靜態方法
    public static EmpBasicViewDTO fromEntity(EmpVO empVO) {
        return new EmpBasicViewDTO(
            empVO.getEmpName(),
            empVO.getEmpAccount(),
            empVO.getEmpPhone(),
            empVO.getEmpEmail()
        );
    }
}