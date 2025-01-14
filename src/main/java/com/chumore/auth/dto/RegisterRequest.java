package com.chumore.auth.dto;

import javax.validation.constraints.*;
import java.sql.Date;

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

    // 移除 @NotBlank 註解，但保留格式驗證
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼格式")
    private String phone;

    // 移除 @NotNull、@Min、@Max 註解
    private Integer gender;

    // 移除 @NotNull、@Past 註解
    private Date birthdate;

    private String address;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, String phone,
            Integer gender, Date birthdate, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.address = address;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getPhone() { return phone; }
    public Integer getGender() { return gender; }
    public Date getBirthdate() { return birthdate; }
    public String getAddress() { return address; }

    // Setters
    public void setName(String name) { this.name = name != null ? name.trim() : null; }
    public void setEmail(String email) { this.email = email != null ? email.trim() : null; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void setPhone(String phone) { this.phone = phone != null ? phone.trim() : null; }
    public void setGender(Integer gender) { this.gender = gender; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }
    public void setAddress(String address) { this.address = address != null ? address.trim() : null; }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", birthdate=" + birthdate +
                ", address='" + address + '\'' +
                '}';
    }
}