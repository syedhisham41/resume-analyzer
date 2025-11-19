package com.resumeanalyzer.jd.exceptions;

public class JdNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JdNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
