package com.resumeanalyzer.analyzer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.service.AnalyzerService;
import com.resumeanalyzer.common.dto.JwtUserDetails;

@RestController
public class AnalyzeController {

	private AnalyzerService analyzeService;

	public AnalyzeController(AnalyzerService analyzeService) {
		super();
		this.analyzeService = analyzeService;
	}

	@PostMapping("/api/analyze")
	public ResponseEntity<AnalyzeResponseDTO> analyze(@RequestParam long jdId, @RequestParam long resumeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(analyzeService.analyze(jdId, resumeId, user));

	}

	@DeleteMapping("/api/analyze/delete")
	public ResponseEntity<String> deleteAnalyzeRecordByAnalyzeId(@RequestParam long analyzeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(analyzeService.deleteAnalysisByAnalyzeId(analyzeId, user));
	}

}
