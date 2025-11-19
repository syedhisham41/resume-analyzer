package com.resumeanalyzer.auth.exceptions;

public class UnauthorizedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

}
