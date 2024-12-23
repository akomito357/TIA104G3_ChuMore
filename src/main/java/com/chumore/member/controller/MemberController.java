package com.chumore.member.controller;

import com.chumore.member.model.MemberVO;
import com.chumore.member.model.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 顯示會員查詢頁面
     */
    @GetMapping("/select")
    public String showSelectPage() {
        return "member/select_page";
    }

    /**
     * 根據ID查詢會員
     */
    @GetMapping("/getOne")
    public String getOne(@RequestParam("memberId") String memberIdStr, Model model) {
        List<String> errorMsgs = new LinkedList<>();
        
        // 驗證輸入
        if (memberIdStr == null || memberIdStr.trim().isEmpty()) {
            errorMsgs.add("請輸入會員編號");
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/select_page";
        }

        try {
            Integer memberId = Integer.valueOf(memberIdStr);
            MemberVO memberVO = memberService.getOneMember(memberId)
                .orElseThrow(() -> new IllegalArgumentException("查無資料"));
            
            model.addAttribute("memberVO", memberVO);
            return "member/listOneMember";
            
        } catch (NumberFormatException e) {
            errorMsgs.add("會員編號格式不正確");
        } catch (IllegalArgumentException e) {
            errorMsgs.add(e.getMessage());
        }
        
        model.addAttribute("errorMsgs", errorMsgs);
        return "member/select_page";
    }

    /**
     * 顯示會員更新表單
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer memberId, Model model) {
        MemberVO memberVO = memberService.getOneMember(memberId)
            .orElseThrow(() -> new IllegalArgumentException("會員不存在"));
        model.addAttribute("memberVO", memberVO);
        return "member/update_member_input";
    }

    /**
     * 更新會員資料
     */
    @PostMapping("/update")
    public String updateMember(@Validated @ModelAttribute("memberVO") MemberVO memberVO,
                             BindingResult result, Model model) {
        List<String> errorMsgs = validateMemberData(memberVO);
        
        if (result.hasErrors() || !errorMsgs.isEmpty()) {
            model.addAttribute("memberVO", memberVO);
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/update_member_input";
        }

        try {
            memberVO = memberService.updateMember(
                memberVO.getMemberId(),
                memberVO.getMemberName(),
                memberVO.getMemberEmail(),
                memberVO.getMemberPassword(),
                memberVO.getMemberPhoneNumber(),
                memberVO.getMemberGender(),
                memberVO.getMemberBirthdate(),
                memberVO.getMemberAddress()
            );
            
            model.addAttribute("memberVO", memberVO);
            return "member/listOneMember";
            
        } catch (Exception e) {
            errorMsgs.add("修改資料失敗: " + e.getMessage());
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/update_member_input";
        }
    }

    /**
     * 新增會員
     */
    @PostMapping("/add")
    public String addMember(@Validated @ModelAttribute("memberVO") MemberVO memberVO,
                          BindingResult result, Model model) {
        List<String> errorMsgs = validateMemberData(memberVO);
        
        if (result.hasErrors() || !errorMsgs.isEmpty()) {
            model.addAttribute("memberVO", memberVO);
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/addMember";
        }

        try {
            memberVO = memberService.addMember(
                memberVO.getMemberName(),
                memberVO.getMemberEmail(),
                memberVO.getMemberPassword(),
                memberVO.getMemberPhoneNumber(),
                memberVO.getMemberGender(),
                memberVO.getMemberBirthdate(),
                memberVO.getMemberAddress()
            );
            
            return "redirect:/member/list";
            
        } catch (Exception e) {
            errorMsgs.add("新增資料失敗: " + e.getMessage());
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/addMember";
        }
    }

    /**
     * 刪除會員
     */
    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") Integer memberId, Model model) {
        List<String> errorMsgs = new LinkedList<>();
        
        try {
            memberService.deleteMember(memberId);
            return "redirect:/member/list";
            
        } catch (Exception e) {
            errorMsgs.add("刪除資料失敗: " + e.getMessage());
            model.addAttribute("errorMsgs", errorMsgs);
            return "member/listAllMember";
        }
    }

    /**
     * 驗證會員資料
     */
    private List<String> validateMemberData(MemberVO memberVO) {
        List<String> errorMsgs = new LinkedList<>();
        
        // 驗證姓名
        String nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z)]{2,10}$";
        if (memberVO.getMemberName() == null || memberVO.getMemberName().trim().isEmpty()) {
            errorMsgs.add("會員姓名: 請勿空白");
        } else if (!memberVO.getMemberName().trim().matches(nameReg)) {
            errorMsgs.add("會員姓名: 只能是中、英文字母, 且長度必需在2到10之間");
        }

        // 驗證電子郵件
        String emailReg = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (memberVO.getMemberEmail() == null || memberVO.getMemberEmail().trim().isEmpty()) {
            errorMsgs.add("電子郵件: 請勿空白");
        } else if (!memberVO.getMemberEmail().trim().matches(emailReg)) {
            errorMsgs.add("請輸入有效的電子郵件地址");
        }

        // 驗證密碼
        if (memberVO.getMemberPassword() == null || memberVO.getMemberPassword().trim().isEmpty()) {
            errorMsgs.add("密碼請勿空白");
        }

        // 驗證手機號碼
        String phoneReg = "^09\\d{8}$";
        if (memberVO.getMemberPhoneNumber() == null || memberVO.getMemberPhoneNumber().trim().isEmpty()) {
            errorMsgs.add("手機號碼: 請勿空白");
        } else if (!memberVO.getMemberPhoneNumber().trim().matches(phoneReg)) {
            errorMsgs.add("請輸入有效的手機號碼格式");
        }

        // 驗證性別
        if (memberVO.getMemberGender() == null || 
            (memberVO.getMemberGender() != 0 && memberVO.getMemberGender() != 1)) {
            errorMsgs.add("性別請選擇有效選項");
        }

        // 驗證地址
        if (memberVO.getMemberAddress() == null || memberVO.getMemberAddress().trim().isEmpty()) {
            errorMsgs.add("地址請勿空白");
        }

        return errorMsgs;
    }

    /**
     * 顯示所有會員列表
     */
    @GetMapping("/list")
    public String listAllMembers(Model model) {
        List<MemberVO> members = memberService.getAll();
        model.addAttribute("members", members);
        return "member/listAllMember";
    }
}