package com.chumore.auth.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "請輸入電子信箱")
    @Email(message = "請輸入有效的電子信箱格式")
    @Size(max = 100, message = "電子信箱長度不能超過100個字元")
    private String email;

    @NotBlank(message = "請輸入密碼")
    @Size(min = 8, max = 50, message = "密碼長度必須在8到50個字元之間")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{email='" + email + "', password='[PROTECTED]'}";
    }
}