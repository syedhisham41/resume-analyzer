package com.resumeanalyzer.auth.exceptions;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.resumeanalyzer.auth.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AuthExceptionHandler {

	@ExceptionHandler(InvalidEmailFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidEmailFormat(HttpServletRequest http,
			InvalidEmailFormatException ex) {

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Email Format", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(HttpServletRequest http,
			DataIntegrityViolationException ex) {

		if (ex.getMessage().contains("user_username"))
			return handleDuplicateUserName(http, ex);
		return handleDuplicateEmail(http, ex);
	}

	public ResponseEntity<ErrorResponse> handleDuplicateUserName(HttpServletRequest http,
			DataIntegrityViolationException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Duplicate UserName", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	public ResponseEntity<ErrorResponse> handleDuplicateEmail(HttpServletRequest http,
			DataIntegrityViolationException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Duplicate Email", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPassword(HttpServletRequest http, InvalidPasswordException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid Password", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(HttpServletRequest http, UserNotFoundException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found", ex.getMessage(),
				LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(UserNameOrEmailEmptyException.class)
	public ResponseEntity<ErrorResponse> handleEmptyUserNameorEmail(HttpServletRequest http,
			UserNameOrEmailEmptyException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Empty Username or Email",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}
	
	@ExceptionHandler(DifferentPasswordException.class)
	public ResponseEntity<ErrorResponse> handleDifferentPasswordDuringChange(HttpServletRequest http, DifferentPasswordException ex){
		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "New Password and Confirm password are different",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnAuthorizedAccess(HttpServletRequest http, UnauthorizedException ex){
		ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Unauthorized Access",
				ex.getMessage(), LocalDateTime.now(), http.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

}
