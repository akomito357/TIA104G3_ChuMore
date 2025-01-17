package com.chumore.auth.service;

import com.chumore.auth.dto.AuthenticatedUser;
import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestaurantRegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;

/**
 * 認證服務介面
 * 提供會員和餐廳的註冊登入功能
 */
public interface AuthService {
    
    /**
     * 處理會員註冊
     * 
     * @param request 會員註冊請求
     * @return 註冊成功的會員ID
     * @throws DuplicateEmailException 當電子信箱已被註冊時
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時
     */
	Integer registerMember(RegisterRequest request) 
        throws DuplicateEmailException, DuplicatePhoneNumberException;
    
    /**
     * 處理餐廳註冊
     * 
     * @param request 餐廳註冊請求，包含基本資料和餐廳資訊
     * @return 註冊成功的餐廳ID
     * @throws DuplicateEmailException 當電子信箱已被註冊時
     * @throws DuplicatePhoneNumberException 當手機號碼或營業電話已被註冊時
     */
	Integer registerRestaurant(RestaurantRegisterRequest request) 
        throws DuplicateEmailException, DuplicatePhoneNumberException;
    
	/**
     * 驗證用戶身份並返回完整的用戶資訊
     *
     * @param request 登入請求
     * @return AuthenticatedUser 包含用戶完整資訊的物件
     * @throws AuthenticationException 當驗證失敗時
     */
    AuthenticatedUser authenticateAndGetUser(LoginRequest request) throws AuthenticationException;

    
    /**
     * 驗證註冊資料
     * 
     * @param email 電子信箱
     * @param phone 電話號碼
     * @throws DuplicateEmailException 當電子信箱已存在時
     * @throws DuplicatePhoneNumberException 當電話號碼已存在時
     */
    void validateRegistrationData(String email, String phone) 
        throws DuplicateEmailException, DuplicatePhoneNumberException;
}