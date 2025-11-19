package com.resumeanalyzer.resume.dto;

public class UploadResumeTextRequest {

	private String content;

	public UploadResumeTextRequest() {
	}

	public UploadResumeTextRequest(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
