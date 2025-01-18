package com.chumore.ordermaster.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Enumeration;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chumore.exception.OrderTableNotFoundException;
import com.chumore.orderitem.dto.OrderItemForOrderDto;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.ordermaster.res.OrderMasterResponse;
import com.chumore.ordertable.model.OrderTableService;
import com.chumore.ordertable.model.OrderTableVO;
import com.chumore.rest.model.RestService;

@CrossOrigin // for dev
@Controller
@RequestMapping("/orders")
public class OrderMasterController {
	
	@Autowired
	OrderMasterService orderSvc;
	
	@Autowired
	OrderTableService orderTableSvc;
	
	@Autowired
	RestService restSvc;

//	// getOneForCheckOut / getOneForUpdate（連結到商家結帳確認頁面）
//	@GetMapping("getOne")
//	public String getOneForCheckOut(@RequestParam Integer orderId, Model model, HttpSession session) {
////		session.setAttribute("orderId", 1);
//		if (session.getAttribute("restId") == null) {
//			session.setAttribute("restId", 2001);
//		}
//		
//		OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
//		model.addAttribute("orderMaster", orderMaster);
//		return "secure/rest/order/rest_checkout";
//	}
	
	// getOneOrder (RESTful)
	@GetMapping("findOneByOrderId")
	@ResponseBody
	public ResponseEntity<OrderMasterResponse> findOneOrder(@RequestParam Integer orderId){
		OrderMasterResponse<OrderMasterVO> res = null;
		OrderMasterVO orderMaster = null;
		try {
//			Integer orderId = req.get("orderId");
			orderMaster = orderSvc.getOneById(orderId);
			res = new OrderMasterResponse<>("success", 200, orderMaster);
			return ResponseEntity.ok(res);
		}catch (Exception e){
			res = new OrderMasterResponse<>("error", 400, orderMaster);
			return ResponseEntity.badRequest().body(res);
		}
	}
	
	
	// updateOrder
	@PostMapping("updateOrder")
	public String updateOrder(@Valid OrderMasterVO orderMaster, BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return ""; // 返回並進行錯誤顯示
		}
//		orderSvc.updateOrderMaster(orderMaster);
		orderMaster = orderSvc.getOneById(Integer.valueOf(orderMaster.getOrderId()));
		model.addAttribute("orderMaster", orderMaster);
		return "";
	}
	
	
	// addOrder - for QRcode to send GET request - 導到桌位對應的開始點餐頁面
	@GetMapping("addOrder/{restId}/{tableNumber}")
	public String addOrder(@PathVariable Integer restId, @PathVariable String tableNumber, ModelMap model, HttpSession session) {		
//		OrderTableVO orderTable = orderTableSvc.getOrderTableById(orderTableId);
//		Set<OrderTableVO> tableSet = restSvc.getOneById(restId).getOrderTables();
		OrderMasterVO orderMaster = null;
		OrderTableVO orderTable = orderTableSvc.findByRestIdAndNumber(restId, tableNumber);
		if (orderTable == null) {
			// 當抓不到對應的桌位時
			throw new OrderTableNotFoundException(
					"OrderTable which restId = " + restId + " and tableNumber = " + tableNumber + " is not found.");
		}

		
		session.setAttribute("restId", restId);
		session.setAttribute("tableNumber", tableNumber);
		


		if (session.getAttribute("orderId") == null) {
			orderMaster = new OrderMasterVO();
			orderMaster.setOrderTable(orderTable);
			orderMaster.setRest(orderTable.getRest());
		} else {
			Integer orderId = (Integer)session.getAttribute("orderId");
			orderMaster = orderSvc.getOneById(orderId);
		}
		

		model.addAttribute("orderMaster", orderMaster);
		return "public/order/order_start_page";
	}
	
	// insertOrder - for "開始點餐"btn in order_start_page.html
	@PostMapping("insert")
	public String insert(@ModelAttribute("orderMaster") OrderMasterVO orderMaster, 
			BindingResult result, ModelMap model, HttpSession session) {
		
			orderMaster.getOrderTable().getOrderTableId();
			// 1. 錯誤處理
			if(result.hasErrors()) {
				return ""; // return to show error msg
			}
			// 2. 永續層存取，新增資料
//		System.out.println(session.getAttribute("orderId"));
			if (session.getAttribute("orderId") == null) {
				orderMaster.setOrderStatus(0);
				orderMaster.setSubtotalPrice(new BigDecimal("0"));
				orderMaster.setServedDatetime(LocalDateTime.now());
				orderSvc.addOrderMaster(orderMaster);
			} else {
				orderMaster.setOrderId((Integer)session.getAttribute("orderId"));
			}
			
			// 3. 準備轉交
			session.setAttribute("orderId", orderMaster.getOrderId());
			orderMaster = orderSvc.getOneById(orderMaster.getOrderId());
			model.addAttribute("orderMaster", orderMaster);
		
		return "public/order/order_page";
	}
	
	@PostMapping("cart")
	public String toCart(HttpSession session, ModelMap model) {
		Integer orderId = (Integer)session.getAttribute("orderId");
		OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
		model.addAttribute("orderMaster", orderMaster);
		return "public/order/order_cart";
	}
	
	@PostMapping("order")
	public String toOrderPage(HttpSession session, ModelMap model) {
		Integer orderId = (Integer)session.getAttribute("orderId");
		OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
		model.addAttribute("orderMaster", orderMaster);
		return "public/order/order_page";
	}
	
	@PostMapping("submit")
	@ResponseBody
	public ResponseEntity<OrderMasterResponse> submitOrderItems(@RequestBody OrderItemForOrderDto item, HttpSession session) {
//		OrderMasterResponse res = null;
		orderSvc.submitOrder(item, session);
		// 若上面此行有錯誤，service層會拋出DataMismatchException，由GlobalExceptionHandler處理
		OrderMasterResponse res = new OrderMasterResponse<>("success", 200, item);
		return ResponseEntity.ok(res);		
	}
	
	
	// 連結到消費者用餐完畢/結帳確認頁面
	@PostMapping("finishOrder")
	public String getOneForCheckOut(@RequestBody Map<String, Integer> map, Model model, HttpSession session) {
//				session.setAttribute("orderId", 1);
//		if (session.getAttribute("restId") == null) {
//			session.setAttribute("restId", 2001);
//		}
		
		OrderMasterVO orderMaster = orderSvc.getOneById(map.get("orderId"));
		model.addAttribute("orderMaster", orderMaster);
		
		Enumeration names = session.getAttributeNames();
		
		while (names.hasMoreElements()) {
			String name = (String)names.nextElement();
			System.out.println(name + ": " + session.getAttribute(name));
		}
		
		session.removeAttribute("restId");
		session.removeAttribute("tableName");
		session.removeAttribute("orderId");
		
		names = session.getAttributeNames();
		
		while (names.hasMoreElements()) {
			String name = (String)names.nextElement();
			System.out.println("===");
			System.out.println(name + ": " + session.getAttribute(name));
			System.out.println("===");
		}
		
		return "public/order/order_finish_page";
	}
	
	
