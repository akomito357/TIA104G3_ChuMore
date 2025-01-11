package com.chumore.auth.dto;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.validation.constraints.*;

public class RestRegisterRequest {
    
    @NotBlank(message = "請填寫餐廳名稱")
    @Size(min = 2, max = 50, message = "餐廳名稱長度需在2至50個字元之間")
    private String restName;
    
    @NotBlank(message = "請輸入餐廳所在縣市")
    private String restCity;
    
    @NotBlank(message = "請輸入餐廳所在鄉鎮市區")
    private String restDist;
    
    @NotBlank(message = "請輸入餐廳地址")
    @Size(min = 5, max = 100, message = "地址長度需在5至100個字元之間")
    private String restAddress;
    
    @NotBlank(message = "請輸入食品業者登錄字號")
    @Pattern(regexp = "^[A-Za-z]-[0-9]{9}-[0-9]{5}-[0-9]{1}$", 
            message = "登錄字號格式錯誤！請輸入如：B-123456789-12345-1之格式")
    private String restRegist;
    
    @NotBlank(message = "請輸入餐廳聯絡電話")
    @Pattern(regexp = "^(0[2-9]\\d{7,8}|09\\d{8})$", 
            message = "請輸入有效的市話或手機號碼")
    private String restPhone;
    
    @NotBlank(message = "請輸入負責人姓名")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9_]+$", 
            message = "負責人姓名只能是中、英文字母、數字和_")
    @Size(min = 2, max = 20, message = "姓名長度需在2至20個字元之間")
    private String merchantName;
    
    @NotBlank(message = "請輸入負責人身分證字號")
    @Pattern(regexp = "^[A-Za-z][12]\\d{8}$", 
            message = "負責人身分證字號格式錯誤")
    private String merchantIdNumber;
    
    @NotBlank(message = "請輸入負責人電子信箱")
    @Email(message = "請輸入有效的電子信箱格式")
    private String merchantEmail;
    
    @NotBlank(message = "請輸入密碼")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "密碼必須包含至少8個字元，且至少包含一個字母和一個數字")
    private String merchantPassword;
    
    @NotBlank(message = "請再次輸入密碼")
    private String confirmPassword;
    
    @NotBlank(message = "請輸入負責人手機號碼")
    @Pattern(regexp = "^09\\d{8}$", 
            message = "負責人手機號碼格式錯誤")
    private String phoneNumber;
    
    private String userType;
    private LocalDateTime createTime;

    // 預設建構子
    public RestRegisterRequest() {
        this.createTime = LocalDateTime.now();
    }

    // 帶參數建構子
    public RestRegisterRequest(String restName) {
        this.restName = restName;
        this.createTime = LocalDateTime.now();
    }

    // 密碼驗證方法
    public boolean isPasswordMatching() {
        return merchantPassword != null && merchantPassword.equals(confirmPassword);
    }

    // 取得完整地址方法
    public String getFullAddress() {
        return restCity + restDist + restAddress;
    }

    // 資料初始化方法
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }

    // Getters and Setters
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}