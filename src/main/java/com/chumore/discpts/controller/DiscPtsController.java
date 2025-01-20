package com.chumore.discpts.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import com.chumore.discpts.model.DiscPtsService;


@Controller
@RequestMapping("/member")
public class DiscPtsController {
    
    @Autowired
    private DiscPtsService discPtsService;
    
    @GetMapping("/points")
    public String getPointsSummary(
//            @RequestParam Integer memberId,
            Model model, HttpSession session) {
    	Integer memberId = (Integer)session.getAttribute("memberId");
        List<DiscPtsSummaryDTO> summary = discPtsService.getPointsSummary(memberId);
        model.addAttribute("pointsSummary", summary);
        return "member/points";
    }
    
}