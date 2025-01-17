package com.chumore.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chumore.rest.model.RestVO;
import com.chumore.rest.model.RestService;

import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/secure/rest")
public class RestaurantDashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantDashboardController.class);
    
    private final RestService restService;
    
    @Autowired
    public RestaurantDashboardController(RestService restService) {
        this.restService = restService;
    }
    
    @GetMapping("/dashboard_restaurant")
    public String showRestaurantDashboard(Model model, HttpSession session, 
            RedirectAttributes redirectAttributes) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        String userName = (String) session.getAttribute("userName");
        
        if (userId == null || !"RESTAURANT".equals(userType)) {
            logger.warn("未授權的餐廳儀表板訪問嘗試");
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/auth/login";
        }
        
        try {
            RestVO restaurant = restService.getOneById(userId);
            
            if (restaurant.getApprovalStatus() != 1) {
                logger.warn("餐廳 ID: {} 尚未通過審核", userId);
                redirectAttributes.addFlashAttribute("warningMessage", 
                    "您的餐廳帳號尚在審核中，部分功能可能受限");
            }
            
            // 獲取營業時間資訊
            List<String> businessHours = restService.getFormattedBusinessHours(userId);
            
            model.addAttribute("restaurantName", userName);
            model.addAttribute("loginTime", session.getAttribute("loginTime"));
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("businessHours", businessHours);
            model.addAttribute("businessStatus", getBusinessStatusText(restaurant.getBusinessStatus()));
            model.addAttribute("approvalStatus", getApprovalStatusText(restaurant.getApprovalStatus()));
            
            logger.info("餐廳 ID: {} 成功訪問儀表板", userId);
            return "secure/rest/dashboard_restaurant";
            
        } catch (Exception e) {
            logger.error("餐廳儀表板載入失敗，餐廳 ID: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統錯誤，請稍後再試");
            return "redirect:/error";
        }
    }
    
    private String getBusinessStatusText(Integer status) {
        return switch (status) {
            case 0 -> "暫停營業";
            case 1 -> "營業中";
            default -> "未知狀態";
        };
    }
    
    private String getApprovalStatusText(Integer status) {
        return switch (status) {
            case 0 -> "審核中";
            case 1 -> "已審核通過";
            case 2 -> "審核未通過";
            default -> "未知狀態";
        };
    }
}