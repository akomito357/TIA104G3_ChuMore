package com.chumore.emp.dto;

import javax.validation.constraints.*;

import com.chumore.emp.model.EmpVO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpBasicUpdateDTO {
    
    @NotEmpty(message = "手機號碼: 請勿空白")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼: 請輸入正確的台灣手機號碼格式")
    private String empPhone;

    @NotEmpty(message = "Email: 請勿空白")
    @Email(message = "Email: 請輸入正確的email格式")
    private String empEmail;

    // 從 EmpVO 轉換為 DTO 的靜態方法
    public static EmpBasicUpdateDTO fromEntity(EmpVO empVO) {
        return new EmpBasicUpdateDTO(
            empVO.getEmpPhone(),
            empVO.getEmpEmail()
        );
    }
}