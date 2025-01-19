package com.chumore.usepoints.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.usepoints.model.UsePointsDTO;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/secure/member")
public class UsePointsController {

    @Autowired
    private OrderMasterService orderMasterService;
    
    @Autowired
    private MemberService memberService;

    @GetMapping("/points-usage")
    public String showPointsUsageRecord(Model model, Authentication auth) {
        // 取得當前登入會員
        MemberVO member = memberService.getMemberByEmail(auth.getName());
        
        // 使用會員ID查詢該會員的訂單
        List<OrderMasterVO> orders = orderMasterService.getByMemberId(member.getMemberId());
        
        // 過濾出有使用點數的訂單
        List<UsePointsDTO> records = orders.stream()
            .filter(order -> order.getPointUsed() != null && order.getPointUsed() > 0)
            .map(order -> new UsePointsDTO(
                order.getRest().getRestName(),
                order.getPointUsed(),
                order.getCheckoutDatetime()
            ))
            .sorted((a, b) -> b.getUsageDate().compareTo(a.getUsageDate())) // 依照使用日期降序排序
            .collect(Collectors.toList());
        
        model.addAttribute("pointsRecords", records);
        return "secure/member/points_usage";
    }
}