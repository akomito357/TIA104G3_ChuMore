package com.chumore.discpts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import com.chumore.discpts.model.DiscPtsService;

import java.util.List;


@Controller
@RequestMapping("/member")
public class DiscPtsController {
    
    @Autowired
    private DiscPtsService discPtsService;
    
    @GetMapping("/points")
    public String getPointsSummary(
            @RequestParam Integer memberId,
            Model model) {
        List<DiscPtsSummaryDTO> summary = discPtsService.getPointsSummary(memberId);
        model.addAttribute("pointsSummary", summary);
        return "member/points";
    }
}