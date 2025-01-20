package com.chumore.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.exception.ResourceNotFoundException;
import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.util.ResponseUtil;

@Controller("restOrderMemberController")
@RequestMapping("/rests/member")
public class RestMemberController {
	
	@Autowired
    private MemberService memberService;

	// 用電話號碼搜尋會員（商家結帳用）
    @PostMapping("findMemberByPhone")
    @ResponseBody
    public ResponseEntity<?> findMemberByPhoneNumber(@RequestParam String memberPhoneNumber){
    	MemberVO member = memberService.findMemberByPhoneNumber(memberPhoneNumber).orElse(null);
    	
    	if (member == null) {
    		throw new ResourceNotFoundException("No member found for phone number = " + memberPhoneNumber);
    	}
    	
    	ResponseUtil res = new ResponseUtil("success", 200, member);
    	return ResponseEntity.ok(res);
    	
    }
	
}
