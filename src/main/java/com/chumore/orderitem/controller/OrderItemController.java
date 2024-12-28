package com.chumore.orderitem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.orderitem.res.OrderItemResponse;

//@Controller
@RestController
@RequestMapping("/rest/orderitem")
public class OrderItemController {

	@Autowired
	OrderItem_Service orderItemSvc;

	@GetMapping("addOrderItem")
	public ResponseEntity<Map<String, String>> addOrderItem() {
		Map<String, String> response = new HashMap<>();
		response.put("message", "Add order item endpoint is ready");
		return ResponseEntity.ok(response);
	}

	@PostMapping("insertOrderItem")
	public ResponseEntity<OrderItemResponse> insert(@RequestBody OrderItemVO orderItem) {
		OrderItemVO vo = null;
		try {
			vo = orderItemSvc.addOrUpdateOrderItem(orderItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		OrderItemResponse<OrderItemVO> response = new OrderItemResponse<OrderItemVO>();
		response.setData(vo);
		response.setCode(200);
		response.setMsg("Success");
		return ResponseEntity.ok(response);
	}
	

	
	@PostMapping("deleteOrderItem")
	public ResponseEntity<OrderItemResponse> delete(@RequestBody Map<String, Integer> request){
		Integer orderItemId = request.get("orderItemId");
		Integer vo = orderItemSvc.deleteOrderItem(orderItemId);
		OrderItemResponse<Integer> response = new OrderItemResponse<Integer>();
		response.setData(vo);
		response.setCode(200);
		response.setMsg("Success");
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("findByOrderItemId")
	public ResponseEntity<OrderItemResponse> findByOrderItemId(@RequestBody Map<String, Integer> request){
		Integer orderItemId = request.get("orderItemId");
		OrderItemVO vo = orderItemSvc.getOrderItemListByOrderItemId(orderItemId);
		OrderItemResponse<OrderItemVO> response = new OrderItemResponse<OrderItemVO>();
		response.setData(vo);
		response.setCode(200);
		response.setMsg("Success");
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("findByOrderId")
	public ResponseEntity<OrderItemResponse> findByOrderId(@RequestBody Map<String, Integer> request){
		Integer orderId = request.get("orderId");
		List<OrderItemVO> vo = orderItemSvc.getOrderItemListByOrderId(orderId);
		OrderItemResponse<List<OrderItemVO>> response = new OrderItemResponse<List<OrderItemVO>>();
		response.setData(vo);
		response.setCode(200);
		response.setMsg("success");
		return ResponseEntity.ok(response);
	}
	
	
	
	@GetMapping("getorderidlist")
	public ResponseEntity<OrderItemResponse> getOrderIdList(){
		List<Integer> vo = orderItemSvc.findAllDistinctOrderIds();
		OrderItemResponse<List<Integer>> response = new OrderItemResponse<List<Integer>>();
		response.setData(vo);
		return ResponseEntity.ok(response);
	}
	
	
}
