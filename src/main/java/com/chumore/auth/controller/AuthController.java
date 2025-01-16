package com.chumore.auth.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestRegisterRequest;
import com.chumore.auth.dto.RestaurantDetailsRequest;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;
import com.chumore.auth.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	private static final String VIEW_PREFIX = "auth/";
	private static final String REGISTER_VIEW_PREFIX = VIEW_PREFIX + "register/";
	private static final String USER_TYPE_MEMBER = "MEMBER";
	private static final String USER_TYPE_RESTAURANT = "RESTAURANT";
	private static final String SESSION_USER_TYPE = "userType";

	@Autowired
	private AuthService authService;

	@GetMapping("/login")
	public String showLoginForm(Model model, @RequestParam(required = false) String error,
			@RequestParam(required = false) String logout) {

		if (error != null) {
			model.addAttribute("errorMessage", "登入失敗，請確認您的帳號密碼是否正確");
		}

		if (logout != null) {
			model.addAttribute("successMessage", "您已成功登出");
		}

		if (!model.containsAttribute("loginRequest")) {
			model.addAttribute("loginRequest", new LoginRequest());
		}

		return VIEW_PREFIX + "login";
	}

	@PostMapping("/login")
	public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest, BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			return VIEW_PREFIX + "login";
		}

		try {
			// 身份驗證邏輯將由 Spring Security 處理
			return "redirect:/dashboard";
		} catch (Exception e) {
			logger.error("Login error for email: {}", loginRequest.getEmail(), e);
			redirectAttributes.addFlashAttribute("errorMessage", "登入過程發生錯誤，請稍後再試");
			return "redirect:/auth/login";
		}
	}

	/**
	 * 顯示註冊選擇頁面
	 */
	@GetMapping("/register")
	public String showRegisterForm() {
		return VIEW_PREFIX + "register";
	}

	/**
	 * 顯示一般會員註冊表單
	 */
	@GetMapping("/register/memregister")
	public String showMemberRegisterForm(Model model) {
		if (!model.containsAttribute("registerRequest")) {
			model.addAttribute("registerRequest", new RegisterRequest());
		}
		return REGISTER_VIEW_PREFIX + "memregister";
	}

	/**
	 * 處理一般會員註冊
	 */
	@PostMapping("/register/memregister")
	public String processMemberRegistration(@Valid @ModelAttribute RegisterRequest request, BindingResult result,
			RedirectAttributes redirectAttributes) {

		logger.info("Processing member registration for email: {}", request.getMemberEmail());

		if (result.hasErrors()) {
			return REGISTER_VIEW_PREFIX + "memregister";
		}

		try {
			// 驗證密碼確認
			if (!request.getMemberPassword().equals(request.getConfirmPassword())) {
				result.rejectValue("confirmPassword", "error.confirmPassword", "密碼與確認密碼不符");
				return REGISTER_VIEW_PREFIX + "memregister";
			}

			request.setUserType(USER_TYPE_MEMBER);
			authService.register(request);

			logger.info("Member registration successful for email: {}", request.getMemberEmail());
			redirectAttributes.addFlashAttribute("successMessage", "會員註冊成功！請登入");
			return "redirect:/auth/login";

		} catch (DuplicateEmailException | DuplicatePhoneNumberException e) {
			logger.warn("Registration failed: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			redirectAttributes.addFlashAttribute("registerRequest", request);
			return "redirect:/auth/register/memregister";
		} catch (Exception e) {
			logger.error("Unexpected error during member registration", e);
			redirectAttributes.addFlashAttribute("errorMessage", "註冊過程發生錯誤，請稍後再試");
			redirectAttributes.addFlashAttribute("registerRequest", request);
			return "redirect:/auth/register/memregister";
		}
	}

	@GetMapping("/register/restregister1")
	public String showRestaurantRegisterStep1(Model model) {
	    // 初始化表單對象
	    if (!model.containsAttribute("restRegisterRequest")) {
	        RestRegisterRequest request = new RestRegisterRequest();
	        model.addAttribute("restRegisterRequest", request);
	    }
	    return REGISTER_VIEW_PREFIX + "restregister1";
	}

	@PostMapping("/register/restregister1")
	public String processRestaurantStep1(
	        @Valid @ModelAttribute("restRegisterRequest") RestRegisterRequest request,
	        BindingResult result,
	        HttpSession session,
	        RedirectAttributes redirectAttributes,
	        Model model) {
	    
	    logger.info("處理餐廳註冊步驟一，電子信箱: {}", request.getMerchantEmail());

	    // 表單驗證錯誤處理
	    if (result.hasErrors()) {
	        logger.warn("餐廳註冊表單驗證失敗");
	        model.addAttribute("restRegisterRequest", request);
	        return REGISTER_VIEW_PREFIX + "restregister1";
	    }

	    try {
	        // 密碼確認驗證
	        if (!request.getMerchantPassword().equals(request.getConfirmPassword())) {
	            result.rejectValue("confirmPassword", "error.confirmPassword", 
	                             "密碼與確認密碼不符");
	            model.addAttribute("restRegisterRequest", request);
	            return REGISTER_VIEW_PREFIX + "restregister1";
	        }

	        request.setUserType(USER_TYPE_RESTAURANT);

	        // 檢查電子信箱是否已存在
	        if (authService.isEmailExist(request.getMerchantEmail())) {
	            logger.warn("註冊失敗 - 電子信箱已存在: {}", request.getMerchantEmail());
	            result.rejectValue("merchantEmail", "error.email", 
	                             "此電子信箱已被註冊");
	            model.addAttribute("restRegisterRequest", request);
	            return REGISTER_VIEW_PREFIX + "restregister1";
	        }

	        // 儲存步驟一資料到 Session
	        session.setAttribute("restaurantStepOneData", request);
	        logger.info("餐廳註冊步驟一完成: {}", request.getMerchantEmail());
	        
	        return "redirect:/auth/register/restregister2";

	    } catch (Exception e) {
	        logger.error("餐廳註冊步驟一處理發生錯誤: {}", request.getMerchantEmail(), e);
	        redirectAttributes.addFlashAttribute("errorMessage", 
	                                          "註冊過程發生錯誤，請稍後再試");
	        // 保留用戶輸入的資料
	        redirectAttributes.addFlashAttribute("restRegisterRequest", request);
	        return "redirect:/auth/register/restregister1";
	    }
	}

	/**
	 * 顯示餐廳註冊步驟二表單
	 */
	@GetMapping("/register/restregister2")
	public String showRestaurantRegisterStep2(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		RestRegisterRequest stepOneData = (RestRegisterRequest) session.getAttribute("restaurantStepOneData");

		if (stepOneData == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "請先完成步驟一");
			return "redirect:/auth/register/restregister1";
		}

		if (!model.containsAttribute("restaurantDetailsRequest")) {
			model.addAttribute("restaurantDetailsRequest", new RestaurantDetailsRequest());
		}

		return REGISTER_VIEW_PREFIX + "restregister2";
	}

	/**
	 * 處理餐廳註冊步驟二
	 */
	@PostMapping("/register/restregister2")
	public String processRestaurantStep2(@Valid @ModelAttribute RestaurantDetailsRequest detailsRequest,
			BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {

		// 驗證步驟一資料是否存在
		RestRegisterRequest stepOneData = (RestRegisterRequest) session.getAttribute("restaurantStepOneData");

		if (stepOneData == null) {
			logger.warn("餐廳註冊步驟二失敗：找不到步驟一的資料");
			redirectAttributes.addFlashAttribute("errorMessage", "請先完成步驟一");
			return "redirect:/auth/register/restregister1";
		}

		// 驗證表單資料
		if (result.hasErrors()) {
			logger.warn("餐廳註冊步驟二驗證失敗，電子信箱: {}", stepOneData.getMerchantEmail());
			return REGISTER_VIEW_PREFIX + "restregister2";
		}

		try {
			logger.info("開始處理餐廳註冊步驟二，電子信箱: {}", stepOneData.getMerchantEmail());

			// 執行完整餐廳註冊流程
			authService.registerRestaurantComplete(stepOneData, detailsRequest);

			// 清除 Session 中的註冊資料
			session.removeAttribute("restaurantStepOneData");

			logger.info("餐廳註冊完成，電子信箱: {}", stepOneData.getMerchantEmail());

			redirectAttributes.addFlashAttribute("successMessage", "餐廳註冊成功！請登入");
			return "redirect:/auth/login";

		} catch (Exception e) {
			logger.error("餐廳註冊步驟二發生錯誤，電子信箱: {}", stepOneData.getMerchantEmail(), e);
			redirectAttributes.addFlashAttribute("errorMessage", "註冊過程發生錯誤，請稍後再試");
			redirectAttributes.addFlashAttribute("restaurantDetailsRequest", detailsRequest);
			return "redirect:/auth/register/restregister2";
		}
	}

	/**
	 * 處理登出請求
	 */
	@PostMapping("/logout")
	public String handleLogout(HttpSession session, RedirectAttributes redirectAttributes) {
		try {
			String userType = (String) session.getAttribute(SESSION_USER_TYPE);
			String username = getUsernameFromSession(session, userType);

			logger.info("User logout: {} (Type: {})", username, userType);

			session.invalidate();
			redirectAttributes.addFlashAttribute("successMessage", "您已成功登出系統");

		} catch (Exception e) {
			logger.error("Logout error", e);
			redirectAttributes.addFlashAttribute("errorMessage", "登出過程發生錯誤，請稍後再試");
		}

		return "redirect:/auth/login";
	}

	/**
	 * 依據使用者類型從Session中獲取使用者名稱
	 */
	private String getUsernameFromSession(HttpSession session, String userType) {
		if (USER_TYPE_MEMBER.equals(userType)) {
			return (String) session.getAttribute("memberName");
		} else if (USER_TYPE_RESTAURANT.equals(userType)) {
			return (String) session.getAttribute("restName");
		}
		return "Unknown User";
	}
}