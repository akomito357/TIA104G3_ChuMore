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
import com.chumore.orderlineitem.dto.ShowOrderLineItemDto;
import com.chumore.orderlineitem.dto.ShowOrderLineItemDto.LineItemDto;
import com.chumore.orderlineitem.dto.ShowOrderLineItemDto.OrderItemListDto;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.orderlineitem.model.OrderLineItem_Service;
import com.chumore.ordermaster.model.OrderMasterServiceImpl;
import com.chumore.ordermaster.model.OrderMasterVO;
import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;

@RestController
@RequestMapping("/member/orderLineItem")
public class MemberOrderLineItemController {

	@Autowired
	HttpSession session;
	
	@Autowired
	OrderMasterServiceImpl orderSvc;
	
	
	@Autowired
	OrderItem_Service orderItemSvc;
	
	@Autowired
	OrderLineItem_Service orderLineItemSvc;
	
	@Autowired
	Product_Service productSvc;
	
	@GetMapping("items")
	public ResponseEntity<ShowOrderLineItemDto> showOrderItemList(@RequestParam Integer orderId){
		ShowOrderLineItemDto showOrderLineItemDto = new ShowOrderLineItemDto();
		
		OrderMasterVO orderMaster = orderSvc.getOneById(orderId);
		showOrderLineItemDto.setSubtotalPrice(orderMaster.getSubtotalPrice());
		showOrderLineItemDto.setPointUsed(orderMaster.getPointUsed());
		showOrderLineItemDto.setTotalPrice(orderMaster.getTotalPrice());
		
		List<OrderItemListDto> orderItemListDto = new ArrayList<OrderItemListDto>();
		showOrderLineItemDto.setOrderItemListDto(orderItemListDto);
		
		List<OrderItemVO> list = orderItemSvc.getOrderItemListByOrderId(orderId);
		for(OrderItemVO orderItem : list) {
			OrderItemListDto orderItemList = new OrderItemListDto(orderItem);
			List<LineItemDto> lineItemList = new ArrayList<LineItemDto>();
			orderItemList.setLineItemList(lineItemList);
			
			List<OrderLineItemVO> orderLineItemList = orderItem.getOrderLineItem();
			for(OrderLineItemVO orderLineItem : orderLineItemList) {
				LineItemDto lineItemDto = new LineItemDto();
				
//				ProductVO product = productSvc.getProductById(orderLineItem.getProductId());
				ProductVO product = orderLineItem.getProduct();
				lineItemDto.setProductName(product.getProductName());
				lineItemDto.setQuantity(orderLineItem.getQuantity());
				lineItemDto.setPrice(orderLineItem.getPrice());
				
				lineItemList.add(lineItemDto);
			}
			orderItemListDto.add(orderItemList);
			
		}
		
		return ResponseEntity.ok(showOrderLineItemDto);
	}
	
	
	
	
}
