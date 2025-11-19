package com.resumeanalyzer.ui.service;

import org.springframework.stereotype.Service;

import com.resumeanalyzer.auth.dto.PasswordChangeDTO;
import com.resumeanalyzer.auth.dto.UserDetailsDTO;
import com.resumeanalyzer.auth.dto.UserUpdateDetailsDTO;
import com.resumeanalyzer.auth.service.AuthService;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import jakarta.transaction.Transactional;

@Service
public class SettingsPageService {

	private final AuthService authService;

	public SettingsPageService(AuthService authService) {
		super();
		this.authService = authService;
	}

	public UserDetailsDTO getUserData(JwtUserDetails user) {
		return authService.getUserDetails(user);
	}

	@Transactional
	public UserDetailsDTO updateUserData(UserUpdateDetailsDTO userDetails, JwtUserDetails user) {
		return authService.updateUserDetails(userDetails, user);
	}

	@Transactional
	public void deleteUser(JwtUserDetails user) {
		authService.deleteUser(user);
	}

	@Transactional
	public String updatePassword(PasswordChangeDTO pwd, JwtUserDetails user) {
		return authService.changePassword(pwd, user);
	}

	public String generateJwtToken(JwtUserDetails user) {
		return authService.getGenerateToken(user);
	}
}
