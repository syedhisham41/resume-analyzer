package com.resumeanalyzer.ui.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.service.AnalyzerService;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.ui.dto.AnalyzeDashboardPageDataDTO;

@Service
public class AnalyzePageService {

	private final AnalyzerService analyzeService;

	private final DashboardService dashboardService;

	public AnalyzePageService(AnalyzerService analyzeService, DashboardService dashboardService) {
		super();
		this.analyzeService = analyzeService;
		this.dashboardService = dashboardService;
	}

	public List<AnalyzeResponseDTO> getAllAnalyzePerUser(JwtUserDetails user) {
		return analyzeService.getAllAnalyzesPerUser(user);
	}

	public List<AnalyzeResponseDTO> getLast5Analyzes(JwtUserDetails user) {
		return analyzeService.getLatest5AnalyzesPerUser(user);
	}

	public AnalyzeDashboardPageDataDTO getAnalyzeDashboardPageData(JwtUserDetails user) {

		return new AnalyzeDashboardPageDataDTO(dashboardService.getTotalAnalyzeCount(user),
				dashboardService.getLastAnalyzeResult(user).getCreatedAt(), getLast5Analyzes(user),
				getAllAnalyzePerUser(user));
	}

	public AnalyzeResponseDTO runAnalysis(long jdId, long resumeId, JwtUserDetails user) {
		return analyzeService.analyze(jdId, resumeId, user);
	}

	public AnalyzeResponseDTO getAnalysisByAnalyzeId(long analyzeId, JwtUserDetails user) {
		return analyzeService.getAnalysisByAnalyzeId(analyzeId, user);
	}

}
