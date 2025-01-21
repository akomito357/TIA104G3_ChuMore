package com.chumore.notification.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.notification.model.NotificationRequest;
import com.chumore.notification.model.NotificationService;
import com.chumore.util.ResponseUtil;
import com.chumore.websocket.NotificationWebSocketHandler;

@RestController
@RequestMapping("/notification")
public class NotificationController {
	
	@Autowired
	private final NotificationService notiService;
	
	@Autowired
	private final NotificationWebSocketHandler handler;
	
	
	public NotificationController(NotificationService notiService, NotificationWebSocketHandler handler) {
		this.notiService = notiService;
		this.handler = handler;
	}
	
	@PostMapping("serviceBell")
	public String pressServiceBell(@RequestBody NotificationRequest bellReq) {
		// 1. 將通知資料存到redis
		String restId = bellReq.getRestId();
		String caller = bellReq.getCaller();
//		LocalTime time = LocalTime.now();

		System.out.println("caller:" + caller);
		String message = notiService.pressServiceBell(restId, caller);
		
		// 2. 用websocket推送通知
//		try {
//			handler.notifyToRest(restId, message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		notiService.notifyToRest(restId, message);
		
		return "notification from " + caller + " has send to " + restId;
	}
	
	@PostMapping("requestCheckout")
	public String requestCheckout(@RequestBody NotificationRequest notiReq) {
		String restId = notiReq.getRestId();
		String caller = notiReq.getCaller();
		String message = notiService.requestCheckout(restId, caller);
		
		
		notiService.notifyToRest(restId, message);
//		try {
//			handler.notifyToRest(restId, message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return "requestCheckout from " + caller + " has send to " + restId;
		
	}
	
	@PostMapping("confirmReservation")
	public String confirmReservation(@RequestParam String restId, @RequestParam String caller) {
//		String restId = notiReq.get("restId");
//		String caller = notiReq.get("caller");
		String message = notiService.requestCheckout(restId, caller);
		
		notiService.notifyToRest(restId, message);
		
//		try {
//			handler.notifyToRest(restId, message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return "requestCheckout from " + caller + " has send to " + restId;
		
	}
	
	
	// 要有一個方法可以從Redis得到某間餐廳的所有通知
	@GetMapping("/{restId}/{type}")
	public ResponseEntity<?> getRestNotification(@PathVariable("restId") String restId, @PathVariable("type") String type) {
		Map<String, String> restNotifications = notiService.getRestNotifications(restId, type);
				
		ResponseUtil res = new ResponseUtil("success", 200, restNotifications);
		return ResponseEntity.ok(res);
	}
	
}
