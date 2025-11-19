package com.resumeanalyzer.analyzer.exceptions;

public class AnalyzeNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AnalyzeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
