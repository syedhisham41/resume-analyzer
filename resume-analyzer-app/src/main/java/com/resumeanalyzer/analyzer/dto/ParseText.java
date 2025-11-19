package com.resumeanalyzer.analyzer.dto;

import jakarta.validation.constraints.NotBlank;

public class ParseText {

	@NotBlank
	private String text;

	public ParseText(@NotBlank String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
