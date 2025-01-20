package com.chumore.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;

@Controller
@RequestMapping("/secure/rest")
public class RestMemberController {
    private static final Logger logger = LoggerFactory.getLogger(RestMemberController.class);
    private final RestService restSvc;

    @Autowired
    public RestMemberController(RestService restSvc) {
        this.restSvc = restSvc;
    }

    @GetMapping("/rest_information")
    public String showRestInformation(Model model, HttpSession session) {
        logger.debug("開始處理餐廳資訊請求");

        try {
            // 從 Security Context 獲取當前登入用戶信箱
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String merchantEmail = authentication.getName();
            
            // 使用信箱查詢餐廳資料
            RestVO restaurantData = restSvc.getOneByEmail(merchantEmail);
            
            if (restaurantData == null) {
                logger.error("無法找到商家信箱 {} 對應的餐廳資訊", merchantEmail);
                return handleDataError(model, "無法載入餐廳資料，請稍後再試");
            }

            // 設置會話屬性
            session.setAttribute("restId", restaurantData.getRestId());
            session.setAttribute("userType", "ROLE_RESTAURANT");

            // 設置模型資料
            model.addAttribute("restaurant", restaurantData);
            logger.info("成功載入餐廳 '{}' 的資訊", restaurantData.getRestName());

            return "secure/rest/rest_information";

        } catch (Exception e) {
            logger.error("處理餐廳資訊時發生錯誤", e);
            return handleSystemError(model, "系統發生錯誤，請稍後再試");
        }
    }

    private String handleDataError(Model model, String message) {
        model.addAttribute("errorMessage", message);
        return "redirect:/auth/login";  // 修改為正確的登入頁面路徑
    }

    private String handleSystemError(Model model, String message) {
        model.addAttribute("errorMessage", message);
        return "redirect:/auth/login";  // 修改為正確的登入頁面路徑
    }
}