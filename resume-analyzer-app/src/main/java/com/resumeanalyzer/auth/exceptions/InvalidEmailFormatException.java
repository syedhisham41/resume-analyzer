package com.resumeanalyzer.auth.exceptions;

public class InvalidEmailFormatException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidEmailFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
