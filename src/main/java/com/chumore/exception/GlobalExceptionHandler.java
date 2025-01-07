package com.chumore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),"Not Found", ex.getMessage());

        // 回傳 JSON 格式錯誤訊息
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleBookingConflict(BookingConflictException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),"Conflict", e.getMessage());
        return ResponseEntity.status(409).body(errorResponse);
    }
    
    
    
    
    
    
    
    
    @ExceptionHandler(OrderDataMismatchException.class)
    public ResponseEntity<ErrorResponse> handleOrderDataMismatch(OrderDataMismatchException e){
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable entity", e.getMessage());
    	return ResponseEntity.status(422).body(errorResponse);
    }

}
