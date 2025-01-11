package com.chumore.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.LoginResponse;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.service.AuthServiceImpl;
import com.chumore.auth.exception.AuthenticationException;

public class AuthControllerTest {
    
    private AuthServiceImpl authService;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        authService = new AuthServiceImpl();
        
        // 設置測試用的登入請求
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Test123456");
        
        // 設置測試用的註冊請求
        registerRequest = new RegisterRequest();
        registerRequest.setMemberName("測試用戶");
        registerRequest.setMemberEmail("test@example.com");
        registerRequest.setMemberPassword("Test123456");
        registerRequest.setMemberPhoneNumber("0912345678");
    }

    @Test
    public void testLoginSuccess() {
        try {
            LoginResponse response = authService.login(loginRequest);
            assertNotNull(response, "登入響應不應為空");
            assertNotNull(response.getMemberVO(), "會員資訊不應為空");
            assertEquals("test@example.com", response.getMemberVO().getMemberEmail(), "電子郵件應相符");
        } catch (Exception e) {
            fail("登入過程不應拋出異常: " + e.getMessage());
        }
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        loginRequest.setPassword("wrongpassword");
        
        Exception exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(loginRequest);
        });
        
        assertEquals("帳號或密碼錯誤", exception.getMessage());
    }

    @Test
    public void testRegisterSuccess() {
        try {
            LoginResponse response = authService.register(registerRequest);
            assertNotNull(response, "註冊響應不應為空");
            assertNotNull(response.getMemberVO(), "會員資訊不應為空");
            assertEquals("test@example.com", response.getMemberVO().getMemberEmail(), "電子郵件應相符");
        } catch (Exception e) {
            fail("註冊過程不應拋出異常: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterWithExistingEmail() {
        // 先註冊一個用戶
        try {
            authService.register(registerRequest);
        } catch (Exception e) {
            fail("首次註冊不應失敗");
        }
        
        // 嘗試使用相同的電子郵件再次註冊
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setMemberEmail("test@example.com");
        
        Exception exception = assertThrows(AuthenticationException.class, () -> {
            authService.register(duplicateRequest);
        });
        
        assertEquals("此電子信箱已被註冊", exception.getMessage());
    }
}