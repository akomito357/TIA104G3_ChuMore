package com.chumore.auth.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.*;

public class RestRegisterRequest {
    @NotBlank(message = "請填寫餐廳名稱")
    private String restName;

    @NotBlank(message = "請輸入餐廳所在縣市")
    private String restCity;

    @NotBlank(message = "請輸入餐廳所在鄉鎮市區")
    private String restDist;

    @NotBlank(message = "請輸入餐廳地址")
    private String restAddress;

    @NotBlank(message = "請輸入食品業者登錄字號")
    @Pattern(regexp = "^[A-Za-z]-[0-9]{9}-[0-9]{5}-[0-9]{1}$", 
            message = "登錄字號格式錯誤！請輸入如：B-123456789-12345-1之格式")
    private String restRegist;

    @NotBlank(message = "請輸入餐廳聯絡電話")
    private String restPhone;

    @NotBlank(message = "請輸入負責人姓名")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9_]+$", 
            message = "負責人姓名只能是中、英文字母、數字和_")
    private String merchantName;

    @NotBlank(message = "請輸入負責人身分證字號")
    @Pattern(regexp = "^[A-Za-z][12]\\d{8}$", 
            message = "負責人身分證字號格式錯誤")
    private String merchantIdNumber;

    @NotBlank(message = "請輸入負責人電子信箱")
    @Email(message = "請輸入有效的電子信箱格式")
    private String merchantEmail;

    @NotBlank(message = "請輸入密碼")
    private String merchantPassword;

    @NotBlank(message = "請輸入負責人手機號碼")
    @Pattern(regexp = "^09\\d{8}$", 
            message = "負責人手機號碼格式錯誤")
    private String phoneNumber;

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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

   
}