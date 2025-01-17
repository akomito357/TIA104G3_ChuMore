package com.chumore.auth.service;

import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.auth.dto.AuthenticatedUser;
import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestaurantRegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;
import com.chumore.auth.exception.DuplicateRegistrationNumberException;
import com.chumore.cuisinetype.model.CuisineTypeRepository;
import com.chumore.cuisinetype.model.CuisineTypeVO;
import com.chumore.member.model.MemberRepository;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final MemberRepository memberRepository;
	private final RestRepository restRepository;
	private final CuisineTypeRepository cuisineTypeRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AuthServiceImpl(MemberRepository memberRepository, RestRepository restRepository,
			CuisineTypeRepository cuisineTypeRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.restRepository = restRepository;
		this.cuisineTypeRepository = cuisineTypeRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Integer registerMember(RegisterRequest request) {
		logger.info("處理會員註冊請求: {}", request.getEmail());
		validateRegistrationData(request.getEmail(), request.getPhone());
		MemberVO member = createMemberFromRequest(request);
		MemberVO savedMember = memberRepository.save(member);
		logger.info("會員註冊成功: {}", request.getEmail());
		return savedMember.getMemberId();
	}

	@Override
	public Integer registerRestaurant(RestaurantRegisterRequest request) {
		logger.info("處理餐廳註冊請求: {}", request.getEmail());
		RestVO restaurant = createRestaurantFromRequest(request);
		RestVO savedRestaurant = restRepository.save(restaurant);
		logger.info("餐廳註冊成功: {}", request.getEmail());
		return savedRestaurant.getRestId();
	}

	@Override
    public AuthenticatedUser authenticateAndGetUser(LoginRequest request) throws AuthenticationException {
        logger.info("開始處理登入請求: {}", request.getEmail());
        validateLoginRequest(request);
        
        // 嘗試會員登入
        AuthenticatedUser memberUser = authenticateMemberUser(request);
        if (memberUser != null) {
            return memberUser;
        }
        
        // 嘗試餐廳會員登入
        AuthenticatedUser restaurantUser = authenticateRestaurantUser(request);
        if (restaurantUser != null) {
            return restaurantUser;
        }
        
        logger.warn("登入驗證失敗: {}", request.getEmail());
        throw new AuthenticationException("帳號或密碼錯誤");
    }

    private void validateLoginRequest(LoginRequest request) throws AuthenticationException {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            logger.error("登入請求資料不完整");
            throw new AuthenticationException("請輸入完整的登入資訊");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (!isValidEmailFormat(email)) {
            logger.error("無效的電子郵件格式: {}", email);
            throw new AuthenticationException("請輸入有效的電子郵件");
        }

        String password = request.getPassword();
        if (password.length() < 8 || password.length() > 20) {
            logger.error("密碼長度不符合要求");
            throw new AuthenticationException("密碼格式不正確");
        }
    }

    private AuthenticatedUser authenticateMemberUser(LoginRequest request) {
        return memberRepository.findByMemberEmail(request.getEmail())
            .filter(member -> {
                boolean matches = passwordEncoder.matches(request.getPassword(), 
                                                        member.getMemberPassword());
                if (!matches) {
                    logger.warn("會員密碼驗證失敗: {}", request.getEmail());
                }
                return matches;
            })
            .map(member -> {
                logger.info("會員登入成功: {}", request.getEmail());
                return AuthenticatedUser.builder()
                    .userId(member.getMemberId())
                    .email(member.getMemberEmail())
                    .userType("MEMBER")
                    .name(member.getMemberName())
                    .build();
            })
            .orElse(null);
    }

    private AuthenticatedUser authenticateRestaurantUser(LoginRequest request) {
        logger.info("開始驗證餐廳會員身份: {}", request.getEmail());

        Optional<RestVO> restaurantOptional = restRepository.findByMerchantEmail(request.getEmail());
        
        if (!restaurantOptional.isPresent()) {
            logger.debug("未找到對應的餐廳會員帳號: {}", request.getEmail());
            return null;
        }

        RestVO restaurant = restaurantOptional.get();
        logger.debug("找到餐廳會員帳號: restId={}", restaurant.getRestId());

        // 驗證密碼
        if (!passwordEncoder.matches(request.getPassword(), restaurant.getMerchantPassword())) {
            logger.warn("餐廳會員密碼驗證失敗: email={}, restId={}", 
                request.getEmail(), restaurant.getRestId());
            return null;
        }

        // 驗證審核狀態
        if (restaurant.getApprovalStatus() != 1) {
            logger.warn("餐廳帳號尚未通過審核: email={}, restId={}, approvalStatus={}", 
                request.getEmail(), restaurant.getRestId(), restaurant.getApprovalStatus());
            throw new AuthenticationException("您的帳號尚未通過審核，請耐心等候");
        }

        // 建立認證用戶
        AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
                .userId(restaurant.getRestId())
                .email(restaurant.getMerchantEmail())
                .userType("RESTAURANT")
                .name(restaurant.getRestName())
                .approvalStatus(restaurant.getApprovalStatus())
                .businessStatus(restaurant.getBusinessStatus())
                .build();

        logger.info("餐廳會員登入成功: email={}, restId={}", 
            request.getEmail(), restaurant.getRestId());
        
        return authenticatedUser;
    }

    private boolean isValidEmailFormat(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException e) {
            logger.debug("電子郵件格式驗證失敗: {}", email);
            return false;
        }
    }

	@Override
	public void validateRegistrationData(String email, String phone) {
		validateEmail(email);
		validatePhone(phone);
	}


	private void validateEmail(String email) {
		if (memberRepository.existsByMemberEmail(email) || restRepository.existsByMerchantEmail(email)) {
			logger.warn("電子信箱已被註冊: {}", email);
			throw new DuplicateEmailException("此電子信箱已被註冊");
		}
	}

	private void validatePhone(String phone) {
		if (memberRepository.existsByMemberPhoneNumber(phone) || restRepository.existsByPhoneNumber(phone)) {
			logger.warn("電話號碼已被註冊: {}", phone);
			throw new DuplicatePhoneNumberException("此電話號碼已被註冊");
		}
	}

	private void validateAndSetMerchantName(RestaurantRegisterRequest request, RestVO restVO) {
		String name = request.getName();
		if (name == null || name.trim().isEmpty()) {
			throw new ValidationException("負責人姓名不能為空");
		}
		if (!name.matches("^[\u4e00-\u9fa5A-Za-z0-9_]+$")) {
			throw new ValidationException("負責人姓名只能包含中文、英文字母、數字和底線");
		}
		restVO.setMerchantName(name.trim());
	}

	private MemberVO createMemberFromRequest(RegisterRequest request) {
		MemberVO member = new MemberVO();
		member.setMemberName(request.getName());
		member.setMemberEmail(request.getEmail());
		member.setMemberPassword(passwordEncoder.encode(request.getPassword()));
		member.setMemberPhoneNumber(request.getPhone());
		member.setMemberGender(request.getGender());
		member.setMemberBirthdate(request.getBirthdate());
		member.setMemberAddress(request.getAddress());
		return member;
	}

	private RestVO createRestaurantFromRequest(RestaurantRegisterRequest request) {
		RestVO restaurant = new RestVO();

		validateAndSetMerchantName(request, restaurant);
		restaurant.setMerchantEmail(request.getEmail());
		restaurant.setMerchantPassword(passwordEncoder.encode(request.getPassword()));
		restaurant.setPhoneNumber(request.getPhoneNumber());

		// 基本商家資訊
		restaurant.setMerchantName(request.getName());
		restaurant.setMerchantEmail(request.getEmail());
		restaurant.setMerchantPassword(passwordEncoder.encode(request.getPassword()));
		restaurant.setPhoneNumber(request.getPhoneNumber());

		// 手動設置 merchantIdNumber 和 phoneNumber 並驗證
		validateAndSetMerchantIdNumber(request, restaurant);
		validateAndSetPhoneNumber(request, restaurant);

		// 設置預設值
		restaurant.setBusinessStatus(1);
		restaurant.setApprovalStatus(0);
		restaurant.setOrderTableCount(10);
		restaurant.setRestStars(0.0);
		restaurant.setRestReviewers(0);

		// 設置營業時間和週期
		restaurant.setWeeklyBizDays("1111111"); // 代表每週7天營業
		restaurant.setBusinessHours("000000011111111111110000"); // 代表 00:00 - 24:00 營業

		// 餐廳特定資訊
		restaurant.setRestName(request.getRestaurantName());
		restaurant.setRestPhone(request.getBusinessPhone());
		restaurant.setRestRegist(request.getRegistrationNumber());

		// 料理類型處理
		try {
			int cuisineTypeId = Integer.parseInt(request.getCuisineType());
			if (cuisineTypeId < 1 || cuisineTypeId > 10) {
				throw new IllegalArgumentException("無效的餐廳類型代碼");
			}
			CuisineTypeVO cuisineType = cuisineTypeRepository.findById(cuisineTypeId)
					.orElseThrow(() -> new IllegalArgumentException("無效的餐廳類型代碼: " + cuisineTypeId));
			restaurant.setCuisineType(cuisineType);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("餐廳類型格式錯誤: " + request.getCuisineType());
		}

		// 地址資訊
		restaurant.setRestCity(request.getCity());
		restaurant.setRestDist(request.getDistrict());
		restaurant.setRestAddress(request.getAddressDetail());

		// 狀態設定
		restaurant.setBusinessStatus(1);
		restaurant.setApprovalStatus(0);

		return restaurant;
	}

	private void validateAndSetMerchantIdNumber(RestaurantRegisterRequest request, RestVO restVO) {
		String merchantIdNumber = request.getMerchantIdNumber();
		if (merchantIdNumber == null || merchantIdNumber.trim().isEmpty()) {
			throw new ValidationException("請輸入負責人身分證字號");
		}
		if (!merchantIdNumber.matches("^[A-Z][1-2][0-9]{8}$")) {
			throw new ValidationException("負責人身分證字號格式錯誤");
		}
		restVO.setMerchantIdNumber(merchantIdNumber.trim());
	}

	private void validateAndSetPhoneNumber(RestaurantRegisterRequest request, RestVO restVO) {
		String phoneNumber = request.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			throw new ValidationException("請輸入負責人手機號碼");
		}
		if (!phoneNumber.matches("^09[0-9]{8}$")) {
			throw new ValidationException("負責人手機號碼格式錯誤");
		}
		restVO.setPhoneNumber(phoneNumber.trim());
	}

}