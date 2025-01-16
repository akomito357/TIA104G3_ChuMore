package com.chumore.exception;

public class PointsOverUsedException extends RuntimeException{

	public PointsOverUsedException() {
	}

	public PointsOverUsedException(String message) {
		super(message);
	}
	
}
