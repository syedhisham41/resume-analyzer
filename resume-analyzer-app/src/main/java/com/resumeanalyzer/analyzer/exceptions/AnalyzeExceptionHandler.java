package com.resumeanalyzer.analyzer.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.resumeanalyzer.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AnalyzeExceptionHandler {

	@ExceptionHandler(AnalyzeEngineTimeOutException.class)
	public ResponseEntity<ErrorResponse> handleAnalyzeEngineTimeOut(HttpServletRequest http,
			AnalyzeEngineTimeOutException ex) {

		ErrorResponse error = new ErrorResponse(HttpStatus.GATEWAY_TIMEOUT.value(), "Analyze Engine timeout",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(AnalyzeEngineException.class)
	public ResponseEntity<ErrorResponse> handleAnalyzeEngineException(HttpServletRequest http,
			AnalyzeEngineException ex) {

		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Analyze Engine Server Error",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(AnalyzeNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleAnalyzeNotFoundException(HttpServletRequest http,
			AnalyzeNotFoundException ex) {

		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "No analysis done so far",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(HttpServletRequest http,
			HttpRequestMethodNotSupportedException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Invalid HTTP request", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleInvalidArguments(HttpServletRequest http,
			MethodArgumentNotValidException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid arguments", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	} 
}
