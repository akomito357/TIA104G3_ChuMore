package com.chumore.dashboard;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secure/member")
public class MemberDashboardController {
    
    @GetMapping("/dashboard_member")
    public String showMemberDashboard(Model model, HttpSession session) {
        // 確保變數名稱與 session 屬性名稱完全一致
        Integer userId = (Integer) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        String userType = (String) session.getAttribute("userType");
        
        // 驗證用戶身份和登入狀態
        if (userId == null || !"MEMBER".equals(userType)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("userName", userName);
        model.addAttribute("loginTime", session.getAttribute("loginTime"));
        return "secure/member/dashboard_member";
    }
}