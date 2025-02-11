package com.chumore.productcategory.res;

public class ProductCategoryResponse<T> {
	private String msg;

	private int code;

	private T data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ProductCategoryResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductCategoryResponse(String msg, int code, T data) {
		super();
		this.msg = msg;
		this.code = code;
		this.data = data;
	}
	
}
