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
import com.chumore.auth.dto.LoginResponse;
import com.chumore.auth.dto.RegisterRequest;
import com.chumore.auth.exception.AuthenticationException;
import com.chumore.auth.exception.DuplicateEmailException;
import com.chumore.auth.exception.DuplicatePhoneNumberException;
import com.chumore.auth.service.AuthService;
import com.chumore.member.model.MemberVO;
import com.chumore.rest.model.RestVO;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    
    // 會話常數
    private static final String VIEW_PREFIX = "auth/";
    public static final String SESSION_MEMBER_ID = "memberId";
    public static final String SESSION_REST_ID = "restId";
    public static final String SESSION_USER_TYPE = "userType";
    public static final String USER_TYPE_MEMBER = "MEMBER";
    public static final String USER_TYPE_RESTAURANT = "RESTAURANT";

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return VIEW_PREFIX + "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute LoginRequest request,
                             BindingResult result,
                             HttpSession session,
                             Model model) {
        if (result.hasErrors()) {
            return VIEW_PREFIX + "login";
        }

        try {
            LoginResponse response = authService.login(request);
            if (USER_TYPE_MEMBER.equals(response.getUserType())) {
                setupMemberSession(session, response.getMemberVO());
                return "redirect:/member/dashboard";
            } else {
                setupRestaurantSession(session, response.getRestVO());
                return "redirect:/restaurant/dashboard";
            }
        } catch (AuthenticationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return VIEW_PREFIX + "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return VIEW_PREFIX + "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute RegisterRequest request,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return VIEW_PREFIX + "register";
        }

        try {
            authService.register(request);
            redirectAttributes.addFlashAttribute("successMessage", "註冊成功！請登入");
            return "redirect:/auth/login";
        } catch (DuplicateEmailException e) {
            model.addAttribute("emailError", e.getMessage());
            return VIEW_PREFIX + "register";
        } catch (DuplicatePhoneNumberException e) {
            model.addAttribute("phoneError", e.getMessage());
            return VIEW_PREFIX + "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "註冊過程發生錯誤，請稍後再試");
            return VIEW_PREFIX + "register";
        }
    }

    @PostMapping("/memlogout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "已成功登出");
        return "redirect:/auth/login";
    }

    private void setupMemberSession(HttpSession session, MemberVO member) {
        session.setAttribute(SESSION_MEMBER_ID, member.getMemberId());
        session.setAttribute(SESSION_USER_TYPE, USER_TYPE_MEMBER);
        session.setAttribute("memberName", member.getMemberName());
        session.setAttribute("memberEmail", member.getMemberEmail());
        session.setAttribute("memberPhoneNumber",member.getMemberPhoneNumber());
        session.setAttribute("memberGender", member.getMemberGender());
    }

    private void setupRestaurantSession(HttpSession session, RestVO rest) {
        session.setAttribute(SESSION_REST_ID, rest.getRestId());
        session.setAttribute(SESSION_USER_TYPE, USER_TYPE_RESTAURANT);
        session.setAttribute("restName", rest.getRestName());
        session.setAttribute("restEmail", rest.getMerchantEmail());
    }
}