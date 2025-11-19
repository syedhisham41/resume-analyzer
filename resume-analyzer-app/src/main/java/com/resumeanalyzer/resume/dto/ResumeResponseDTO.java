package com.resumeanalyzer.resume.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ResumeResponseDTO {

	private long resumeId;

	private String content;

	private String title;

	@JsonFormat(pattern = "dd MMM yyyy")
	private LocalDateTime createdAt;

	private int userId;

	public ResumeResponseDTO() {
	}

	public ResumeResponseDTO(long resumeId, String content, String title, LocalDateTime createdAt, int userId) {
		super();
		this.resumeId = resumeId;
		this.content = content;
		this.title = title;
		this.createdAt = createdAt;
		this.userId = userId;
	}

	public long getResumeId() {
		return resumeId;
	}

	public void setResumeId(long resumeId) {
		this.resumeId = resumeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
