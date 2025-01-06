package com.chumore.emp.model;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "emp")
public class EmpVO implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    @Column(name = "emp_name", nullable = false, length = 255)
    @NotEmpty(message = "員工姓名: 請勿空白")
    @Size(min = 2, max = 255, message = "員工姓名: 長度需要在2到255個字元之間")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z\\s]+$", message = "員工姓名: 只能包含中文、英文字母和空格")
    private String empName;

    @Column(name = "emp_account", nullable = false, length = 50, unique = true)
    @NotEmpty(message = "帳號: 請勿空白")
    @Size(min = 6, max = 50, message = "帳號: 長度需要在6到50個字元之間")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "帳號: 只能包含英文字母、數字、點、底線和連字號")
    private String empAccount;

    @Column(name = "emp_password", nullable = false, length = 50)
    @NotEmpty(message = "密碼: 請勿空白")
    @Size(min = 8, max = 50, message = "密碼: 長度需要在8到50個字元之間")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$", 
            message = "密碼: 必須包含至少一個字母和一個數字")
    private String empPassword;

    @Column(name = "emp_account_status", nullable = false, columnDefinition = "TINYINT")
    @NotNull(message = "帳號狀態: 請勿空白")
    @Min(value = 0, message = "帳號狀態: 必須為0或1")
    @Max(value = 1, message = "帳號狀態: 必須為0或1")
    private Integer empAccountStatus;

    @Column(name = "emp_create_time", nullable = false, updatable = false)
    @NotNull(message = "建立時間: 請勿空白")
    @Past(message = "建立時間: 不能是未來時間")
    private LocalDateTime empCreateTime;

    @Column(name = "emp_update_time", nullable = false)
    @NotNull(message = "更新時間: 請勿空白")
    private LocalDateTime empUpdateTime;
    // 無參數建構子
    public EmpVO() {
    }

    // 全參數建構子
    public EmpVO(Integer empId, String empName, String empAccount, String empPassword, Integer empAccountStatus,
                 LocalDateTime empCreateTime, LocalDateTime empUpdateTime) {
        this.empId = empId;
        this.empName = empName;
        this.empAccount = empAccount;
        this.empPassword = empPassword;
        this.empAccountStatus = empAccountStatus;
        this.empCreateTime = empCreateTime;
        this.empUpdateTime = empUpdateTime;
    }

    // Getter跟Setter
    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpAccount() {
        return empAccount;
    }

    public void setEmpAccount(String empAccount) {
        this.empAccount = empAccount;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public void setEmpPassword(String empPassword) {
        this.empPassword = empPassword;
    }

    public Integer getEmpAccountStatus() {
        return empAccountStatus;
    }

    public void setEmpAccountStatus(Integer empAccountStatus) {
        this.empAccountStatus = empAccountStatus;
    }

    public LocalDateTime getEmpCreateTime() {
        return empCreateTime;
    }

    public void setEmpCreateTime(LocalDateTime empCreateTime) {
        this.empCreateTime = empCreateTime;
    }

    public LocalDateTime getEmpUpdateTime() {
        return empUpdateTime;
    }

    public void setEmpUpdateTime(LocalDateTime empUpdateTime) {
        this.empUpdateTime = empUpdateTime;
    }

    // 每次更新時自動設置 empUpdateTime 為當前時間
    @PreUpdate
    public void onUpdate() {
        this.empUpdateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "EmpVO{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", empAccount='" + empAccount + '\'' +
                ", empPassword='" + empPassword + '\'' +
                ", empAccountStatus=" + empAccountStatus +
                ", empCreateTime=" + empCreateTime +
                ", empUpdateTime=" + empUpdateTime +
                '}';
    }
}
