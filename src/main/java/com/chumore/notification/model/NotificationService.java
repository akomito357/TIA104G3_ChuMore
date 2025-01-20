package com.chumore.notification.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.notification.NotificationMessage;
import com.chumore.websocket.NotificationWebSocketHandler;
import com.google.gson.Gson;

@Service
public class NotificationService {
	
	@Autowired
	NotificationRepository notiRepository;
	
	@Autowired
	Gson gson;
	
	@Autowired
	private final NotificationWebSocketHandler handler;
	
	public NotificationService(NotificationRepository notiRepository, Gson gson, NotificationWebSocketHandler handler) {
		this.notiRepository = notiRepository;
		this.gson = gson;
		this.handler = handler;
	}
	
	
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	
	public String pressServiceBell(String restId, String caller) {
		String type = "service_bell";
		String content = "桌位〈" + caller + "〉按了服務鈴";
		String message = saveMessage(restId, caller, type, content);
		return message;
	}
	
	public String requestCheckout(String restId, String caller) {
		String type = "checkout";
		String content = "桌位〈" + caller + "〉預備結帳";
		String message = saveMessage(restId, caller, type, content);
		return message;
	}
	
	public String confirmReservation(String restId, String caller) {
		String type = "reservation";
		String content = "已收到〈" + caller + "〉的訂位";
		String message = saveMessage(restId, caller, type, content);
		return message;
	}
	
	@Transactional
	public String saveMessage(String restId, String caller, String type, String content) {
		String currentTime = LocalTime.now().format(TIME_FORMATTER);
		String currentDateTime = LocalDateTime.now().format(DATETIME_FORMATTER);
		
		NotificationMessage message = new NotificationMessage(caller, type, content, currentDateTime);
		String messageJson = gson.toJson(message);
		System.out.println(messageJson);
		
		notiRepository.saveNotifications(restId, caller, messageJson, type);
		return messageJson;
	}
	
	
	// 查詢通知
	public Map<String, String> getRestNotifications(String restId, String type){
		return notiRepository.getRestNotifications(restId, type);
	}
	
	public void notifyToRest(String restId, String message) {
		try {
			handler.notifyToRest(restId, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
