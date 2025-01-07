package com.chumore.exception;

public class DataMismatchException extends RuntimeException {
	public DataMismatchException() {
		
	}
	
	public DataMismatchException(String message) {
		super(message);
	}
}
