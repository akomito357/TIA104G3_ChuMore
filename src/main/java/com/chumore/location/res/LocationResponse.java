package com.chumore.location.res;

public class LocationResponse<T> {
	private int resCode;
	private String resMsg;
	private T data;
	
	public LocationResponse() {
		super();
	}
	
	public LocationResponse(int resCode, String resMsg, T data) {
		super();
		this.resCode = resCode;
		this.resMsg = resMsg;
		this.data = data;
	}

	public int getResCode() {
		return resCode;
	}
	public void setResCode(int resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
