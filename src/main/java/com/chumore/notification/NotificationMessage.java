package com.chumore.notification;

import java.time.LocalTime;

public class NotificationMessage {

	private String caller; // A1
	private String type; // checkout / servicebell / reservation
	private String content; // 
	private String time;
	
	public NotificationMessage() {
	}

	public NotificationMessage(String caller, String type, String content, String time) {
		super();
		this.caller = caller;
		this.type = type;
		this.content = content;
		this.time = time;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
	
}
