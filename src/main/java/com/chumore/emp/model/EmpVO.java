package com.chumore.emp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "emp")
public class EmpVO implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id //PK
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "emp_id")
   private Integer empId;
   
   //員工姓名，不可為空，長度限制50
   @Column(name = "emp_name", nullable = false, length = 50)
   @NotEmpty(message = "員工姓名: 請勿空白")
   @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,50}$", message = "員工姓名: 只能是中文字且長度必需在2到50之間")
   private String empName;

   //員工帳號，不可為空，長度限制50，不可與他人重複
   @Column(name = "emp_account", nullable = false, length = 50, unique = true)
   @NotEmpty(message = "帳號: 請勿空白")
   @Pattern(regexp = "^[a-zA-Z0-9]{6,50}$", message = "帳號: 只能是英文字母和數字的組合，且長度必需在6到50之間")
   private String empAccount;
   
   //員工密碼，不可為空，長度限制60
   @Column(name = "emp_password", nullable = false, length = 60)
   
   private String empPassword;
   
   //員工手機，不可為空，長度20，不可與他人重複
   @Column(name = "emp_phone", nullable = false, length = 20, unique = true)
   @NotEmpty(message = "手機號碼: 請勿空白")
   @Pattern(regexp = "^09\\d{8}$", message = "手機號碼: 請輸入正確的台灣手機號碼格式")
   private String empPhone;
   
   //員工信箱，不可為空，長度100，不可與他人重複
   @Column(name = "emp_email", nullable = false, length = 100, unique = true)
   @NotEmpty(message = "Email: 請勿空白")
   @Email(message = "Email: 請輸入正確的email格式")
   private String empEmail;
   
   //員工帳號狀態，不可為空，型別為TINYINT，0為停用，1為啟用，預設為1
   @Column(name = "emp_account_status", nullable = false, columnDefinition = "TINYINT")
   @NotNull(message = "帳號狀態: 請勿空白")
   @Min(value = 0, message = "帳號狀態: 必須為0或1")
   @Max(value = 1, message = "帳號狀態: 必須為0或1")
   private Integer empAccountStatus;
   
   //員工權限角色，不可為空，型別為TINYINT，0為一般員工，1為管理員，預設為0
   @Column(name = "emp_role", nullable = false, columnDefinition = "TINYINT")
   @NotNull(message = "權限角色: 請勿空白")
   @Min(value = 0, message = "權限角色: 必須為0或1")
   @Max(value = 1, message = "權限角色: 必須為0或1")
   private Integer empRole;
   
   //員工到職日，不可為空
   @Column(name = "emp_hire_date", nullable = false)
   @NotNull(message = "到職日: 請勿空白")
   private LocalDate empHireDate;
   
   //員工離職日，可為空
   @Column(name = "emp_resign_date")
   private LocalDate empResignDate;
   
   //帳號建立時間，不可為空，不可修改
   @Column(name = "emp_create_time", nullable = false, updatable = false)
   private LocalDateTime empCreateTime;
   
   //帳號建立時間，不可為空
   @Column(name = "emp_update_time", nullable = false)
   private LocalDateTime empUpdateTime;

   //無參數建構子
   public EmpVO() {
   }

   // 在資料新增前執行
   @PrePersist
   protected void onCreate() {
       empCreateTime = LocalDateTime.now();
       empUpdateTime = LocalDateTime.now();
   }

   // 在資料更新前執行
   @PreUpdate
   protected void onUpdate() {
       empUpdateTime = LocalDateTime.now();
   }

   // Getters and Setters
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

   public String getEmpPhone() {
       return empPhone;
   }

   public void setEmpPhone(String empPhone) {
       this.empPhone = empPhone;
   }

   public String getEmpEmail() {
       return empEmail;
   }

   public void setEmpEmail(String empEmail) {
       this.empEmail = empEmail;
   }

   public Integer getEmpAccountStatus() {
       return empAccountStatus;
   }

   public void setEmpAccountStatus(Integer empAccountStatus) {
       this.empAccountStatus = empAccountStatus;
   }

   public Integer getEmpRole() {
       return empRole;
   }

   public void setEmpRole(Integer empRole) {
       this.empRole = empRole;
   }

   public LocalDate getEmpHireDate() {
       return empHireDate;
   }

   public void setEmpHireDate(LocalDate empHireDate) {
       this.empHireDate = empHireDate;
   }

   public LocalDate getEmpResignDate() {
       return empResignDate;
   }

   public void setEmpResignDate(LocalDate empResignDate) {
       this.empResignDate = empResignDate;
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

   // toString 方法
   @Override
   public String toString() {
       return "EmpVO{" +
               "empId=" + empId +
               ", empName='" + empName + '\'' +
               ", empAccount='" + empAccount + '\'' +
               ", empPassword='" + empPassword + '\'' +
               ", empPhone='" + empPhone + '\'' +
               ", empEmail='" + empEmail + '\'' +
               ", empAccountStatus=" + empAccountStatus +
               ", empRole=" + empRole +
               ", empHireDate=" + empHireDate +
               ", empResignDate=" + empResignDate +
               ", empCreateTime=" + empCreateTime +
               ", empUpdateTime=" + empUpdateTime +
               '}';
   }
}