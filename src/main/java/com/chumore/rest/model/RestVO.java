package com.chumore.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chumore.cuisinetype.model.CuisineTypeVO;

@Entity
@Table(name = "rest")
public class RestVO implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rest_id", updatable = false, insertable = false, nullable = false)
	private Integer restId;
	
	@Column(name = "rest_name")
	private String restName;
	
	@Column(name = "rest_city")
	private String restCity;
	
	@Column(name = "rest_dist")
	private String restDist;
	
	@Column(name = "rest_address")
	private String restAddress;
	
	@Column(name = "rest_regist", unique = true)
	private String restRegist;
	
	@Column(name = "rest_phone")
	private String restPhone;
	
//	@Column(name = "cuisine_type_id")
//	private Integer cuisineTypeId;
	
	@ManyToOne
	@JoinColumn(name = "cuisine_type_id", referencedColumnName = "cuisine_type_id")
	private CuisineTypeVO cuisineType;
	
	@Column(name = "rest_intro", columnDefinition = "text")
	private String restIntro;
	
	@Column(name = "merchant_name")
	private String merchantName;
	
	@Column(name = "merchant_id_number", columnDefinition = "char(10)", unique = true)
	private String merchantIdNumber;
	
	@Column(name = "merchant_email", unique = true)
	private String merchantEmail;
	
	@Column(name = "merchant_password")
	private String merchantPassword;
	
	@Column(name = "phone_number", unique = true)
	private String phoneNumber;
	
	@Column(name = "business_status", columnDefinition = "tinyint")
	private Integer businessStatus;
	
	@Column(name = "weekly_biz_days")
	private String weeklyBizDays;
	
	@Column(name = "business_hours")
	private String businessHours;
	
	@Column(name = "order_table_count")
	private Integer orderTableCount;
	
	@Column(name = "rest_stars", columnDefinition = "DECIMAL")
	private Double restStars;
	
	@Column(name = "rest_reviewers")
	private Integer restReviewers;
	
	@Column(name = "approval_status", columnDefinition = "TINYINT")
	private Integer approvalStatus;
	
	@Column(name = "register_datetime", insertable = false, updatable = false, columnDefinition = "DATETIME CURRENT TIMESTAMP")
	private Timestamp registerDatetime;
	
	@Column(name = "created_datetime", insertable = false, columnDefinition = "DATETIME")
	private Timestamp createdDatetime;
	
	@Column(name = "updated_datetime", columnDefinition = "DATETIME CURRENT TIMESTAMP")
	private Timestamp updatedDatetime;
	
//	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
//	private Set<DiscPtsVO> discPts;
	
//	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
//	private Set<>
	
	
//	public RestVO() {	
//	}
	
	public Integer getRestId() {
		return restId;
	}
	public void setRestId(Integer restId) {
		this.restId = restId;
	}
	public String getRestName() {
		return restName;
	}
	public void setRestName(String restName) {
		this.restName = restName;
	}
	public String getRestCity() {
		return restCity;
	}
	public void setRestCity(String restCity) {
		this.restCity = restCity;
	}
	public String getRestDist() {
		return restDist;
	}
	public void setRestDist(String restDist) {
		this.restDist = restDist;
	}
	public String getRestAddress() {
		return restAddress;
	}
	public void setRestAddress(String restAddress) {
		this.restAddress = restAddress;
	}
	public String getRestRegist() {
		return restRegist;
	}
	public void setRestRegist(String restRegist) {
		this.restRegist = restRegist;
	}
	public String getRestPhone() {
		return restPhone;
	}
	public void setRestPhone(String restPhone) {
		this.restPhone = restPhone;
	}
	
	public CuisineTypeVO getCuisineType() {
		return cuisineType;
	}
	
	public void setCuisineType(CuisineTypeVO cuisineType) {
		this.cuisineType = cuisineType;
	}
	
//	public Integer getCuisineTypeId() {
//		return cuisineTypeId;
//	}
//	public void setCuisineTypeId(Integer cuisineTypeId) {
//		this.cuisineTypeId = cuisineTypeId;
//	}
	
	public String getRestIntro() {
		return restIntro;
	}
	public void setRestIntro(String restIntro) {
		this.restIntro = restIntro;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getMerchantIdNumber() {
		return merchantIdNumber;
	}
	public void setMerchantIdNumber(String merchantIdNumber) {
		this.merchantIdNumber = merchantIdNumber;
	}
	public String getMerchantEmail() {
		return merchantEmail;
	}
	public void setMerchantEmail(String merchantEmail) {
		this.merchantEmail = merchantEmail;
	}
	public String getMerchantPassword() {
		return merchantPassword;
	}
	public void setMerchantPassword(String merchantPassword) {
		this.merchantPassword = merchantPassword;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Integer getBusinessStatus() {
		return businessStatus;
	}
	public void setBusinessStatus(Integer businessStatus) {
		this.businessStatus = businessStatus;
	}
	public String getWeeklyBizDays() {
		return weeklyBizDays;
	}
	public void setWeeklyBizDays(String weeklyBizDays) {
		this.weeklyBizDays = weeklyBizDays;
	}
	public String getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(String businessHours) {
		this.businessHours = businessHours;
	}
	public Integer getOrderTableCount() {
		return orderTableCount;
	}
	public void setOrderTableCount(Integer orderTableCount) {
		this.orderTableCount = orderTableCount;
	}
	public Double getRestStars() {
		return restStars;
	}
	public void setRestStars(Double restStars) {
		this.restStars = restStars;
	}
	public Integer getRestReviewers() {
		return restReviewers;
	}
	public void setRestReviewers(Integer restReviewers) {
		this.restReviewers = restReviewers;
	}
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public Timestamp getRegisterDatetime() {
		return registerDatetime;
	}
	public void setRegisterDatetime(Timestamp registerDatetime) {
		this.registerDatetime = registerDatetime;
	}
	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public Timestamp getUpdatedDatetime() {
		return updatedDatetime;
	}
	public void setUpdatedDatetime(Timestamp updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}
	@Override
	public String toString() {
		return "RestVO [restId=" + restId + ", restName=" + restName + ", restCity=" + restCity + ", restDist="
				+ restDist + ", restAddress=" + restAddress + ", restRegist=" + restRegist + ", restPhone=" + restPhone
				+ ", cuisineType=" + cuisineType + ", restIntro=" + restIntro + ", merchantName=" + merchantName
				+ ", merchantIdNumber=" + merchantIdNumber + ", merchantEmail=" + merchantEmail + ", merchantPassword="
				+ merchantPassword + ", phoneNumber=" + phoneNumber + ", businessStatus=" + businessStatus
				+ ", weeklyBizDays=" + weeklyBizDays + ", businessHours=" + businessHours + ", orderTableCount="
				+ orderTableCount + ", restStars=" + restStars + ", restReviewers=" + restReviewers
				+ ", approvalStatus=" + approvalStatus + ", registerDatetime=" + registerDatetime + ", createdDatetime="
				+ createdDatetime + ", updatedDatetime=" + updatedDatetime + "]";
	}
	
	
	
}
