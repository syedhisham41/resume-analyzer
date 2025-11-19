package com.resumeanalyzer.ui.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;

public class AnalyzeDashboardPageDataDTO {

	private int totalAnalyzeCount;

	@JsonFormat(pattern = "dd MMM yyyy")
	private LocalDateTime lastAnalyzeDate;

	private List<AnalyzeResponseDTO> recent5Analyzes;

	private List<AnalyzeResponseDTO> allAnalyzes;

	public AnalyzeDashboardPageDataDTO() {
	}

	public AnalyzeDashboardPageDataDTO(int totalAnalyzeCount, LocalDateTime lastAnalyzeDate,
			List<AnalyzeResponseDTO> recent5Analyzes, List<AnalyzeResponseDTO> allAnalyzes) {
		super();
		this.totalAnalyzeCount = totalAnalyzeCount;
		this.lastAnalyzeDate = lastAnalyzeDate;
		this.recent5Analyzes = recent5Analyzes;
		this.allAnalyzes = allAnalyzes;
	}

	public int getTotalAnalyzeCount() {
		return totalAnalyzeCount;
	}

	public void setTotalAnalyzeCount(int totalAnalyzeCount) {
		this.totalAnalyzeCount = totalAnalyzeCount;
	}

	public LocalDateTime getLastAnalyzeDate() {
		return lastAnalyzeDate;
	}

	public void setLastAnalyzeDate(LocalDateTime lastAnalyzeDate) {
		this.lastAnalyzeDate = lastAnalyzeDate;
	}

	public List<AnalyzeResponseDTO> getRecent5Analyzes() {
		return recent5Analyzes;
	}

	public void setRecent5Analyzes(List<AnalyzeResponseDTO> recent5Analyzes) {
		this.recent5Analyzes = recent5Analyzes;
	}

	public List<AnalyzeResponseDTO> getAllAnalyzes() {
		return allAnalyzes;
	}

	public void setAllAnalyzes(List<AnalyzeResponseDTO> allAnalyzes) {
		this.allAnalyzes = allAnalyzes;
	}

}
