package com.resumeanalyzer.analyzer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class ParseAnalyzeText {

	@NotBlank(message = "required")
	@JsonProperty("jd_text")
	private String jd_text;

	@NotBlank
	@JsonProperty("resume_text")
	private String resume_text;

	public ParseAnalyzeText() {}
	
	public ParseAnalyzeText(@NotBlank String jd_text, @NotBlank String resume_text) {
		super();
		this.jd_text = jd_text;
		this.resume_text = resume_text;
	}

	public String getJd_text() {
		return jd_text;
	}

	public void setJd_text(String jd_text) {
		this.jd_text = jd_text;
	}

	public String getResume_text() {
		return resume_text;
	}

	public void setResume_text(String resume_text) {
		this.resume_text = resume_text;
	}

}
