package com.chumore.emp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import com.chumore.emp.model.EmpVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpFullDTO {
    private Integer empId;
    private String empName;
    private String empAccount;
    private String empPassword;  // 添加密碼欄位
    private String empPhone;
    private String empEmail;
    private Integer empAccountStatus;
    private Integer empRole;
    private LocalDate empHireDate;
    private LocalDate empResignDate;

    // 從 Entity 轉換為 DTO 的靜態方法
    public static EmpFullDTO fromEntity(EmpVO empVO) {
        return new EmpFullDTO(
            empVO.getEmpId(),
            empVO.getEmpName(),
            empVO.getEmpAccount(),
            null,  // 密碼不從資料庫讀取
            empVO.getEmpPhone(),
            empVO.getEmpEmail(),
            empVO.getEmpAccountStatus(),
            empVO.getEmpRole(),
            empVO.getEmpHireDate(),
            empVO.getEmpResignDate()
        );
    }
}