package com.chumore.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
    
    @ExceptionHandler(DataMismatchException.class)
    public ResponseEntity<ErrorResponse> handleDataMismatch(DataMismatchException e){
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable entity", e.getMessage());
    	return ResponseEntity.status(422).body(errorResponse);
    }
    
    @ExceptionHandler(OrderTableNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderTableNotFound(OrderTableNotFoundException e, HttpServletResponse res){
    	try {
			res.sendRedirect("/");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.NOT_FOUND.value(), "Unprocessable entity", e.getMessage());
    	return ResponseEntity.status(422).body(errorResponse);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
    		HttpRequestMethodNotSupportedException e, HttpServletRequest req, HttpServletResponse res){
    	ErrorResponse errorResponse = null;
    	String mappingPath = req.getServletPath();
    	
    	try {    		
    		System.out.println(mappingPath.split("/")[1]); // 檢查@RequestMapping路徑是否正確
    		if (mappingPath.split("/")[1].equals("orders")) { // for 點餐系統
    			res.sendRedirect("/orders/handleMethodEx");
    		} else {
//    			res.sendRedirect("/"); // 此行效果為若觸發405錯誤就重導到首頁，為了除錯方便先註解
    		}
    		
    	} catch (IOException ie) {
    		errorResponse = new ErrorResponse(
    				HttpStatus.METHOD_NOT_ALLOWED.value(), "method not allowed", e.getMessage());
    	}
    	return ResponseEntity.status(405).body(errorResponse);
    	
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse res) {
    	try {
			res.sendRedirect("/");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),"Bad Request", e.getMessage());
        return ResponseEntity.status(400).body(errorResponse);
    }
    
    @ExceptionHandler(DiscPtsAbsentException.class)
    public ResponseEntity<ErrorResponse> handleDiscPtsAbsent(DiscPtsAbsentException e){
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.BAD_REQUEST.value(), "Forbidden", e.getMessage());
    	return ResponseEntity.status(400).body(errorResponse);
    }
    
    @ExceptionHandler(PointsInsufficientException.class)
    public ResponseEntity<ErrorResponse> handlePointsInsufficient(PointsInsufficientException e){
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.FORBIDDEN.value(), "Forbidden", e.getMessage());
    	return ResponseEntity.status(403).body(errorResponse);
    }
    
    @ExceptionHandler(PointsOverUsedException.class)
    public ResponseEntity<ErrorResponse> handlePointsOverUsed(PointsOverUsedException e){
    	ErrorResponse errorResponse = new ErrorResponse(
    			HttpStatus.UNPROCESSABLE_ENTITY.value(), "Unprocessable entity", e.getMessage());
    	return ResponseEntity.status(422).body(errorResponse);
    }
    
    // 未登入的錯誤處理 - 導回登入頁
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
//    	try {
//    		String originUri = req.getRequestURI();
//    		session.setAttribute("originUri", originUri);
//			res.sendRedirect("/auth/login");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//    	ErrorResponse errorResponse = new ErrorResponse(
//                HttpStatus.UNAUTHORIZED.value(),"Unauthorized", e.getMessage());
//        return ResponseEntity.status(401).body(errorResponse);
//    }
    



}