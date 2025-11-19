package com.resumeanalyzer.common.utils;

import org.springframework.stereotype.Component;

import com.resumeanalyzer.analyzer.client.MlClientService;
import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.dto.ParseAllResponse;
import com.resumeanalyzer.analyzer.dto.ParseAnalyzeText;
import com.resumeanalyzer.analyzer.dto.ParseText;

@Component
public class AIUtils {

	private final MlClientService mlService;

	public AIUtils(MlClientService service) {
		this.mlService = service;
	}

	public ParseAllResponse parseRawContent(ParseText content) {
		return mlService.parseRawText(content).block().getBody();

	}

	public AnalyzeResponseDTO runAnalyzeEngine(ParseAnalyzeText content) {
		return mlService.runAnalyzeEngine(content).block().getBody();
	}

}
