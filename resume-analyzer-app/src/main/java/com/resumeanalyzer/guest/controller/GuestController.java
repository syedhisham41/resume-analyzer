package com.resumeanalyzer.guest.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.analyzer.dto.ParseAnalyzeText;
import com.resumeanalyzer.auth.dto.Jwt;
import com.resumeanalyzer.auth.service.AuthService;
import com.resumeanalyzer.common.dto.GuestResponseDTO;
import com.resumeanalyzer.common.dto.GuestUserDetails;
import com.resumeanalyzer.guest.service.GuestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

	private final GuestService guestService;

	private final AuthService authService;

	public GuestController(GuestService guestService, AuthService authService) {
		super();
		this.guestService = guestService;
		this.authService = authService;
	}

	@PostMapping("/login")
	public Jwt Login() {
		return authService.guestLogin();

	}

	@PostMapping("/analyze")
	public GuestResponseDTO analyze(@Valid @RequestBody ParseAnalyzeText content,
			@AuthenticationPrincipal GuestUserDetails details) {
		return guestService.analyze(content, details);
	}
}
