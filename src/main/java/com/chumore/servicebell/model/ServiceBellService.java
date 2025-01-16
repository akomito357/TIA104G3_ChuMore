package com.chumore.servicebell.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceBellService {
	
	@Autowired
	ServiceBellRepository bellRepository;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	// 按服務鈴後儲存通知
	public String pressServiceBell(String restId, String tableName) {
		String currentTime = LocalTime.now().format(FORMATTER);
		String message = "桌位〈" + tableName + "〉按了服務鈴，時間：" + currentTime;
		bellRepository.saveNotifications(restId, tableName, message);
		
		return message;
	}

	// 查詢通知
	public Map<String, String> getRestNotifications(String restId){
		return bellRepository.getRestNotifications(restId);
	}
	
	
}
