package com.chumore.emp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import com.chumore.emp.model.EmpVO;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpFullDTO {
    private Integer empId;
    
    @NotEmpty(message = "員工姓名: 請勿空白")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,50}$", message = "員工姓名: 只能是中文字且長度必需在2到50之間")
    private String empName;
    
    @NotEmpty(message = "帳號: 請勿空白")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,50}$", message = "帳號: 只能是英文字母和數字的組合，且長度必需在6到50之間")
    private String empAccount;
    
    private String empPassword;
    
    @NotEmpty(message = "手機號碼: 請勿空白")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼: 請輸入正確的台灣手機號碼格式")
    private String empPhone;
    
    @NotEmpty(message = "Email: 請勿空白")
    @Email(message = "Email: 請輸入正確的email格式")
    private String empEmail;
    
    @NotNull(message = "帳號狀態: 請勿空白")
    private Integer empAccountStatus;
    
    @NotNull(message = "權限角色: 請勿空白")
    private Integer empRole;
    
    @NotNull(message = "到職日: 請勿空白")
    private LocalDate empHireDate;
    
    private LocalDate empResignDate;

    // 從 Entity 轉換為 DTO 的靜態方法
    public static EmpFullDTO fromEntity(EmpVO empVO) {
        if (empVO == null) {
            return null;
        }
        
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
    
    // 轉換為 Entity 的方法
    public EmpVO toEntity() {
        EmpVO empVO = new EmpVO();
        empVO.setEmpId(this.empId);
        empVO.setEmpName(this.empName);
        empVO.setEmpAccount(this.empAccount);
        // 密碼由 Service 層處理，這裡不設置
        empVO.setEmpPhone(this.empPhone);
        empVO.setEmpEmail(this.empEmail);
        empVO.setEmpAccountStatus(this.empAccountStatus);
        empVO.setEmpRole(this.empRole);
        empVO.setEmpHireDate(this.empHireDate);
        empVO.setEmpResignDate(this.empResignDate);
        return empVO;
    }
}