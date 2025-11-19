package com.resumeanalyzer.jd.dto;

public class JdParsedDTO {

	private String title;

	private String companyName;

	private String content;

	public JdParsedDTO() {
	}

	public JdParsedDTO(String title, String companyName, String content) {
		super();
		this.title = title;
		this.companyName = companyName;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
