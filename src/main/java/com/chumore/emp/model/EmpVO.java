package com.chumore.emp.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "emp")
public class EmpVO implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // PK自增
	@Column(name = "emp_id", nullable = false)
	private Integer empId; // 流水號PK
	
	@Column(name = "emp_name", nullable = false, length = 255)
	private String empName; // 姓名
	
	@Column(name = "emp_account", nullable = false, length = 50, unique = true)
	private String empAccount; // 帳號
	
	@Column(name = "emp_password", nullable = false, length = 50) 
	private String empPassword; // 密碼
	
	@Column(name = "emp_account_status", nullable = false, columnDefinition = "TINYINT")
	private Integer empAccountStatus; // 帳號狀態 (true: 啟用, false: 停用)
	
	@Column(name = "emp_create_time", nullable = false, updatable = false)
	private LocalDateTime empCreateTime; // 帳號建立時間
	
	@Column(name = "emp_update_time", nullable = false)
	private LocalDateTime empUpdateTime; // 帳號更新時間

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

	@Override
	public String toString() {
		return "EmpVO{" + "empId=" + empId + ", empName='" + empName + '\'' + ", empAccount='" + empAccount + '\''
				+ ", empPassword='" + empPassword + '\'' + ", empAccountStatus=" + empAccountStatus + ", empCreateTime="
				+ empCreateTime + ", empUpdateTime=" + empUpdateTime + '}';
	}
}
