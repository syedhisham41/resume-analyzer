package com.resumeanalyzer.auth.exceptions;

public class InvalidPasswordException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

}
