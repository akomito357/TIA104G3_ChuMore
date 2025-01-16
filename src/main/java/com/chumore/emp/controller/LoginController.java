package com.chumore.emp.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/emp/login")  // 修改路徑從 /login 到 /emp/login
    public String login() {
        return "emp/login";    // 保持相同的視圖名稱
    }
}