package com.chumore.discpts.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import com.chumore.discpts.model.DiscPtsService;
import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.util.ResponseUtil;


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