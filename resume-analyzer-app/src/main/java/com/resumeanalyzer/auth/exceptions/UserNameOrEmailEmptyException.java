package com.resumeanalyzer.auth.exceptions;

public class UserNameOrEmailEmptyException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserNameOrEmailEmptyException(String message, Throwable cause) {
		super(message, cause);
	}

}
