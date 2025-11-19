package com.resumeanalyzer.auth.exceptions;

public class UserNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
