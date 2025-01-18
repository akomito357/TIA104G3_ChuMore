package com.chumore.rest.model;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.chumore.approval.model.ApprovalVO;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.dailyreservation.model.DailyReservationVO;
import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.envimg.model.EnvImgVO;
import com.chumore.favrest.model.FavRestVO;
import com.chumore.menuimg.model.MenuImgVO;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordertable.model.OrderTableVO;
import com.chumore.product.model.ProductVO;
import com.chumore.productcategory.model.ProductCategoryVO;
import com.chumore.reservation.model.ReservationVO;
import com.chumore.review.model.ReviewVO;
import com.chumore.tabletype.model.TableTypeVO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "rest")
public class RestVO implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rest_id", updatable = false, insertable = false, nullable = false)
	private Integer restId;
	
	@Column(name = "rest_name")
	@NotEmpty(message = "請填寫餐廳名稱")
	private String restName;
	
	@Column(name = "rest_city")
	@NotEmpty(message = "請輸入餐廳所在縣市")
	private String restCity;
	
	@Column(name = "rest_dist")
	@NotEmpty(message = "請輸入餐廳所在鄉鎮市區")
	private String restDist;
	
	@Column(name = "rest_address")
	@NotEmpty(message = "請輸入餐廳地址")
	private String restAddress;
	
	@Column(name = "rest_regist", unique = true)
	@NotEmpty(message = "請輸入食品業者登錄字號")
	@Pattern(regexp = "^[A-Za-z]-[0-9]{9}-[0-9]{5}-[0-9]{1}$", message = "登錄字號格式錯誤！請輸入如：B-123456789-12345-1之格式")
	private String restRegist;
	
	@Column(name = "rest_phone")
	@NotEmpty(message = "請輸入餐廳聯絡電話")
	private String restPhone;
	
