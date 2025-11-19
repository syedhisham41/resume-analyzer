package com.resumeanalyzer.jd.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.resumeanalyzer.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class JdExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(HttpServletRequest http, AccessDeniedException ex) {

		ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleResumeGetInputTypeMismatch(HttpServletRequest http,
			MethodArgumentTypeMismatchException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Input", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);

	}
	
	@ExceptionHandler(JdNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleJdNotFoundEntity(HttpServletRequest http,
			JdNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "JD Not Found", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

	}

}
