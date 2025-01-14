package com.chumore.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chumore.auth.dto.AuthenticatedUser;
import com.chumore.auth.dto.LoginRequest;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.dto.RestaurantRegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;
import com.chumore.auth.exception.DuplicateRegistrationNumberException;
import com.chumore.auth.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String VIEW_PREFIX = "auth/";
    private static final String REDIRECT_LOGIN = "redirect:/auth/login";
    private static final String REDIRECT_MEMBER_REGISTER = "redirect:/auth/register/member";
    private static final String REDIRECT_RESTAURANT_REGISTER = "redirect:/auth/register/restaurant";

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model,
                              @RequestParam(required = false) String error,
                              @RequestParam(required = false) String logout) {
        logger.info("處理登入頁面請求");
        model.addAttribute("loginRequest", new LoginRequest());

        if (error != null) {
            logger.warn("使用者登入失敗");
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "帳號或密碼錯誤，請重新輸入");
        }

        if (logout != null) {
            logger.info("使用者已成功登出");
            model.addAttribute("logout", true);
            model.addAttribute("successMessage", "您已成功登出系統");
        }

        logger.debug("轉向登入頁面視圖");
        return VIEW_PREFIX + "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @Valid @ModelAttribute LoginRequest loginRequest,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        logger.info("接收到登入請求: {}", loginRequest.getEmail());

        if (result.hasErrors()) {
            logger.warn("表單驗證失敗: {}", result.getAllErrors());
            addValidationErrorsToRedirectAttributes(result, loginRequest, redirectAttributes);
            return REDIRECT_LOGIN;
        }

        try {
            AuthenticatedUser user = authService.authenticateAndGetUser(loginRequest);
            logger.info("用戶驗證成功: {}", user.getEmail());

            setupUserSession(session, user);
            return determineRedirectPath(user.getUserType());

        } catch (AuthenticationException e) {
            logger.error("登入驗證失敗: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return REDIRECT_LOGIN;
        }
    }

    private void setupUserSession(HttpSession session, AuthenticatedUser user) {
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userType", user.getUserType());
        session.setAttribute("userName", user.getName());
        session.setAttribute("loginTime", LocalDateTime.now().toString());
        logger.info("用戶會話已設置: userId={}, userType={}", user.getUserId(), user.getUserType());
    }

    private String determineRedirectPath(String userType) {
        return switch (userType) {
            case "MEMBER" -> "redirect:/secure/member/member_dashboard";
            case "RESTAURANT" -> "redirect:/secure/rest/rest_information";
            default -> {
                logger.error("未知的身份類型: {}", userType);
                throw new AuthenticationException("未知的身份類型: " + userType);
            }
        };
    }

    @GetMapping("/register/member")
    public String showMemberRegisterForm(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return VIEW_PREFIX + "memberRegister";
    }

    @PostMapping("/register/member")
    public String processMemberRegistration(
            @Valid @ModelAttribute RegisterRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("會員註冊表單驗證失敗: {}", result.getAllErrors());
            addValidationErrorsToRedirectAttributes(result, request, redirectAttributes);
            return REDIRECT_MEMBER_REGISTER;
        }

        try {
            Integer memberId = authService.registerMember(request);
            logger.info("會員註冊成功: memberId={}", memberId);
            redirectAttributes.addFlashAttribute("successMessage", "會員註冊成功，請登入");
            return REDIRECT_LOGIN;

        } catch (DuplicateEmailException | DuplicatePhoneNumberException e) {
            logger.warn("會員註冊驗證失敗: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("registerRequest", request);
            return REDIRECT_MEMBER_REGISTER;

        } catch (Exception e) {
            logger.error("會員註冊過程發生未預期錯誤", e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後再試");
            return REDIRECT_MEMBER_REGISTER;
        }
    }

    @GetMapping("/register/restaurant")
    public String showRestaurantRegisterForm(Model model) {
        if (!model.containsAttribute("restaurantRegisterRequest")) {
            model.addAttribute("restaurantRegisterRequest", new RestaurantRegisterRequest());
        }
        return VIEW_PREFIX + "restaurantRegister";
    }

    @PostMapping("/register/restaurant")
    public String processRestaurantRegistration(
            @Valid @ModelAttribute RestaurantRegisterRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            logger.warn("餐廳註冊表單驗證失敗: {}", result.getAllErrors());
            addValidationErrorsToRedirectAttributes(result, request, redirectAttributes);
            return REDIRECT_RESTAURANT_REGISTER;
        }

        try {
            Integer restaurantId = authService.registerRestaurant(request);
            logger.info("餐廳註冊成功: restaurantId={}", restaurantId);
            redirectAttributes.addFlashAttribute("successMessage", "餐廳註冊成功，請等待審核通過後登入");
            return REDIRECT_LOGIN;

        } catch (DuplicateEmailException | DuplicatePhoneNumberException | 
                DuplicateRegistrationNumberException e) {
            logger.warn("餐廳註冊驗證失敗: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("restaurantRegisterRequest", request);
            return REDIRECT_RESTAURANT_REGISTER;

        } catch (Exception e) {
            logger.error("餐廳註冊過程發生未預期錯誤", e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後再試");
            return REDIRECT_RESTAURANT_REGISTER;
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        logger.info("用戶登出: userId={}, userType={}", userId, userType);

        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "您已成功登出");
        return REDIRECT_LOGIN;
    }
    
    private void addValidationErrorsToRedirectAttributes(
            BindingResult result, Object formObject, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
            "org.springframework.validation.BindingResult." + formObject.getClass().getSimpleName(), result);
        redirectAttributes.addFlashAttribute(formObject.getClass().getSimpleName(), formObject);
    }

}