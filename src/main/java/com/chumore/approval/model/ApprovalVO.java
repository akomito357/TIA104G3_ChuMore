package com.chumore.approval.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import com.chumore.emp.model.EmpVO;
import com.chumore.rest.model.RestVO;

@Entity
@Table(name = "approval")
public class ApprovalVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // PK自增
	@Column(name = "approval_id")
	private Integer approvalId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rest_id", referencedColumnName = "rest_id", nullable = false)
	private RestVO rest; // 對應餐廳ID (FK)

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_id", referencedColumnName = "emp_id", nullable = false)
	private EmpVO emp; // 對應員工ID (FK)

	@Column(name = "approval_result", nullable = false)
	private Integer approvalResult;

	@Column(name = "submission_datetime", nullable = false)
	private LocalDateTime submissionDatetime;

	@Column(name = "approval_req_datetime", nullable = false)
	private LocalDateTime approvalReqDatetime;

	// 無參數建構子
	public ApprovalVO() {
	}

	  // 全參數建構子
    public ApprovalVO(Integer approvalId, RestVO rest, EmpVO emp, Integer approvalResult,
                      LocalDateTime submissionDatetime, LocalDateTime approvalReqDatetime) {
        this.approvalId = approvalId;
        this.rest = rest;
        this.emp = emp;
        this.approvalResult = approvalResult;
        this.submissionDatetime = submissionDatetime;
        this.approvalReqDatetime = approvalReqDatetime;
    }

    // Getter 和 Setter
    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public RestVO getRest() {
        return rest;
    }

    public void setRest(RestVO rest) {
        this.rest = rest;
    }

    public EmpVO getEmp() {
        return emp;
    }

    public void setEmp(EmpVO emp) {
        this.emp = emp;
    }

    public Integer getApprovalResult() {
        return approvalResult;
    }

    public void setApprovalResult(Integer approvalResult) {
        this.approvalResult = approvalResult;
    }

    public LocalDateTime getSubmissionDatetime() {
        return submissionDatetime;
    }

    public void setSubmissionDatetime(LocalDateTime submissionDatetime) {
        this.submissionDatetime = submissionDatetime;
    }

    public LocalDateTime getApprovalReqDatetime() {
        return approvalReqDatetime;
    }

    public void setApprovalReqDatetime(LocalDateTime approvalReqDatetime) {
        this.approvalReqDatetime = approvalReqDatetime;
    }

    @Override
    public String toString() {
        return "ApprovalVO{" +
                "approvalId=" + approvalId +
                ", rest=" + (rest != null ? rest.getRestId() : null) +
                ", emp=" + (emp != null ? emp.getEmpId() : null) +
                ", approvalResult=" + approvalResult +
                ", submissionDatetime=" + submissionDatetime +
                ", approvalReqDatetime=" + approvalReqDatetime +
                '}';
    }
}
