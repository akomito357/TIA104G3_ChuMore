package com.chumore.servicebell.controller;

import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chumore.servicebell.model.ServiceBellRequest;
import com.chumore.servicebell.model.ServiceBellService;
import com.chumore.util.ResponseUtil;
import com.chumore.websocket.NotificationWebSocketHandler;

@RestController
@RequestMapping("/serviceBell")
public class ServiceBellController {
	
	@Autowired
	private final ServiceBellService bellService;
	
	@Autowired
	private final NotificationWebSocketHandler handler;
	
	
	
	
	public ServiceBellController(ServiceBellService bellService, NotificationWebSocketHandler handler) {
		this.bellService = bellService;
		this.handler = handler;
	}
	
	@PostMapping("pressBell")
	public String pressServiceBell(@RequestBody ServiceBellRequest bellReq) {
		// 1. 將通知資料存到redis
		String restId = bellReq.getRestId();
		String tableName = bellReq.getTableName();
		LocalTime time = LocalTime.now();

		String message = bellService.pressServiceBell(restId, tableName);
		
		// 2. 用websocket推送通知
		try {
			handler.notifyToRest(restId, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "notification from " + tableName + " has send to " + restId;
	}
	
	// 要有一個方法可以從Redis得到某間餐廳的所有通知
	@GetMapping("/{restId}")
	public ResponseEntity<?> getRestNotification(@PathVariable("restId") String restId) {
		Map<String, String> restNotifications = bellService.getRestNotifications(restId);
				
		ResponseUtil res = new ResponseUtil("success", 200, restNotifications);
		return ResponseEntity.ok(res);
	}
	
}
