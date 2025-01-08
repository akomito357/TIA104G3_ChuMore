package com.chumore.ordermaster.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.ordermaster.model.OrderMasterServiceImpl;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.ordertable.model.OrderTableService;
import com.chumore.rest.model.RestVO;

@RestController
@RequestMapping("/rest/orderMaster")
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
	public ResponseEntity<OrderMasterResponse<Page<Map<String, Object>>>> findOrderByRestId(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "served_datetime,asc") String sort,
			@RequestParam(required = false) String startDatetime, @RequestParam(required = false) String endDatetime,
			@RequestParam(required = false) String tableNumber, @RequestParam(required = false) String memberName) {

		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if (restNum == null) {
			restId = 2001;
		} else {
			RestVO rest = (RestVO) restNum;
			restId = rest.getRestId();
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

//		String col = null;

//		String field = sortParams[0];
//		switch(field) {
//			case"a":
//				col = "served_datetime";
//				break;
//			case"c":
//				col = "total_price";
//				break;
//			default:
//				System.out.println("錯誤");
//				break;
//		}
//		Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//		Sort.Order order = new Sort.Order(direction, sortParams[0]);
//		Sort sortBy = Sort.by(order);

		Sort sortBy;
		if (sortParams != null && sortParams.length > 1) {
		    Sort.Direction direction = sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		    Sort.Order order = new Sort.Order(direction, sortParams[0]);
		    sortBy = Sort.by(order);
		} else {
		    sortBy = Sort.unsorted(); 
		}

		
		Pageable pageable = PageRequest.of(page, size, sortBy);
		

		Page<Map<String, Object>> orderPage = ordersvc.findOrderByRestId(restId, start, end, tableNumber, memberName, pageable);

		OrderMasterResponse<Page<Map<String, Object>>> response = new OrderMasterResponse<Page<Map<String, Object>>>(
				"Success", 200, orderPage);
		return ResponseEntity.ok(response);

	}

	
	@GetMapping("getOne")
	public String getOneForCheckOut(@RequestParam Integer orderId, ModelMap model) {
//		session.setAttribute("orderId", 1);
		OrderMasterVO orderMaster = ordersvc.getOneById(orderId);
		List<OrderItemVO> orderItems = orderItemSvc.getOrderItemListByOrderId(orderId);
		model.addAttribute("orderMaster", orderMaster);
		
		return "";
	} 
	
}
