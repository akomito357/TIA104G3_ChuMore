package com.chumore.auth.service;

import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.LoginResponse;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestRegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;

/**
 * 提供會員認證相關服務的介面
 */
public interface AuthService {
    
    /**
     * 處理會員註冊
     * 
     * @param request 包含註冊資訊的請求對象
     * @return 含有註冊會員資訊的回應對象
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    LoginResponse register(RegisterRequest request);
    
    LoginResponse registerRestaurant(RestRegisterRequest request);

    
    /**
     * 處理會員登入
     * 
     * @param request 包含登入資訊的請求對象
     * @return 含有登入會員資訊的回應對象
     * @throws AuthenticationException 當驗證失敗時拋出
     */
    LoginResponse login(LoginRequest request);
}