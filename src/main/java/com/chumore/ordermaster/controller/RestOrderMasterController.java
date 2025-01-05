package com.chumore.ordermaster.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.ordermaster.dto.RestDiningDto;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.rest.model.RestVO;

@RestController
@RequestMapping("/rest/orderMaster")
public class RestOrderMasterController {

	@Autowired
	HttpSession session;
	
	@Autowired
	OrderMasterService ordersvc;
	
	
	public ResponseEntity<OrderMasterResponse<Page<RestDiningDto>>> findOrderByRestId(
			@RequestParam(defaultValue = "0") int Page, @RequestParam(defaultValue = "10") int size,
			@RequestParam String sort){
		
		Object restNum = session.getAttribute("restId");
		Integer restId = null;
		if(restNum == null) {
			restId = 2004;
		} else {
			RestVO rest = (RestVO) restNum;
			restId = rest.getRestId();
		}
		
		
		
				return null;
		
	}
}
