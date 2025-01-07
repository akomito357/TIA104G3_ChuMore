package com.chumore.exception;

public class OrderDataMismatchException extends RuntimeException {
	public OrderDataMismatchException() {
		
	}
	
	public OrderDataMismatchException(String message) {
		super(message);
	}
}
