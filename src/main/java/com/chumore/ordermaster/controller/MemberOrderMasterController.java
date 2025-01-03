package com.chumore.ordermaster.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.member.model.MemberVO;
import com.chumore.ordermaster.dto.OrderMasterDto;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.review.model.ReviewService;
import com.chumore.review.model.ReviewVO;

//@Controller
@RestController
@RequestMapping("/member/orderMaster")
public class MemberOrderMasterController {

	@Autowired
	HttpSession session;
	
	@Autowired
	OrderMasterService orderSvc;
	
	@Autowired
	ReviewService reviewService;
	
	@GetMapping("findByMemberId")
	public ResponseEntity<OrderMasterResponse> findByMemberId(){
		Object memId = session.getAttribute("memberId");
		Integer memberId = null;
		if(memId == null) {
			memberId = 1002;			
		} else {
			MemberVO member = (MemberVO) memId;
			memberId = member.getMemberId();
		}
		
		List<OrderMasterVO> list = orderSvc.getByMemberId(memberId);

		List<OrderMasterDto> dtoList = new ArrayList<OrderMasterDto>();
		
		
		for(OrderMasterVO data : list) {
			ReviewVO review = reviewService.getReviewByOrderId(data.getOrderId());
			OrderMasterDto dto = new OrderMasterDto(data, review);
			dtoList.add(dto);
		}
		
		OrderMasterResponse<List<OrderMasterDto>> response = new OrderMasterResponse<List<OrderMasterDto>>("Success",200,dtoList);
		return ResponseEntity.ok(response);
	}

}
