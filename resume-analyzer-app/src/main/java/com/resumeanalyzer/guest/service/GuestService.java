package com.resumeanalyzer.guest.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.dto.ParseAnalyzeText;
import com.resumeanalyzer.auth.exceptions.UnauthorizedException;
import com.resumeanalyzer.common.dto.GuestResponseDTO;
import com.resumeanalyzer.common.dto.GuestUserDetails;
import com.resumeanalyzer.common.utils.AIUtils;

@Service
public class GuestService {

	private final AIUtils utils;

	public GuestService(AIUtils utils) {
		super();
		this.utils = utils;
	}

	public GuestResponseDTO analyze(ParseAnalyzeText analyzeContent, GuestUserDetails details) {

		if (details == null)
			throw new UnauthorizedException("Guest authentication failed", null);

		AnalyzeResponseDTO response = utils.runAnalyzeEngine(analyzeContent);
		List<String> skills = response.getMatchedSkills().keySet().stream().limit(5).toList();
		return new GuestResponseDTO(response.getOverallFit(), skills);
	}
}
