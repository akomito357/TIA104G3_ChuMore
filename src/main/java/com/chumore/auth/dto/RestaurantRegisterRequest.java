package com.chumore.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RestaurantRegisterRequest {

    @NotBlank(message = "請輸入負責人姓名")
    @Size(max = 50, message = "負責人姓名不能超過50個字元")
    private String name;

    @NotBlank(message = "請輸入電子信箱")
    @Email(message = "請輸入有效的電子信箱格式")
    private String email;

    @NotBlank(message = "請輸入密碼")
    @Size(min = 8, max = 20, message = "密碼長度必須在8到20個字元之間")
    private String password;

    @NotBlank(message = "請再次輸入密碼")
    private String confirmPassword;

    @NotBlank(message = "請輸入餐廳名稱")
    @Size(max = 100, message = "餐廳名稱不能超過100個字元")
    private String restaurantName;

    @NotBlank(message = "請選擇縣市")
    private String city;

    @NotBlank(message = "請選擇地區")
    private String district;

    @NotBlank(message = "請輸入詳細地址")
    @Size(max = 100, message = "詳細地址不能超過100個字元")
    private String addressDetail;

    @NotBlank(message = "請輸入營業電話")
    @Pattern(regexp = "^0[0-9]{8,9}$", message = "請輸入有效的營業電話號碼")
    private String businessPhone;

    @NotBlank(message = "請輸入負責人手機號碼")
    @Pattern(regexp = "^09[0-9]{8}$", message = "請輸入有效的手機號碼格式")
    private String phoneNumber;

    @NotBlank(message = "請輸入食品業者登錄字號")
    @Pattern(regexp = "^[A-Za-z]-[0-9]{9}-[0-9]{5}-[0-9]{1}$", 
            message = "登錄字號格式錯誤！請輸入如：B-123456789-12345-1之格式")
    private String registrationNumber;

    @NotBlank(message = "請選擇餐廳類型")
    private String cuisineType;

    @NotBlank(message = "請輸入負責人身分證字號")
    @Pattern(regexp = "^[A-Z][1-2][0-9]{8}$", message = "身分證字號格式錯誤！請輸入正確格式")
    private String merchantIdNumber;

    private Integer businessStatus = 1;

    // 預設建構子
    public RestaurantRegisterRequest() {}

    // 提供完整地址的工具方法
    public String getFullAddress() {
        return String.format("%s%s%s", city, district, addressDetail);
    }

    // Getters and Setters with null checks and trimming
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getCuisineType() {
		return cuisineType;
	}

	public void setCuisineType(String cuisineType) {
		this.cuisineType = cuisineType;
	}

	public String getMerchantIdNumber() {
		return merchantIdNumber;
	}

	public void setMerchantIdNumber(String merchantIdNumber) {
		this.merchantIdNumber = merchantIdNumber;
	}

	public Integer getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(Integer businessStatus) {
		this.businessStatus = businessStatus;
	}

    

    @Override
    public String toString() {
        return "RestaurantRegisterRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", merchantIdNumber='" + merchantIdNumber + '\'' +
                ", businessStatus=" + businessStatus +
                '}';
    }

}