package com.resumeanalyzer.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.auth.dto.Jwt;
import com.resumeanalyzer.auth.dto.Login;
import com.resumeanalyzer.auth.dto.Signup;
import com.resumeanalyzer.auth.dto.UserDetailsDTO;
import com.resumeanalyzer.auth.dto.UserUpdateDetailsDTO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.auth.service.AuthService;
import com.resumeanalyzer.common.dto.JwtUserDetails;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private AuthService auth_service;

	public UserController(AuthService auth_service) {
		this.auth_service = auth_service;
	}

	// endpoints for user

	@GetMapping("/")
	public String userTest() {
		return "Welcome User";
	}

	@PostMapping("/signup")
	public String signUp(@RequestBody Signup signup) {

		User user = new User(signup.getName(), signup.getUserName(), signup.getEmail(), signup.getPassword(),
				signup.getCurrentCompany(), signup.getCurrentRole());
		int userId = auth_service.signup(user);

		return "signUp of User successful with user_id : " + userId;
	}

	@PostMapping("/login")
	public Jwt Login(@RequestBody Login login) {
		return auth_service.signin(login);

	}

	@PostMapping("/updateuser")
	public UserDetailsDTO updateUser(@RequestBody UserUpdateDetailsDTO userDetails,
			@AuthenticationPrincipal JwtUserDetails user) {
		return auth_service.updateUserDetails(userDetails, user);
	}
	
	@DeleteMapping("/delete")
	public void deleteUser(@AuthenticationPrincipal JwtUserDetails user) {
		 auth_service.deleteUser( user);
	}
}
