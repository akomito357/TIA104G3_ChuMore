package com.chumore.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
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

    @GetMapping("login")
    public String showLoginForm(Model model, @RequestParam(required = false) String error, 
                                @RequestParam(required = false) String logout) {
        // 確保每次載入頁面時，移除過去的錯誤訊息
        if (model.containsAttribute("errorMessage")) {
            model.asMap().remove("errorMessage");
        }
        if (model.containsAttribute("error")) {
            model.asMap().remove("error");
        }

        model.addAttribute("loginRequest", new LoginRequest());

        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "帳號或密碼錯誤，請重新輸入");
        }

        if (logout != null) {
            model.addAttribute("logout", true);
            model.addAttribute("successMessage", "您已成功登出系統");
        }

        return "auth/login";
    }
    
    @GetMapping("test")
    public String showTestPage() {
        return "auth/test";
    }

    
    @PostMapping("login")
    public String processLogin(Authentication authentication, RedirectAttributes redirectAttributes) {
        // 獲取當前已認證的用戶
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "登入失敗，請重新嘗試");
            return "redirect:/auth/login?error";
        }

        // 獲取用戶角色
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        logger.info("用戶登入成功，角色: {}", role);

        // 根據角色跳轉到對應的頁面
        if ("ROLE_MEMBER".equals(role)) {
            return "redirect:/secure/member/member_information";
        } else if ("ROLE_RESTAURANT".equals(role)) {
            return "redirect:/secure/restaurant/restaurant_information";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "未知的角色，請聯繫系統管理員");
            return "redirect:/auth/login?error";
        }
    }




    @PostMapping("/register/member")
    public String processMemberRegistration(@Valid @ModelAttribute RegisterRequest request, BindingResult result,
                                            RedirectAttributes redirectAttributes) {
        logger.info("接收到會員註冊請求: {}", request);

        if (result.hasErrors()) {
            logger.warn("會員註冊表單驗證失敗: {}", result.getAllErrors());
            addValidationErrorsToRedirectAttributes(result, request, redirectAttributes);
            return "redirect:/auth/register/member";
        }

        try {
            logger.info("開始執行會員註冊流程");
            Integer memberId = authService.registerMember(request);
            logger.info("會員註冊成功: memberId={}", memberId);

            redirectAttributes.addFlashAttribute("successMessage", "會員註冊成功，請登入");
            return "redirect:/auth/login";
        } catch (Exception e) {
            logger.error("會員註冊過程發生錯誤: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後再試");
            return "redirect:/auth/register/member";
        }
    }


    @GetMapping("/register/member")
    public String showMemberRegisterForm(Model model) {
        logger.info("顯示會員註冊頁面");

        if (!model.containsAttribute("registerRequest")) {
            logger.debug("新增空的註冊請求物件至模型");
            model.addAttribute("registerRequest", new RegisterRequest());
        }

        // 不需要手動處理 CSRF Token
        return "auth/memberRegister";
    }
    
    @GetMapping("/register/restaurant")
    public String showRestaurantRegisterForm(Model model) {
        // 確保模型中包含註冊請求物件
        if (!model.containsAttribute("restaurantRegisterRequest")) {
            model.addAttribute("restaurantRegisterRequest", new RestaurantRegisterRequest());
        }

        // 返回對應的視圖名稱
        return "auth/restaurantRegister";
    }
    
    @PostMapping("/register/restaurant")
    public String processRestaurantRegistration(@Valid @ModelAttribute RestaurantRegisterRequest request,
                                              BindingResult result,
                                              RedirectAttributes redirectAttributes) {
        logger.info("接收到餐廳註冊請求: {}", request);

        if (result.hasErrors()) {
            logger.warn("餐廳註冊表單驗證失敗: {}", result.getAllErrors());
            addValidationErrorsToRedirectAttributes(result, request, redirectAttributes);
            return REDIRECT_RESTAURANT_REGISTER;
        }

        try {
            logger.info("開始執行餐廳註冊流程");
            Integer restaurantId = authService.registerRestaurant(request);
            logger.info("餐廳註冊成功: restaurantId={}", restaurantId);

            redirectAttributes.addFlashAttribute("successMessage", "餐廳註冊成功，請登入");
            return REDIRECT_LOGIN;
        } catch (DuplicateEmailException e) {
            logger.warn("註冊失敗：電子郵件已存在");
            redirectAttributes.addFlashAttribute("errorMessage", "此電子郵件已被註冊");
            return REDIRECT_RESTAURANT_REGISTER;
        } catch (DuplicateRegistrationNumberException e) {
            logger.warn("註冊失敗：統一編號已存在");
            redirectAttributes.addFlashAttribute("errorMessage", "此統一編號已被註冊");
            return REDIRECT_RESTAURANT_REGISTER;
        } catch (DuplicatePhoneNumberException e) {
            logger.warn("註冊失敗：電話號碼已存在");
            redirectAttributes.addFlashAttribute("errorMessage", "此電話號碼已被註冊");
            return REDIRECT_RESTAURANT_REGISTER;
        } catch (Exception e) {
            logger.error("餐廳註冊過程發生錯誤: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後再試");
            return REDIRECT_RESTAURANT_REGISTER;
        }
    }



    private void addValidationErrorsToRedirectAttributes(BindingResult result, Object formObject,
                                                         RedirectAttributes redirectAttributes) {
        logger.debug("將驗證錯誤存入 RedirectAttributes");
        redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult." + formObject.getClass().getSimpleName(), result);
        redirectAttributes.addFlashAttribute(formObject.getClass().getSimpleName(), formObject);
    }
}