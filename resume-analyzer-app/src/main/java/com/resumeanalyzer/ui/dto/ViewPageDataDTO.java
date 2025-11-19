package com.resumeanalyzer.ui.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ViewPageDataDTO {

	private int totalCount;

	@JsonFormat(pattern = "dd MMM yyyy")
	private LocalDateTime lastUploadedDate;

	private Object lastUploaded;

	private List<?> recent5Uploads;

	private List<?> allItems;

	public ViewPageDataDTO() {
	}

	public ViewPageDataDTO(int totalCount, LocalDateTime lastUploadedDate, Object lastUploaded, List<?> recent5Uploads,
			List<?> allItems) {
		super();
		this.totalCount = totalCount;
		this.lastUploadedDate = lastUploadedDate;
		this.lastUploaded = lastUploaded;
		this.recent5Uploads = recent5Uploads;
		this.allItems = allItems;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public LocalDateTime getLastUploadedDate() {
		return lastUploadedDate;
	}

	public void setLastUploadedDate(LocalDateTime lastUploadedDate) {
		this.lastUploadedDate = lastUploadedDate;
	}

	public Object getLastUploaded() {
		return lastUploaded;
	}

	public void setLastUploaded(Object lastUploaded) {
		this.lastUploaded = lastUploaded;
	}

	public List<?> getRecent5Uploads() {
		return recent5Uploads;
	}

	public void setRecent5Uploads(List<?> recent5Uploads) {
		this.recent5Uploads = recent5Uploads;
	}

	public List<?> getAllItems() {
		return allItems;
	}

	public void setAllItems(List<?> allItems) {
		this.allItems = allItems;
	}
}