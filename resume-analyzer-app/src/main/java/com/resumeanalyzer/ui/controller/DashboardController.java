package com.resumeanalyzer.ui.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.activity.entity.Activity;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.ui.dto.DashboardMetricsDTO;
import com.resumeanalyzer.ui.service.DashboardService;

@RestController
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		super();
		this.dashboardService = dashboardService;
	}

	@GetMapping("/api/dashboard/metrics")
	public DashboardMetricsDTO getDashboardMetrics(@AuthenticationPrincipal JwtUserDetails user) {

		return new DashboardMetricsDTO(dashboardService.getTotalResumeCount(user),
				dashboardService.getTotalJdCount(user), dashboardService.getTotalAnalyzeCount(user),
				dashboardService.getAverageOverallFit(user), dashboardService.getUserDetails(user).getName(),
				dashboardService.getUserDetails(user).getUserName(), dashboardService.getUserDetails(user).getEmail(),
				dashboardService.getUserDetails(user).getCurrentCompany(),
				dashboardService.getUserDetails(user).getCurrentRole());
	}

//	@GetMapping("/api/dashboard/latest")
//	public AnalyzeResponseDTO getLatestAnalyzeResult(@AuthenticationPrincipal JwtUserDetails user) {
//		return dashboardService.getLastAnalyzeResult(user);
//	}

	@GetMapping("/api/dashboard/latest")
	public ResponseEntity<?> getLastAnalyzeResult(@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(dashboardService.getLastAnalyzeResult(user));
	}

	@GetMapping("/api/dashboard/recentactivity")
	public ResponseEntity<List<Activity>> getRecentActivity(@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(dashboardService.getLatest10Activity(user));
	}
}
