package com.resumeanalyzer.ui.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.analyzer.dto.ParseText;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.ui.dto.ViewPageDataDTO;
import com.resumeanalyzer.ui.dto.ViewPageExtractedContentDTO;
import com.resumeanalyzer.ui.service.ViewPageService;

@RestController
public class ViewPageController {

	private final ViewPageService viewPageService;

	public ViewPageController(ViewPageService viewPageService) {
		super();
		this.viewPageService = viewPageService;
	}

	@GetMapping("/api/viewpage/data")
	public ViewPageDataDTO getViewPageData(@RequestParam String type, @AuthenticationPrincipal JwtUserDetails user) {
		return viewPageService.getViewPageData(user, type);
	}

	@PostMapping("/api/viewpage/extractData")
	public ViewPageExtractedContentDTO getViewPageExtractedData(@RequestBody ParseText content,
			@AuthenticationPrincipal JwtUserDetails user) {
		return viewPageService.getExtractedDetails(user, content);
	}
}
