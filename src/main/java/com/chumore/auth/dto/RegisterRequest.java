package com.chumore.auth.dto;

import javax.validation.constraints.*;

import java.sql.Date;
import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank(message = "請輸入姓名")
    @Size(max = 50, message = "姓名長度不能超過50個字元")
    private String name;

    @NotBlank(message = "請輸入電子信箱")
    @Email(message = "請輸入有效的電子信箱格式")
    @Size(max = 100, message = "電子信箱長度不能超過100個字元")
    private String email;

    @NotBlank(message = "請輸入密碼")
    @Size(min = 8, max = 50, message = "密碼長度必須在8到50個字元之間")
    private String password;

    @NotBlank(message = "請確認密碼")
    private String confirmPassword;

    @NotBlank(message = "手機號碼不能為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phone;
    

    @NotNull(message = "請選擇性別")
    private Integer gender;

    @NotNull(message = "請選擇生日")
    @Past(message = "生日必須是過去的日期")
    private Date birthdate;

    private String address;

    

    public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
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



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public Integer getGender() {
		return gender;
	}



	public void setGender(Integer gender) {
		this.gender = gender;
	}



	public Date getBirthdate() {
		return birthdate;
	}



	public void setBirthdate(@NotNull(message = "請選擇生日") @Past(message = "生日必須是過去的日期") Date birthdate) {
		this.birthdate = birthdate;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	@Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", address='" + address + '\'' +
                '}';
    }
}