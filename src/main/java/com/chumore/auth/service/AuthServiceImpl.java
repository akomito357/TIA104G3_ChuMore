package com.chumore.auth.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.auth.constant.AuthConstants;
import com.chumore.auth.dto.*;
import com.chumore.auth.exception.*;
import com.chumore.member.model.MemberRepository;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private RestRepository restRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("開始處理登入請求，使用者信箱: {}", request.getEmail());

        try {
            Optional<MemberVO> memberOpt = memberRepository.findByMemberEmail(request.getEmail());
            if (memberOpt.isPresent()) {
                MemberVO member = memberOpt.get();
                if (passwordEncoder.matches(request.getPassword(), member.getMemberPassword())) {
                    logger.info("一般會員登入成功: {}", request.getEmail());
                    return createMemberLoginResponse(member);
                }
            }
            
            Optional<RestVO> restOpt = restRepository.findByMerchantEmail(request.getEmail());
            if (restOpt.isPresent()) {
                RestVO rest = restOpt.get();
                if (passwordEncoder.matches(request.getPassword(), rest.getMerchantPassword())) {
                    logger.info("餐廳會員登入成功: {}", request.getEmail());
                    return createRestaurantLoginResponse(rest);
                }
            }
            
            logger.warn("登入失敗 - 帳號或密碼錯誤: {}", request.getEmail());
            throw new AuthenticationException("帳號或密碼錯誤");

        } catch (AuthenticationException e) {
            logger.error("認證失敗: {}", request.getEmail());
            throw e;
        } catch (Exception e) {
            logger.error("登入過程發生未預期錯誤: {}", request.getEmail(), e);
            throw new AuthenticationException("系統發生錯誤，請稍後再試");
        }
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        logger.info("開始處理會員註冊請求: {}", request.getMemberEmail());
        validateRegistrationData(request);

        try {
            MemberVO member = createMemberFromRequest(request);
            MemberVO savedMember = memberRepository.save(member);
            logger.info("會員註冊成功: {}", request.getMemberEmail());
            return createMemberLoginResponse(savedMember);
        } catch (Exception e) {
            logger.error("會員註冊過程發生錯誤: {}", request.getMemberEmail(), e);
            throw new AuthenticationException("註冊過程發生錯誤，請稍後再試");
        }
    }

    @Override
    public LoginResponse registerRestaurantBasic(RestRegisterRequest request) {
        logger.info("開始處理餐廳基本資料註冊: {}", request.getMerchantEmail());
        
        if (isEmailExist(request.getMerchantEmail())) {
            logger.warn("餐廳註冊失敗 - 電子信箱已存在: {}", request.getMerchantEmail());
            throw new DuplicateEmailException("此電子信箱已被註冊");
        }

        try {
            RestVO restaurant = createRestaurantFromRequest(request);
            RestVO savedRestaurant = restRepository.save(restaurant);
            logger.info("餐廳基本資料註冊成功: {}", request.getMerchantEmail());
            return createRestaurantLoginResponse(savedRestaurant);
        } catch (Exception e) {
            logger.error("餐廳基本資料註冊過程發生錯誤: {}", request.getMerchantEmail(), e);
            throw new AuthenticationException("註冊過程發生錯誤，請稍後再試");
        }
    }

    @Override
    public LoginResponse registerRestaurantComplete(RestRegisterRequest stepOneData, 
            RestaurantDetailsRequest detailsRequest) {
        logger.info("開始處理餐廳完整註冊流程: {}", stepOneData.getMerchantEmail());

        try {
            RestVO restaurant = createRestaurantFromRequest(stepOneData);
            updateRestaurantWithDetails(restaurant, detailsRequest);
            
            RestVO savedRestaurant = restRepository.save(restaurant);
            logger.info("餐廳完整註冊成功: {}", stepOneData.getMerchantEmail());
            
            return createRestaurantLoginResponse(savedRestaurant);
        } catch (Exception e) {
            logger.error("餐廳完整註冊過程發生錯誤: {}", stepOneData.getMerchantEmail(), e);
            throw new AuthenticationException("註冊過程發生錯誤，請稍後再試");
        }
    }
    
    private void updateRestaurantWithDetails(RestVO restaurant, RestaurantDetailsRequest details) {
        restaurant.setBusinessHours(details.getBusinessHours());
        restaurant.setBusinessStatus(details.getBusinessStatus());
       
    }

    private MemberVO createMemberFromRequest(RegisterRequest request) {
        MemberVO member = new MemberVO();
        member.setMemberName(request.getMemberName());
        member.setMemberEmail(request.getMemberEmail());
        member.setMemberPassword(passwordEncoder.encode(request.getMemberPassword()));
        member.setMemberPhoneNumber(request.getMemberPhoneNumber());
        member.setMemberGender(request.getMemberGender());
        member.setMemberBirthdate(request.getMemberBirthdate());
        member.setMemberAddress(request.getMemberAddress());
        return member;
    }

    private RestVO createRestaurantFromRequest(RestRegisterRequest request) {
        RestVO restaurant = new RestVO();
        restaurant.setMerchantName(request.getMerchantName());
        restaurant.setMerchantEmail(request.getMerchantEmail());
        restaurant.setMerchantPassword(passwordEncoder.encode(request.getMerchantPassword()));
        restaurant.setMerchantIdNumber(request.getMerchantIdNumber());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setRestName(request.getRestName());
        restaurant.setRestCity(request.getRestCity());
        restaurant.setRestDist(request.getRestDist());
        restaurant.setRestAddress(request.getRestAddress());
        restaurant.setRestRegist(request.getRestRegist());
        restaurant.setRestPhone(request.getRestPhone());
        restaurant.setBusinessStatus(0);
        restaurant.setApprovalStatus(0);
        return restaurant;
    }

    private LoginResponse createMemberLoginResponse(MemberVO member) {
        LoginResponse response = new LoginResponse();
        response.setUserType(AuthConstants.USER_TYPE_MEMBER);
        response.setMemberVO(member);
        return response;
    }

    private LoginResponse createRestaurantLoginResponse(RestVO restaurant) {
        LoginResponse response = new LoginResponse();
        response.setUserType(AuthConstants.USER_TYPE_RESTAURANT);
        response.setRestVO(restaurant);
        return response;
    }
    
    @Override
    public boolean isEmailExist(String email) {
        logger.info("檢查電子郵件是否已存在: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.debug("收到空的電子郵件地址，返回 false");
            return false;
        }

        try {
            boolean memberExists = memberRepository.existsByMemberEmail(email.trim());
            boolean restaurantExists = restRepository.existsByMerchantEmail(email.trim());

            if (memberExists || restaurantExists) {
                logger.debug("電子郵件已存在於系統中: {}", email);
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error("檢查電子郵件時發生錯誤: {}", email, e);
            throw new RuntimeException("檢查電子郵件時發生系統錯誤");
        }
    }
    
    @Override
    public void validateRestaurantData(RegisterRequest merchantRequest, RestRegisterRequest restRequest) 
            throws DuplicateEmailException, DuplicatePhoneNumberException {
        
        logger.info("開始驗證餐廳註冊資料，負責人信箱: {}", merchantRequest.getMemberEmail());

        try {
            // 驗證電子信箱一致性
            if (!merchantRequest.getMemberEmail().equals(restRequest.getMerchantEmail())) {
                logger.warn("餐廳註冊驗證失敗 - 負責人電子信箱與餐廳資料不符: {} vs {}", 
                        merchantRequest.getMemberEmail(), restRequest.getMerchantEmail());
                throw new AuthenticationException("負責人電子信箱與餐廳資料不符");
            }

            // 驗證手機號碼一致性
            if (!merchantRequest.getMemberPhoneNumber().equals(restRequest.getPhoneNumber())) {
                logger.warn("餐廳註冊驗證失敗 - 負責人手機號碼與餐廳資料不符: {} vs {}", 
                        merchantRequest.getMemberPhoneNumber(), restRequest.getPhoneNumber());
                throw new AuthenticationException("負責人手機號碼與餐廳資料不符");
            }

            // 檢查電子信箱是否已被註冊
            if (isEmailExist(restRequest.getMerchantEmail())) {
                logger.warn("餐廳註冊驗證失敗 - 電子信箱已被使用: {}", restRequest.getMerchantEmail());
                throw new DuplicateEmailException("此電子信箱已被註冊");
            }

            // 檢查手機號碼是否已被註冊
            if (memberRepository.existsByMemberPhoneNumber(restRequest.getPhoneNumber()) ||
                restRepository.existsByPhoneNumber(restRequest.getPhoneNumber())) {
                logger.warn("餐廳註冊驗證失敗 - 手機號碼已被使用: {}", restRequest.getPhoneNumber());
                throw new DuplicatePhoneNumberException("此手機號碼已被註冊");
            }

            logger.info("餐廳註冊資料驗證成功");

        } catch (AuthenticationException | DuplicateEmailException | DuplicatePhoneNumberException e) {
            throw e;
        } catch (Exception e) {
            logger.error("餐廳資料驗證過程發生未預期錯誤", e);
            throw new AuthenticationException("資料驗證過程發生錯誤，請稍後再試");
        }
    }
    
    @Override
    public void validateRegistrationData(RegisterRequest request) 
            throws DuplicateEmailException, DuplicatePhoneNumberException {
        
        logger.info("開始驗證會員註冊資料，電子信箱: {}", request.getMemberEmail());

        try {
            // 電子信箱驗證
            if (isEmailExist(request.getMemberEmail())) {
                logger.warn("會員註冊驗證失敗 - 電子信箱已被使用: {}", request.getMemberEmail());
                throw new DuplicateEmailException("此電子信箱已被註冊");
            }

            // 手機號碼驗證
            if (memberRepository.existsByMemberPhoneNumber(request.getMemberPhoneNumber())) {
                logger.warn("會員註冊驗證失敗 - 手機號碼已被會員使用: {}", 
                        request.getMemberPhoneNumber());
                throw new DuplicatePhoneNumberException("此手機號碼已被會員註冊");
            }

            if (restRepository.existsByPhoneNumber(request.getMemberPhoneNumber())) {
                logger.warn("會員註冊驗證失敗 - 手機號碼已被餐廳使用: {}", 
                        request.getMemberPhoneNumber());
                throw new DuplicatePhoneNumberException("此手機號碼已被餐廳會員註冊");
            }

            logger.info("會員註冊資料驗證成功: {}", request.getMemberEmail());

        } catch (DuplicateEmailException | DuplicatePhoneNumberException e) {
            throw e;
        } catch (Exception e) {
            logger.error("會員資料驗證過程發生未預期錯誤", e);
            throw new AuthenticationException("資料驗證過程發生錯誤，請稍後再試");
        }
    }
}