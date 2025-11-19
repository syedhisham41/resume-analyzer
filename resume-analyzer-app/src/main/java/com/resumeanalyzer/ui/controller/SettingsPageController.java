package com.resumeanalyzer.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.auth.dto.PasswordChangeDTO;
import com.resumeanalyzer.auth.dto.UserDetailsDTO;
import com.resumeanalyzer.auth.dto.UserUpdateDetailsDTO;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.ui.service.SettingsPageService;

@RestController
public class SettingsPageController {

	private final SettingsPageService settingsService;

	public SettingsPageController(SettingsPageService settingsService) {
		super();
		this.settingsService = settingsService;
	}

	@GetMapping("/api/settingpage/getdata")
	public UserDetailsDTO getUserData(@AuthenticationPrincipal JwtUserDetails user) {
		return settingsService.getUserData(user);
	}

	@PostMapping("/api/settingpage/updatedata")
	public UserDetailsDTO updateUserData(@RequestBody UserUpdateDetailsDTO userDetails,
			@AuthenticationPrincipal JwtUserDetails user) {
		return settingsService.updateUserData(userDetails, user);

	}

	@DeleteMapping("/api/settingpage/delete")
	public ResponseEntity<?> deleteUser(@AuthenticationPrincipal JwtUserDetails user) {
		settingsService.deleteUser(user);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/api/settingpage/updatepwd")
	public ResponseEntity<String> updatePassword(@RequestBody PasswordChangeDTO pwd,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(settingsService.updatePassword(pwd, user));
	}

	@GetMapping("/api/settingpage/generatetoken")
	public ResponseEntity<String> generateToken(@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(settingsService.generateJwtToken(user));
	}
}
