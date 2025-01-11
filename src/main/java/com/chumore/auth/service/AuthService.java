package com.chumore.auth.service;

import com.chumore.auth.dto.*;
import com.chumore.auth.exception.*;

/**
 * 提供會員認證相關服務的介面
 * 包含會員和餐廳的註冊、登入及相關驗證功能
 */
public interface AuthService {

    /**
     * 處理一般會員註冊
     *
     * @param request 包含會員註冊資訊的請求對象
     * @return 含有註冊會員資訊的回應對象
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 處理會員登入（包含一般會員和餐廳會員）
     *
     * @param request 包含登入資訊的請求對象
     * @return 含有登入會員資訊的回應對象
     * @throws AuthenticationException 當驗證失敗時拋出
     */
    LoginResponse login(LoginRequest request);

    /**
     * 處理餐廳註冊第一步驟（基本資料）
     *
     * @param request 包含餐廳基本資訊的請求對象
     * @return 含有餐廳基本註冊資訊的回應對象
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    LoginResponse registerRestaurantBasic(RestRegisterRequest request);

    /**
     * 處理餐廳註冊完整流程（包含基本資料和詳細資訊）
     *
     * @param stepOneData 包含餐廳基本資訊的請求對象
     * @param detailsRequest 包含餐廳詳細資訊的請求對象
     * @return 含有完整餐廳註冊資訊的回應對象
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    LoginResponse registerRestaurantComplete(RestRegisterRequest stepOneData, 
            RestaurantDetailsRequest detailsRequest);

    /**
     * 驗證一般會員註冊資料
     *
     * @param request 要驗證的註冊資料
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    void validateRegistrationData(RegisterRequest request);

    /**
     * 驗證餐廳註冊資料
     *
     * @param merchantRequest 負責人基本資料
     * @param restRequest 餐廳註冊資料
     * @throws DuplicateEmailException 當電子信箱已被註冊時拋出
     * @throws DuplicatePhoneNumberException 當手機號碼已被註冊時拋出
     */
    void validateRestaurantData(RegisterRequest merchantRequest, RestRegisterRequest restRequest);

    /**
     * 檢查電子郵件是否已被註冊
     *
     * @param email 要檢查的電子郵件地址
     * @return 如果電子郵件已存在返回 true，否則返回 false
     */
    boolean isEmailExist(String email);
}