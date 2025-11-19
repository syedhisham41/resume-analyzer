package com.resumeanalyzer.jd.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class UploadJdTextRequest {

	private List<String> title;

	private List<String> companyName;

	@NotBlank(message = "JD content must not be blank")
	private String content;

	public UploadJdTextRequest() {
	}

	public UploadJdTextRequest(List<String> title, List<String> companyName,
			@NotBlank(message = "JD content must not be blank") String content) {
		super();
		this.title = title;
		this.companyName = companyName;
		this.content = content;
	}

	public List<String> getTitle() {
		return title;
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public List<String> getCompanyName() {
		return companyName;
	}

	public void setCompanyName(List<String> companyName) {
		this.companyName = companyName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
