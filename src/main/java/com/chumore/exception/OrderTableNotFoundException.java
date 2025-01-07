package com.chumore.exception;

public class OrderTableNotFoundException extends RuntimeException{

	public OrderTableNotFoundException() {
		
	}
	
	public OrderTableNotFoundException(String message) {
		super(message);
	}
	
}
