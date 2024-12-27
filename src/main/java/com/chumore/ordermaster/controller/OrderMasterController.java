package com.chumore.ordermaster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;


public class OrderMasterController {
	
	@Autowired
	OrderMasterService ordersvc;

	// getOneForCheckOut（連結到商家結帳確認頁面）
	public String getOneForCheckOut(@RequestParam("orderId") String orderId, Model model) {
		// 之後要再把id改成從session取得
		OrderMasterVO orderMaster = ordersvc.getById(null);
		
		
		return "";
	}
	
	
	// addOrder
	// getOneForUpdate
	// updateOrder
	// getByMember
	// getByRest
	
	
	
}
