package com;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/member")
@Controller
public class MemberHeaderController {

	// 帳號資訊
	@GetMapping("member_information")
	public String toMemberInformation() {
		return "secure/member/member_information";
	}
	
	// 用餐評價紀錄的mapping已經在controller裡面
	
	// 餐廳預約紀錄
	@GetMapping("member_reservations")
	public String toMemberDiningHistory() {
		return "secure/member/reservation/member_reservations";
	}
	
	// 點數使用
	@GetMapping("points_usage")
	public String toMemberPointUsage() {
		return "secure/member/points_usage";
	}
	
	
	
	
}
