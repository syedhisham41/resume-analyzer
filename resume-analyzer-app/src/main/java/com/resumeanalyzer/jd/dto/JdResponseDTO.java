package com.resumeanalyzer.jd.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JdResponseDTO {

	private long jdId;

	private String title;

	private String companyName;

	private String content;

	private int userId;
	
	@JsonFormat(pattern = "dd MMM yyyy")
	private LocalDateTime createdAt;

	public JdResponseDTO() {
	}

	public JdResponseDTO(long jdId, String title, String companyName, String content, int userId, LocalDateTime createdAt) {
		super();
		this.jdId = jdId;
		this.title = title;
		this.companyName = companyName;
		this.content = content;
		this.userId = userId;
		this.createdAt = createdAt;
	}

	public long getJdId() {
		return jdId;
	}

	public void setJdId(long jdId) {
		this.jdId = jdId;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
