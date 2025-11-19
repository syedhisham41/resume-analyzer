package com.resumeanalyzer.ui.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.ui.dto.AnalyzeDashboardPageDataDTO;
import com.resumeanalyzer.ui.service.AnalyzePageService;

@RestController
public class AnalyzeDashboardController {

	private final AnalyzePageService analyzeService;

	public AnalyzeDashboardController(AnalyzePageService analyzeService) {
		super();
		this.analyzeService = analyzeService;
	}

	@GetMapping("/api/analyzepage/data")
	public AnalyzeDashboardPageDataDTO getAnalyzeViewPageData(@AuthenticationPrincipal JwtUserDetails user) {
		return analyzeService.getAnalyzeDashboardPageData(user);
	}

	@PostMapping("/api/analyzepage/analyze")
	public AnalyzeResponseDTO runAnalysis(@RequestParam long jdId, @RequestParam long resumeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return analyzeService.runAnalysis(jdId, resumeId, user);
	}

	@GetMapping("/api/analyzepage/analysis")
	public AnalyzeResponseDTO getAnalyzeRecordWithAnalyzeId(@RequestParam long analyzeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return analyzeService.getAnalysisByAnalyzeId(analyzeId, user);
	}
}
