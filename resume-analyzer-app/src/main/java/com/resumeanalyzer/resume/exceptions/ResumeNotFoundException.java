package com.resumeanalyzer.resume.exceptions;

public class ResumeNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ResumeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
