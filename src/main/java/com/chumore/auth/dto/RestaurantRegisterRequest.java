package com.chumore.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RestaurantRegisterRequest extends RegisterRequest {

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

    // 餐廳負責人身分證字號
    @NotBlank(message = "請輸入負責人身分證字號")
    @Pattern(regexp = "^[A-Z][1-2][0-9]{8}$", message = "身分證字號格式錯誤！請輸入正確格式")
    private String merchantIdNumber;

    private Integer businessStatus = 1; // 預設為營業中狀態

    public RestaurantRegisterRequest() {
        super();
    }

    // Getters
    public String getRestaurantName() {
        return restaurantName;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public String getMerchantIdNumber() {
        return merchantIdNumber;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    // 提供完整地址的 getter 方法以便於資料庫儲存
    public String getFullAddress() {
        return String.format("%s%s%s", city, district, addressDetail);
    }

    // Setters
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName != null ? restaurantName.trim() : null;
    }

    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
    }

    public void setDistrict(String district) {
        this.district = district != null ? district.trim() : null;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail != null ? addressDetail.trim() : null;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone != null ? businessPhone.trim() : null;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber != null ? registrationNumber.trim() : null;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType != null ? cuisineType.trim() : null;
    }

    public void setMerchantIdNumber(String merchantIdNumber) {
        this.merchantIdNumber = merchantIdNumber != null ? merchantIdNumber.trim() : null;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    @Override
    public void setPhone(String phone) {
        super.setPhone(phone);
        this.setPhoneNumber(phone); // 確保 phoneNumber 與父類一致
    }

    @Override
    public String toString() {
        return "RestaurantRegisterRequest{" +
                "restaurantName='" + restaurantName + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", cuisineType='" + cuisineType + '\'' +
                ", merchantIdNumber='" + merchantIdNumber + '\'' +
                ", businessStatus=" + businessStatus +
                "} extends " + super.toString();
    }
}
