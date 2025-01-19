package com.chumore.member.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chumore.exception.ResourceNotFoundException;
import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.util.ResponseUtil;

@Controller
@RequestMapping("/secure/member")
public class MemberController {
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @GetMapping("/member_information")
    public String showMemberInformation(Model model, Authentication authentication) {
        try {
            String memberEmail = authentication.getName();
            logger.debug("正在載入會員資料，Email: {}", memberEmail);

            MemberVO member = memberService.findMemberByEmail(memberEmail)
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員資料"));

            model.addAttribute("member", member);
            logger.info("成功載入會員 {} 的資料", member.getMemberName());

            return "secure/member/member_information";

        } catch (ResourceNotFoundException e) {
            logger.error("找不到會員資料: {}", e.getMessage());
            return "redirect:/auth/login";
        } catch (Exception e) {
            logger.error("載入會員資料時發生錯誤", e);
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/update")
    public String updateMemberInformation(
            @Validated @ModelAttribute("member") MemberVO memberVO,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        logger.debug("開始處理會員資料更新請求");
        String currentUserEmail = authentication.getName();

        try {
            // 獲取當前會員資料
            MemberVO currentMember = memberService.findMemberByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("找不到會員資料"));
            
            // 設置不可修改的欄位
            memberVO.setMemberId(currentMember.getMemberId());
            memberVO.setMemberEmail(currentMember.getMemberEmail());
            
            // 檢查必填欄位
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("errorMessages", 
                    result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList()));
                return "redirect:/secure/member/member_information";
            }

            // 如果未更改密碼，保留原密碼
            if (memberVO.getMemberPassword() == null || memberVO.getMemberPassword().trim().isEmpty()) {
                memberVO.setMemberPassword(currentMember.getMemberPassword());
            }

            // 更新會員資料
            memberService.updateMember(
                memberVO.getMemberId(),
                memberVO.getMemberName(),
                memberVO.getMemberEmail(),
                memberVO.getMemberPassword(),
                memberVO.getMemberPhoneNumber(),
                memberVO.getMemberGender(),
                memberVO.getMemberBirthdate(),
                memberVO.getMemberAddress()
            );

            logger.info("會員資料更新成功，會員ID: {}", memberVO.getMemberId());
            redirectAttributes.addFlashAttribute("successMessage", "資料更新成功");
            
        } catch (ResourceNotFoundException e) {
            logger.error("更新失敗：找不到會員資料", e);
            redirectAttributes.addFlashAttribute("errorMessage", "找不到會員資料");
        } catch (Exception e) {
            logger.error("會員資料更新過程中發生錯誤", e);
            redirectAttributes.addFlashAttribute("errorMessage", "系統發生錯誤，請稍後再試");
        }

        return "redirect:/secure/member/member_information";
    }
}