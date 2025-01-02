package com.chumore.ordermaster.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;

@Controller
@RequestMapping("/orders")
public class OrderMasterController {
	
	@Autowired
	OrderMasterService orderSvc;

	// getOneForCheckOut / getOneForUpdate（連結到商家結帳確認頁面）
	@PostMapping("getOne")
	public String getOneForCheckOut(@RequestParam("orderId") String orderId, Model model) {
		// 之後要再把id改成從session取得
		OrderMasterVO orderMaster = orderSvc.getOneById(Integer.valueOf(orderId));
		model.addAttribute("orderMaster", orderMaster);		
		return "";
	}
	
//	// getOneOrder (RESTful)
//	public ResponseEntity<OrderMasterResponse> getOneOrder(@RequestBody Map<String, Integer>, ){
//		
//	}
	
	
	// updateOrder
	@PostMapping("updateOrder")
	public String updateOrder(@Valid OrderMasterVO orderMaster, BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return ""; // 返回並進行錯誤顯示
		}
		orderSvc.updateOrderMaster(orderMaster);
		orderMaster = orderSvc.getOneById(Integer.valueOf(orderMaster.getOrderId()));
		model.addAttribute("orderMaster", orderMaster);
		return "";
	}
	
	
	// addOrder
	@GetMapping("addOrder")
	public String addOrder(ModelMap model) {
		OrderMasterVO orderMaster = new OrderMasterVO(); // 此處提供基礎結構，用於前端頁面與後端的數據綁定。（通道）
		model.addAttribute("orderMaster", orderMaster);
		return ""; // 是否需要一個開始點餐的按鈕以發送此指令
	}
	
	// insert
	@PostMapping("insert")
	public ResponseEntity<OrderMasterResponse> insert(@RequestBody OrderMasterVO orderMaster){
		OrderMasterVO vo = null;
		OrderMasterResponse<OrderMasterVO> res = null;
		try {
			vo = orderSvc.addOrderMaster(orderMaster);
			res = new OrderMasterResponse<>("success",200, vo);
			return ResponseEntity.ok(res);
		}catch (Exception e){
			res = new OrderMasterResponse<>("error",400, vo);
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	// insert
//	public String insertOrder(@Valid OrderMasterVO orderMaster, BindingResult result, ModelMap model) {
//		// 1. 錯誤處理
//		if(result.hasErrors()) {
//			return ""; // return to show error msg
//		}
//		// 2. 永續層存取，新增資料
//		orderSvc.addOrderMaster(orderMaster);
//		// 3. 準備轉交
//		orderMaster = orderSvc.getOneById(orderMaster.getOrderId());
//		model.addAttribute("orderMaster", orderMaster);		
//		return "";
//	}
	
	// get by Member or Rest or else
	@GetMapping("findOrderBy")
	public String getOrdersByOther(HttpServletRequest req, Model model) {
		// 1. 錯誤處理
		// 2. 永續層存取，新增資料
		Map<String, String[]> map = req.getParameterMap();
		List<OrderMasterVO> list = orderSvc.getAllOrder(map);
		model.addAttribute("memberOrderList", list);
		// 3. 準備轉交
		return "";
	}
	
	@GetMapping("test")
	public String getOneForOrder(HttpSession session, ModelMap map) {
		session.setAttribute("orderId", 1);
		return "/secure/rest/order/working_order_page";
	}
	
}
