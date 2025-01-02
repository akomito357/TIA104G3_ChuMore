package com.chumore.orderlineitem.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.orderlineitem.dto.OrderLineItemDto;
import com.chumore.orderlineitem.dto.OrderLineItemDto.LineItemDto;
import com.chumore.orderlineitem.dto.OrderLineItemDto.OrderItemListDto;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.orderlineitem.model.OrderLineItem_Service;
import com.chumore.ordermaster.model.OrderMasterService;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;

@RestController
@RequestMapping("member/orderLineItem")
public class MemberOrderLineItemController {

	@Autowired
	HttpSession session;
	
	@Autowired
	OrderMasterService orderSvc;
	
	
	@Autowired
	OrderItem_Service orderItemSvc;
	
	@Autowired
	OrderLineItem_Service orderLineItemSvc;
	
	@Autowired
	Product_Service productSvc;
	
	@GetMapping("showOrderItemList")
	public ResponseEntity<OrderLineItemDto> showOrderItemList(@RequestParam Integer orderId){
		OrderLineItemDto orderLineItemDto = new OrderLineItemDto();
		
		OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
		orderLineItemDto.setSubtotalPrice(orderMaster.getSubtotalPrice());
		orderLineItemDto.setPointUsed(orderMaster.getPointUsed());
		orderLineItemDto.setTotalPrice(orderMaster.getTotalPrice());
		
		List<OrderItemListDto> orderItemListDto = new ArrayList<OrderItemListDto>();
		orderLineItemDto.setOrderItemListDto(orderItemListDto);
		
		List<OrderItemVO> list = orderItemSvc.getOrderItemListByOrderId(orderId);
		for(OrderItemVO orderItem : list) {
			OrderItemListDto orderItemList = new OrderItemListDto(orderItem);
			List<LineItemDto> lineItemList = new ArrayList<LineItemDto>();
			orderItemList.setLineItemList(lineItemList);
			
			List<OrderLineItemVO> orderLineItemList = orderItem.getOrderLineItem();
			for(OrderLineItemVO orderLineItem : orderLineItemList) {
				LineItemDto lineItemDto = new LineItemDto();
				
				ProductVO product = productSvc.getProductById(orderLineItem.getProductId());
				lineItemDto.setProductName(product.getProductName());
				lineItemDto.setQuantity(orderLineItem.getQuantity());
				lineItemDto.setPrice(orderLineItem.getPrice());
				
				lineItemList.add(lineItemDto);
			}
			orderItemListDto.add(orderItemList);
			
		}
		
		return ResponseEntity.ok(orderLineItemDto);
	}
	
	
//	public List<LineItemDto> lineResult(List<OrderLineItemVO> list){
//		List<LineItemDto> lineResult = new ArrayList<LineItemDto>();
//		for(OrderLineItemVO data: list) {
//			//productVo
//			LineItemDto lineItem = new LineItemDto(data, product);
//			lineResult.add(lineItem);
//		}
//		return lineResult;
//		
//	}
	
//	public List<OrderItemListDto> itemList(List<OrderItemVO> list){
//		List<OrderItemListDto> itemList = new ArrayList<OrderItemListDto>();
//		for(OrderItemVO data : list) {
//			OrderItemListDto dto = new OrderItemListDto(data);
//			itemList.add(dto);
//		}
//		
//		return itemList;
//	}
	
	
	
//	public List<OrderLineItemDto> orderMainList(List<OrderMasterVO> list) {
//
//		List<OrderLineItemDto> orderMainList = new ArrayList<OrderLineItemDto>();
//
//		if (list != null) {
//			for (OrderMasterVO data : list) {
//				OrderLineItemDto dto = new OrderLineItemDto(data);
//				orderMainList.add(dto);
//			}
//		}
//
//		return orderMainList;
//	}
	
	
	
	
	
	
	
}
