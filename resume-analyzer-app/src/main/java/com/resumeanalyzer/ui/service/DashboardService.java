package com.resumeanalyzer.ui.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resumeanalyzer.activity.entity.Activity;
import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.service.AnalyzerService;
import com.resumeanalyzer.auth.dto.UserDetailsDTO;
import com.resumeanalyzer.auth.service.AuthService;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.jd.exceptions.JdNotFoundException;
import com.resumeanalyzer.jd.service.JdService;
import com.resumeanalyzer.resume.exceptions.ResumeNotFoundException;
import com.resumeanalyzer.resume.service.ResumeService;

@Service
public class DashboardService {

	private JdService jdService;

	private ResumeService resumeService;

	private AnalyzerService analyzeService;

	private AuthService authService;

	private ActivityService activityService;

	public DashboardService(JdService jdService, ResumeService resumeService, AnalyzerService analyzeService,
			AuthService authService, ActivityService activityService) {
		super();
		this.jdService = jdService;
		this.resumeService = resumeService;
		this.analyzeService = analyzeService;
		this.authService = authService;
		this.activityService = activityService;
	}

	public int getTotalResumeCount(JwtUserDetails user) {
		try {
			return resumeService.getAllResumebyUser(user).size();
		} catch (ResumeNotFoundException e) {
			return 0;
		}

	}

	public int getTotalJdCount(JwtUserDetails user) {
		try {
			return jdService.getAllJdbyUser(user).size();
		} catch (JdNotFoundException e) {
			return 0;
		}

	}

	public int getTotalAnalyzeCount(JwtUserDetails user) {
		return analyzeService.getAllAnalyzesPerUser(user).size();
	}

	public double getAverageOverallFit(JwtUserDetails user) {
		return analyzeService.getAverageOverallFit(user);
	}

	public UserDetailsDTO getUserDetails(JwtUserDetails user) {
		return authService.getUserDetails(user);
	}

	public AnalyzeResponseDTO getLastAnalyzeResult(JwtUserDetails user) {
		return analyzeService.getLatestAnalyzeResult(user);

	}

	public List<Activity> getLatest10Activity(JwtUserDetails user) {
		return activityService.getLatest10Records(user);

	}

}
