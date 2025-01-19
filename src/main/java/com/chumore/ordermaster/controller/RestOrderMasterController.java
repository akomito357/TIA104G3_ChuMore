package com.chumore.ordermaster.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.ordermaster.dto.RestDiningDto;
import com.chumore.ordermaster.dto.RestUnpaidDto;
import com.chumore.ordermaster.model.OrderMasterServiceImpl;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.ordertable.model.OrderTableService;
import com.chumore.rest.model.RestVO;

@Controller
@RequestMapping("/rest")
public class RestOrderMasterController {

	@Autowired
	HttpSession session;

	@Autowired
	OrderMasterServiceImpl ordersvc;

	@Autowired
	OrderTableService tablesvc;

	@Autowired
	OrderItem_Service orderItemSvc;

	@GetMapping("diningList")
	@ResponseBody
	public ResponseEntity<OrderMasterResponse<Page<RestDiningDto>>> findOrderByRestId(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "served_datetime,desc") String sort,
			@RequestParam(required = false) String startDatetime, @RequestParam(required = false) String endDatetime,
			@RequestParam(required = false) Integer orderTableId, @RequestParam(required = false) String memberName) {

		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
//			restId = 2001;
		} else {
			Integer rest = (Integer) restNum;
//			restId = rest.getRestId();
			restId = rest;
			
		}

		LocalDateTime start = null;
		LocalDateTime end = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

		if (startDatetime != null && !startDatetime.isEmpty() && !"null".equals(startDatetime)) {
			start = LocalDateTime.parse(startDatetime, formatter);
		}

		if (endDatetime != null && !endDatetime.isEmpty() && !"null".equals(endDatetime)) {
			end = LocalDateTime.parse(endDatetime, formatter);
		}

		String[] sortParams = sort.split(",");

		Sort sortBy;
		if (sortParams != null && sortParams.length > 1) {
			Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
			Sort.Order order = new Sort.Order(direction, sortParams[0]);
			sortBy = Sort.by(order);
		} else {
			sortBy = Sort.unsorted();
		}

		Pageable pageable = PageRequest.of(page, size, sortBy);

		Page<Map<String, Object>> orderPage = ordersvc.findOrderByRestId(restId, start, end, orderTableId, memberName,
				pageable);
		List<RestDiningDto> orderList = new ArrayList<RestDiningDto>();

		for (Map<String, Object> data : orderPage.getContent()) {
			RestDiningDto dto = new RestDiningDto();
			dto.setRestId(restId);
			dto.setServedDatetime((String) data.get("servedDatetime"));
			dto.setOrderTableId((Integer) data.get("orderTableId"));
			dto.setTableNumber((String) data.get("tableNumber"));
			dto.setMemberName((String) data.get("memberName"));
			dto.setTotalPrice((BigDecimal) data.get("totalPrice"));
			dto.setOrderId((Integer) data.get("orderId"));
			orderList.add(dto);
		}

		Page<RestDiningDto> pageContent = new PageImpl<>(orderList, orderPage.getPageable(),
				orderPage.getTotalElements());

		OrderMasterResponse<Page<RestDiningDto>> response = new OrderMasterResponse<Page<RestDiningDto>>("Success", 200,
				pageContent);
		return ResponseEntity.ok(response);

	}

	// getOneForCheckOut / getOneForUpdate（連結到商家結帳確認頁面）
	@GetMapping("getOne")
	@ResponseBody
	public String getOneForCheckOut(@RequestParam Integer orderId, Model model, HttpSession session) {
//			session.setAttribute("orderId", 1);
		if (session.getAttribute("restId") == null) {
			session.setAttribute("restId", 2001);
		}
		
		OrderMasterVO orderMaster = ordersvc.getOneById(orderId);
		model.addAttribute("orderMaster", orderMaster);
		return "secure/rest/order/rest_checkout";
	} 
	
	
	@GetMapping("getUnpaidOrder")
	@ResponseBody
	public ResponseEntity<OrderMasterResponse<Page<RestUnpaidDto>>> getOneForCheckOut(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "served_datetime,asc") String sort,
			@RequestParam(required = false) String startDatetime, @RequestParam(required = false) String endDatetime,
			@RequestParam(required = false) Integer orderTableId) {
		
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
//			restId = 2001;
		} else {
			Integer rest = (Integer) restNum;
//			restId = rest.getRestId();
			restId = rest;
			
		}

		LocalDateTime start = null;
		LocalDateTime end = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

		if (startDatetime != null && !startDatetime.isEmpty() && !"null".equals(startDatetime)) {
			start = LocalDateTime.parse(startDatetime, formatter);
		}

		if (endDatetime != null && !endDatetime.isEmpty() && !"null".equals(endDatetime)) {
			end = LocalDateTime.parse(endDatetime, formatter);
		}

		String[] sortParams = sort.split(",");

		Sort sortBy;
		if (sortParams != null && sortParams.length > 1) {
			Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
			Sort.Order order = new Sort.Order(direction, sortParams[0]);
			sortBy = Sort.by(order);
		} else {
			sortBy = Sort.unsorted();
		}

		Pageable pageable = PageRequest.of(page, size, sortBy);

		Page<Map<String, Object>> orderPage = ordersvc.findOrderByRestIdUnpaid(restId, start, end, orderTableId,
				pageable);
		List<RestUnpaidDto> orderList = new ArrayList<RestUnpaidDto>();

		for (Map<String, Object> data : orderPage.getContent()) {
			RestUnpaidDto dto = new RestUnpaidDto();
			dto.setRestId(restId);
			dto.setServedDatetime((String) data.get("servedDatetime"));
			dto.setOrderTableId((Integer) data.get("orderTableId"));
			dto.setTableNumber((String) data.get("tableNumber"));
			dto.setSubtotalPrice((BigDecimal) data.get("subtotalPrice"));
			dto.setOrderId((Integer) data.get("orderId"));
			orderList.add(dto);
		}

		Page<RestUnpaidDto> pageContent = new PageImpl<>(orderList, orderPage.getPageable(),
				orderPage.getTotalElements());

		OrderMasterResponse<Page<RestUnpaidDto>> response = new OrderMasterResponse<Page<RestUnpaidDto>>("Success", 200,
				pageContent);
		return ResponseEntity.ok(response);

	}

	@PostMapping("checkout")
	@ResponseBody
	public String checkoutSubmit(@RequestParam Map<String, String> map) {
		Integer memberId = Integer.valueOf(map.get("memberId"));
		Integer orderId = Integer.valueOf(map.get("orderId"));
		Integer pointUsed = Integer.valueOf(map.get("usePoints"));

		OrderMasterVO orderMaster = ordersvc.checkout(memberId, orderId, pointUsed);
		orderMaster = ordersvc.updateOrderMaster(orderMaster);

//		OrderMasterVO orderMaster = ordersvc.getOneById(orderId);
		System.out.println(orderMaster);

		return "success/n" + orderMaster;
	}
	
	@GetMapping("dining/history")
	public String restDiningHistory() {
		return "secure/rest/dining/restaurant_dining_history";
	}
	
	@GetMapping("check")
	public String orderCheck() {
		return "secure/rest/order/order_manage_test";
	}
	
}
