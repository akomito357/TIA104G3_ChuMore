package com.chumore.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;

@Controller
@RequestMapping("/secure/rest")
public class RestMemberController {
    
    @Autowired
    RestService restSvc;
    
    @GetMapping("/rest_information")
    public String showRestInformation(Model model) {
        // 獲取當前登入的用戶信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();  // 這會得到當前登入的 email
        
        // 用 email 查詢餐廳資料
        RestVO rest = restSvc.getOneByEmail(currentUserEmail);
        if (rest != null) {
            model.addAttribute("rest", rest);
            // 可以加入日誌來debug
            System.out.println("Found restaurant with email: " + currentUserEmail);
            System.out.println("Restaurant name: " + rest.getMerchantName());
        } else {
            // 如果找不到資料，記錄錯誤
            System.out.println("No restaurant found for email: " + currentUserEmail);
        }
        
        return "secure/rest/rest_information";
    }
}