//	@Column(name = "cuisine_type_id")
//	private Integer cuisineTypeId;
	
	@ManyToOne
	@JoinColumn(name = "cuisine_type_id", referencedColumnName = "cuisine_type_id")
	@NotEmpty(message = "請輸入餐廳料理類型")
	@JsonBackReference("cuisineType-rest")
	private CuisineTypeVO cuisineType;
	
	@Column(name = "rest_intro", columnDefinition = "text")
	private String restIntro;
	
	@Column(name = "merchant_name")
	@NotEmpty(message = "請輸入負責人姓名")
	@Pattern(regexp = "^(\u4e00-\u9fa5)(A-Za-z0-9_)$", message = "負責人姓名只能是中、英文字母、數字和_")
	private String merchantName;
	
	@Column(name = "merchant_id_number", columnDefinition = "char(10)", unique = true)
	@NotEmpty(message = "請輸入負責人身分證字號")
	@Pattern(regexp = "^[A-Za-z]{1}[12]{1}[0-9]{8}$", message = "負責人身分證字號格式錯誤")
	private String merchantIdNumber;
	
	@Column(name = "merchant_email", unique = true)
	@NotEmpty(message = "請輸入負責人電子信箱（登入帳號）")
	private String merchantEmail;
	
	@Column(name = "merchant_password")
	@NotEmpty(message = "請輸入登入密碼")
	private String merchantPassword;
	
	@Column(name = "phone_number", unique = true)
	@NotEmpty(message = "請輸入負責人手機號碼")
	@Pattern(regexp = "^09[0-9]{8}$", message = "負責人手機號碼輸入格式錯誤")
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
	private LocalDateTime registerDatetime;
	
	@Column(name = "created_datetime", insertable = false, columnDefinition = "DATETIME")
	private LocalDateTime createdDatetime;
	
	@Column(name = "updated_datetime", columnDefinition = "DATETIME CURRENT TIMESTAMP")
	private LocalDateTime updatedDatetime;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("discPts-rest")
	private Set<DiscPtsVO> discPts;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("orderMaster-rest")
	private Set<OrderMasterVO> orderMasters;
	
	@OneToMany(mappedBy = "restId", cascade = CascadeType.ALL)
	private Set<ProductVO> products;
	
	@OneToMany(mappedBy = "restId", cascade = CascadeType.ALL)
	private Set<ProductCategoryVO> productCategories;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("orderTable-rest")
	private Set<OrderTableVO> orderTables;

	@OneToMany(mappedBy="rest",cascade=CascadeType.ALL)
	@JsonManagedReference("reservation-rest")
	private Set<ReservationVO> reservations;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("approval-rest")
	private Set<ApprovalVO> approvals;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("dailyReservation-rest")
	private Set<DailyReservationVO> dailyReservations;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("review-rest")
	private Set<ReviewVO> reviews;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("envImg-rest")
	private Set<EnvImgVO> envImgs;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("menuImg-rest")
	private Set<MenuImgVO> menuImgs;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("tableType-rest")
	private Set<TableTypeVO> tableTypes;
	
	@OneToMany(mappedBy = "rest", cascade = CascadeType.ALL)
	@JsonManagedReference("favRest-rest")
	private Set<FavRestVO> favRests;
	
	public String getCuisineTypeName() {
		return cuisineType.getCuisineDescr();	
	}

	public RestVO() {	
	}
	
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
	public LocalDateTime getRegisterDatetime() {
		return registerDatetime;
	}

	public void setRegisterDatetime(LocalDateTime registerDatetime) {
		this.registerDatetime = registerDatetime;
	}

	public LocalDateTime getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(LocalDateTime createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public LocalDateTime getUpdatedDatetime() {
		return updatedDatetime;
	}

	public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public Set<DiscPtsVO> getDiscPts() {
		return discPts;
	}

	public void setDiscPts(Set<DiscPtsVO> discPts) {
		this.discPts = discPts;
	}
	
	public Set<OrderMasterVO> getOrderMasters() {
		return orderMasters;
	}

	public void setOrderMasters(Set<OrderMasterVO> orderMasters) {
		this.orderMasters = orderMasters;
	}

	public Set<ReservationVO> getReservations() {
		return reservations;
	}

	public void setReservations(Set<ReservationVO> reservations) {
		this.reservations = reservations;
	}

	public Set<ApprovalVO> getApprovals() {
		return approvals;
	}

	public void setApprovals(Set<ApprovalVO> approvals) {
		this.approvals = approvals;
	}

	public Set<EnvImgVO> getEnvImgs() {
		return envImgs;
	}

	public void setEnvImgs(Set<EnvImgVO> envImgs) {
		this.envImgs = envImgs;
	}

	public Set<TableTypeVO> getTableTypes() {
		return tableTypes;
	}

	public void setTableTypes(Set<TableTypeVO> tableTypes) {
		this.tableTypes = tableTypes;
	}

	public Set<FavRestVO> getFavRests() {
		return favRests;
	}

	public void setFavRests(Set<FavRestVO> favRests) {
		this.favRests = favRests;
	}
	
	public Set<ProductVO> getProducts() {
		return products;
	}

	public void setProducts(Set<ProductVO> products) {
		this.products = products;
	}

	public Set<ProductCategoryVO> getProductCategories() {
		return productCategories;
	}

	public void setProductCategories(Set<ProductCategoryVO> productCategories) {
		this.productCategories = productCategories;
	}

	public Set<DailyReservationVO> getDailyReservations() {
		return dailyReservations;
	}

	public void setDailyReservations(Set<DailyReservationVO> dailyReservations) {
		this.dailyReservations = dailyReservations;
	}

	public Set<ReviewVO> getReviews() {
		return reviews;
	}

	public void setReviews(Set<ReviewVO> reviews) {
		this.reviews = reviews;
	}
	
	public Set<MenuImgVO> getMenuImgs() {
		return menuImgs;
	}

	public void setMenuImgs(Set<MenuImgVO> menuImgs) {
		this.menuImgs = menuImgs;
	}
	
	

	public Set<OrderTableVO> getOrderTables() {
		return orderTables;
	}

	public void setOrderTables(Set<OrderTableVO> orderTables) {
		this.orderTables = orderTables;
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