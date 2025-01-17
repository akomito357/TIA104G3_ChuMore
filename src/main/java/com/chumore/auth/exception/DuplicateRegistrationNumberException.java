package com.chumore.auth.exception;

public class DuplicateRegistrationNumberException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public DuplicateRegistrationNumberException(String message) {
        super(message);
    }

    public DuplicateRegistrationNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}