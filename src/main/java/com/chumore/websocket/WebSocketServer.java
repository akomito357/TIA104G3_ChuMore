package com.chumore.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.web.bind.annotation.PathVariable;

@ServerEndpoint("/ws/notify/{restId}")
public class WebSocketServer {

	public static final ConcurrentHashMap<String, CopyOnWriteArraySet<Session>> restSessions = new ConcurrentHashMap<>();
	// 存個別餐廳的連線們
	
	@OnOpen
	public void onOpen(Session session, @PathParam("restId") String restId) {
		if (!restSessions.containsKey(restId)) {
			restSessions.put(restId, new CopyOnWriteArraySet<>());
		}
		restSessions.get(restId).add(session);
		System.out.println("add session for " + restId);
	
	}
	
	@OnMessage
	public void onMessage() {
		
	}
	
	@OnClose
	public void onClose(Session session, @PathParam("restId") String restId) {
		CopyOnWriteArraySet<Session> sessions = restSessions.get(restId); // 拿到該餐廳的連線
		if (sessions != null) {
			sessions.remove(session);
			if (sessions.isEmpty()) {
				restSessions.remove(restId);
			}
		}
	}
	
	@OnError
	public void onError() {
		System.out.println("WebSocket error!");
	}
	
	public static void notifyToRest(@PathParam("restId") String restId, String message) {
		CopyOnWriteArraySet<Session> sessions = restSessions.get(restId);
		
		if (sessions != null) {
			for(Session session : sessions) {
				if (session.isOpen()) {
					session.getAsyncRemote().sendText(message);
				} else {
					System.out.println("session is not open, can't send message");
				}
			}
		}
		
	}
}
