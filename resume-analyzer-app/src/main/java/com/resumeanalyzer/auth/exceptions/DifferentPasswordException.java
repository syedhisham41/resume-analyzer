package com.resumeanalyzer.auth.exceptions;

public class DifferentPasswordException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DifferentPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

}
