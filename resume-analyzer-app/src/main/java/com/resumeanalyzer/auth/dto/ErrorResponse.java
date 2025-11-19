package com.resumeanalyzer.auth.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
	
	private int errorStatus;
	
	private String error;
	
	private String errormessage;
	
	private LocalDateTime timestamp;
	
	private String path;
	
	
	public ErrorResponse() {}


	public ErrorResponse(int errorStatus, String error, String errormessage, LocalDateTime timestamp, String path) {
		super();
		this.errorStatus = errorStatus;
		this.error = error;
		this.errormessage = errormessage;
		this.timestamp = timestamp;
		this.path = path;
	}


	public int getErrorStatus() {
		return errorStatus;
	}


	public void setErrorStatus(int errorStatus) {
		this.errorStatus = errorStatus;
	}


	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}


	public String getErrormessage() {
		return errormessage;
	}


	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
	
}
