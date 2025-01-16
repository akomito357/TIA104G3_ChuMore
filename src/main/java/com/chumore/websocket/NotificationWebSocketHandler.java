package com.chumore.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.server.PathParam;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler{
	
	/*
	 * 相當於javax的WebSocketServer 
	 */

	public static final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> restSessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 對應到OnOpen
		String uri = session.getUri().toString();
		String restId = extractRestId(uri);
		
		System.out.println("uri = " + uri);
		System.out.println("restId = " + restId);

		if (!restSessions.containsKey(restId)) {
			restSessions.put(restId, new CopyOnWriteArraySet<>());
			System.out.println("create key-value for " + restId);
		}
		restSessions.get(restId).add(session);
		System.out.println("add session for " + restId);
	}


	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TODO Auto-generated method stub
		super.handleTextMessage(session, message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
		System.out.println("WebSocket error: " + e.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String uri = session.getUri().toString();
		String restId = extractRestId(uri);
		
		CopyOnWriteArraySet<WebSocketSession> sessions = restSessions.get(restId); // 拿到該餐廳的連線
		if (sessions != null) {
			sessions.remove(session);
			if (sessions.isEmpty()) {
				restSessions.remove(restId);
			}
		}
	}
	
	private String extractRestId(String uri) {
		String[] uriArr = uri.split("/");
		// 取得陣列中的最後一個元素 
		return uriArr[uriArr.length - 1];
	}
	
	public static void notifyToRest(@PathParam("restId") String restId, String message) throws IOException {
		CopyOnWriteArraySet<WebSocketSession> sessions = restSessions.get(restId);
		System.out.println("notifyToRest");
		
		if (sessions != null) {
			for(WebSocketSession session : sessions) {
				if (session.isOpen()) {
					session.sendMessage(new TextMessage(message));
					System.out.println("the message has send");
				} else {
					System.out.println("session is not open, can't send message");
				}
			}
		}
		
	}
	
}
