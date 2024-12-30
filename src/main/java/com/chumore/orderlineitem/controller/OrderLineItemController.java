package com.chumore.orderlineitem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.orderlineitem.model.OrderLineItem_Service;
import com.chumore.orderlineitem.res.OrderLineItemResponse;

@RestController
@RequestMapping("/rest/orderlineitem")
public class OrderLineItemController {

	@Autowired
	OrderLineItem_Service orderLineItemSvc;
	
	@Autowired
	OrderItem_Service orderItemSvc;
	
	@PostMapping("insertOrderLineItem")
	public ResponseEntity<OrderLineItemResponse> insert(@RequestBody OrderLineItemVO orderLineItem){
		OrderLineItemVO vo = orderLineItemSvc.addOrderLineItem(orderLineItem);
		OrderLineItemResponse<OrderLineItemVO> response = new OrderLineItemResponse<OrderLineItemVO>("Success",200,vo);
		return ResponseEntity.ok(response);
	}
	
	
	@PostMapping("updateOrderLineItem")
	public ResponseEntity<OrderLineItemResponse> update(@RequestBody OrderLineItemVO orderLineItem){
		OrderLineItemVO vo = null;
		try {
			vo = orderLineItemSvc.updateOrderLineItem(orderLineItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		OrderLineItemResponse<OrderLineItemVO> response = new OrderLineItemResponse<OrderLineItemVO>("Success",200,vo);
		return ResponseEntity.ok(response);
	}
	
	
	
	@PostMapping("deleteOrderLineItem")
	public ResponseEntity<OrderLineItemResponse> delete(@RequestBody Map<String, Integer> request){
		Integer orderLineItemId = request.get("orderLineItemId");
		Integer vo = orderLineItemSvc.deleteOrderLineItem(orderLineItemId);
		OrderLineItemResponse<Integer> response = new OrderLineItemResponse<Integer>("Success",200,vo);
		return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("findOrderLineItemByOrderItemId")
	public ResponseEntity<OrderLineItemResponse> findOrderLineItemList(@RequestBody Map<String, Integer> request){
		Integer orderItemId = request.get("orderItemId");
		List<OrderLineItemVO> vo = orderLineItemSvc.getOrderLineItemByOrderItemId(orderItemId);
		OrderLineItemResponse<List<OrderLineItemVO>> response = new OrderLineItemResponse<List<OrderLineItemVO>>("Success",200,vo);
	
		return ResponseEntity.ok(response);
		
	}
	
	
		
	
	
}
