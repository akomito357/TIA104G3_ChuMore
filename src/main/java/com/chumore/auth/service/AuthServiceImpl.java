package com.chumore.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.auth.controller.AuthController;
import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.LoginResponse;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestRegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;
import com.chumore.member.model.MemberRepository;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;

import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private RestRepository restRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse register(RegisterRequest request) {
        validateRegistration(request);

        MemberVO member = new MemberVO();
        member.setMemberName(request.getMemberName());
        member.setMemberEmail(request.getMemberEmail());
        member.setMemberPassword(passwordEncoder.encode(request.getMemberPassword()));
        member.setMemberPhoneNumber(request.getMemberPhoneNumber());
        member.setMemberGender(request.getMemberGender());
        member.setMemberBirthdate(request.getMemberBirthdate());
        member.setMemberAddress(request.getMemberAddress());

        MemberVO savedMember = memberRepository.save(member);

        LoginResponse response = new LoginResponse();
        response.setUserType(AuthController.USER_TYPE_MEMBER);
        response.setMemberVO(savedMember);
        
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 嘗試會員登入
        Optional<MemberVO> memberOpt = memberRepository.findByMemberEmail(request.getEmail());
        if (memberOpt.isPresent()) {
            MemberVO member = memberOpt.get();
            if (passwordEncoder.matches(request.getPassword(), member.getMemberPassword())) {
                LoginResponse response = new LoginResponse();
                response.setUserType(AuthController.USER_TYPE_MEMBER);
                response.setMemberVO(member);
                return response;
            }
        }
        
        // 嘗試餐廳會員登入，使用自定義方法進行查詢
        Optional<RestVO> restOpt = findRestaurantByEmail(request.getEmail());
        if (restOpt.isPresent()) {
            RestVO rest = restOpt.get();
            if (passwordEncoder.matches(request.getPassword(), rest.getMerchantPassword())) {
                LoginResponse response = new LoginResponse();
                response.setUserType(AuthController.USER_TYPE_RESTAURANT);
                response.setRestVO(rest);
                return response;
            }
        }
        
        throw new AuthenticationException("帳號或密碼錯誤");
    }
    
    @Override
    public LoginResponse registerRestaurant(RestRegisterRequest request) {
        validateRestaurantRegistration(request);

        RestVO restaurant = new RestVO();
        restaurant.setRestName(request.getRestName());
        restaurant.setRestCity(request.getRestCity());
        restaurant.setRestDist(request.getRestDist());
        restaurant.setRestAddress(request.getRestAddress());
        restaurant.setRestRegist(request.getRestRegist());
        restaurant.setRestPhone(request.getRestPhone());
        restaurant.setMerchantName(request.getMerchantName());
        restaurant.setMerchantIdNumber(request.getMerchantIdNumber());
        restaurant.setMerchantEmail(request.getMerchantEmail());
        restaurant.setMerchantPassword(passwordEncoder.encode(request.getMerchantPassword()));
        restaurant.setPhoneNumber(request.getPhoneNumber());
        
        // 設置初始狀態
        restaurant.setBusinessStatus(0);  // 預設未營業
        restaurant.setApprovalStatus(0);  // 預設待審核
        
        RestVO savedRestaurant = restRepository.save(restaurant);

        LoginResponse response = new LoginResponse();
        response.setUserType(AuthController.USER_TYPE_RESTAURANT);
        response.setRestVO(savedRestaurant);
        
        return response;
    }

    private void validateRestaurantRegistration(RestRegisterRequest request) {
        if (restRepository.existsByMerchantEmail(request.getMerchantEmail())) {
            throw new DuplicateEmailException("此電子信箱已被註冊");
        }
        if (memberRepository.existsByMemberEmail(request.getMerchantEmail())) {
            throw new DuplicateEmailException("此電子信箱已被一般會員使用");
        }
        if (restRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException("此手機號碼已被註冊");
        }
    }

    private void validateRegistration(RegisterRequest request) {
        if (memberRepository.existsByMemberEmail(request.getMemberEmail())) {
            throw new DuplicateEmailException("此電子信箱已被會員註冊");
        }
        
        if (isRestaurantEmailExists(request.getMemberEmail())) {
            throw new DuplicateEmailException("此電子信箱已被餐廳會員註冊");
        }

        if (memberRepository.existsByMemberPhoneNumber(request.getMemberPhoneNumber())) {
            throw new DuplicatePhoneNumberException("此手機號碼已被會員註冊");
        }
        
        if (isRestaurantPhoneExists(request.getMemberPhoneNumber())) {
            throw new DuplicatePhoneNumberException("此手機號碼已被餐廳會員註冊");
        }
    }

    // 自定義方法：根據電子信箱查找餐廳
    private Optional<RestVO> findRestaurantByEmail(String email) {
        return restRepository.findAll().stream()
                .filter(rest -> rest.getMerchantEmail().equals(email))
                .findFirst();
    }

    // 自定義方法：檢查餐廳電子信箱是否存在
    private boolean isRestaurantEmailExists(String email) {
        return restRepository.findAll().stream()
                .anyMatch(rest -> rest.getMerchantEmail().equals(email));
    }

    // 自定義方法：檢查餐廳手機號碼是否存在
    private boolean isRestaurantPhoneExists(String phoneNumber) {
        return restRepository.findAll().stream()
                .anyMatch(rest -> rest.getPhoneNumber().equals(phoneNumber));
    }
}