//	@PostMapping("findOneFromSession")
//	@ResponseBody
//	public ResponseEntity<OrderMasterResponse> insert(HttpSession session){
//		OrderMasterResponse<OrderMasterVO> res = null;
//		System.out.println(session.getAttribute("orderMaster"));
////		OrderMasterVO orderMaster = new OrderMasterVO();
//		
//		// 請求現有session中的orderMaster
//		Integer orderId = (Integer)session.getAttribute("orderId");
//		
//		try {
////			orderMaster = orderSvc.getOneById(orderMaster.getOrderId());
//			System.out.println("order: " + orderId);
////			session.setAttribute("orderMaster", orderMaster);
//			res = new OrderMasterResponse<>("success", 200, orderId);
//			return ResponseEntity.ok(res);
//		}catch (Exception e){
//			res = new OrderMasterResponse<>("error",400, orderId);
//			return ResponseEntity.badRequest().body(res);
//		}
//	}
	
	
	// RESTFul
//	@PostMapping("insert")
//	@ResponseBody
//	public ResponseEntity<OrderMasterResponse> insert(@RequestBody Map<String, String> orderMasterJson, HttpSession session){
//		OrderMasterResponse<OrderMasterVO> res = null;
//		OrderMasterVO orderMaster = new OrderMasterVO();
//		
//		Integer orderTableId = Integer.valueOf(orderMasterJson.get("orderTableId"));
//		orderMaster.setOrderTable(orderTableSvc.getOneOrderTable(orderTableId));
//		Integer restId = Integer.valueOf(orderMasterJson.get("restId"));
//		orderMaster.setRest(restSvc.getOneById(restId));
//		Integer orderStatus = Integer.valueOf(orderMasterJson.get("orderStatus"));
//		orderMaster.setOrderStatus(orderStatus);
//		System.out.println(orderMasterJson.get("servedDatetime"));
//		orderMaster.setServedDatetime(LocalDateTime.now());
//		
//		orderSvc.addOrderMaster(orderMaster);
//		
//		try {
//			orderMaster = orderSvc.getOneById(orderMaster.getOrderId());
//			System.out.println("order: " + orderMaster);
//			session.setAttribute("orderMaster", orderMaster);
//			res = new OrderMasterResponse<>("success", 200, orderMaster);
//			return ResponseEntity.ok(res);
//		}catch (Exception e){
//			res = new OrderMasterResponse<>("error",400, orderMaster);
//			return ResponseEntity.badRequest().body(res);
//		}
//	}
	
	
	// RESTFul
//	public ResponseEntity<OrderMasterResponse> insert(@RequestBody OrderMasterVO orderMaster){
//		OrderMasterVO vo = null;
//		OrderMasterResponse<OrderMasterVO> res = null;
//		try {
//			vo = orderSvc.addOrderMaster(orderMaster);
//			res = new OrderMasterResponse<>("success",200, vo);
//			return ResponseEntity.ok(res);
//		}catch (Exception e){
//			res = new OrderMasterResponse<>("error",400, vo);
//			return ResponseEntity.badRequest().body(res);
//		}
//	}
	
	
	
//	@GetMapping("addOrder")
//	public String addOrder(HttpSession session, ModelMap model) {
//		// Spring 會自動注入現有session，底層仍是HttpServletRequest.getSession()
//		OrderMasterVO orderMaster = null;
//		if (session.getAttribute("orderMaster") != null) {
//			orderMaster = (OrderMasterVO)session.getAttribute("orderMaster");
//		} else {			
//			orderMaster = new OrderMasterVO();
//			session.setAttribute("orderMaster", orderMaster);
//		}
////		model.addAttribute("orderMaster", orderMaster);
//		return "public/order/order_page"; 
//	}
	
	// insert
	
	
	
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
	
	@GetMapping("handleMethodEx")
	public String handleMethodNotAllowedEx() {
		// 找桌位
		return "redirect:/";
	}
	
}