package com.chumore.ordermaster.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.ordermaster.dto.OrderMasterDto;
import com.chumore.ordermaster.model.OrderMasterServiceImpl;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.review.model.ReviewService;
import com.chumore.review.model.ReviewVO;

@Controller
//@RestController
@RequestMapping("/member")
public class MemberOrderMasterController {

	@Autowired
	HttpSession session;

	@Autowired
	OrderMasterServiceImpl orderSvc;

	@Autowired
	ReviewService reviewService;
	
	@Autowired
	MemberService memberSvc;

	@GetMapping("order")
	@ResponseBody
	public ResponseEntity<OrderMasterResponse<Page<OrderMasterDto>>> findByMemberId(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam String sort) {
		Object memId = session.getAttribute("memberId");
		Integer  memberId = null;
		if (memId == null) {
//			memberId = 1004;
		} else {
//			MemberVO member = (MemberVO) memberSvc.getOneMember((Integer)memId).orElse(null);
//			memberId = (Integer)member.getMemberId();
			memberId =(Integer)memId;
		}

		String[] sortParams = sort.split(",");
		String col = null;
		
		String field = sortParams[0];
		switch(field) {
			case"b":
				col = "served_datetime";
				break;
			case"c":
				col = "total_price";
				break;
			default:
				System.out.println("錯誤");
				break;
		}
		
		Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort.Order order = new Sort.Order(direction, col);
		Sort sortBy = Sort.by(order);				
				
		Pageable pageable = PageRequest.of(page, size, sortBy);

		Page<OrderMasterVO> orderPage = orderSvc.findByMemberId(memberId, pageable);
		List<OrderMasterDto> dtoList = new ArrayList<OrderMasterDto>();

		for (OrderMasterVO data : orderPage) {
			ReviewVO review = reviewService.getReviewByOrderId(data.getOrderId());
			OrderMasterDto dto = new OrderMasterDto(data, review);
			dtoList.add(dto);

		}

		Page<OrderMasterDto> dtoPage = new PageImpl<>(dtoList, orderPage.getPageable(), orderPage.getTotalElements());

		OrderMasterResponse<Page<OrderMasterDto>> response = new OrderMasterResponse<Page<OrderMasterDto>>("Success",
				200, dtoPage);
		return ResponseEntity.ok(response);
	}

	@GetMapping("dining_and_review/history")
	public String memberDiningHistory() {
		return "secure/member/dining/member_dining_history";
	}
}