package com.chumore.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RestaurantDetailsRequest {

    @NotBlank(message = "餐廳名稱不能為空")
    @Size(max = 100, message = "餐廳名稱不能超過100個字元")
    private String restaurantName;

    @NotBlank(message = "營業地址不能為空")
    @Size(max = 200, message = "營業地址不能超過200個字元")
    private String businessAddress;

    @NotBlank(message = "營業電話不能為空")
    @Pattern(regexp = "^0[0-9]{8,9}$", message = "請輸入有效的電話號碼格式")
    private String businessPhone;

    @NotBlank(message = "統一編號不能為空")
    @Pattern(regexp = "^[0-9]{8}$", message = "請輸入有效的統一編號（8位數字）")
    private String businessRegistrationNumber;

    @NotBlank(message = "營業時間不能為空")
    private String businessHours;

    @Size(max = 500, message = "餐廳描述不能超過500個字元")
    private String restaurantDescription;

    // 營業狀態
    private Integer businessStatus;

    // 餐廳類型
    private String restaurantType;

    // Getter 和 Setter 方法
    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessRegistrationNumber() {
        return businessRegistrationNumber;
    }

    public void setBusinessRegistrationNumber(String businessRegistrationNumber) {
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public void setRestaurantDescription(String restaurantDescription) {
        this.restaurantDescription = restaurantDescription;
    }

    public Integer getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(Integer businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    @Override
    public String toString() {
        return "RestaurantDetailsRequest{" +
                "restaurantName='" + restaurantName + '\'' +
                ", businessAddress='" + businessAddress + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", businessRegistrationNumber='" + businessRegistrationNumber + '\'' +
                ", businessHours='" + businessHours + '\'' +
                ", restaurantDescription='" + restaurantDescription + '\'' +
                ", businessStatus=" + businessStatus +
                ", restaurantType='" + restaurantType + '\'' +
                '}';
    }
}