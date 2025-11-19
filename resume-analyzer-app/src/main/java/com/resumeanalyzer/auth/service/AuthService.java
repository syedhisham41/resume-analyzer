package com.resumeanalyzer.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.dto.Jwt;
import com.resumeanalyzer.auth.dto.Login;
import com.resumeanalyzer.auth.dto.PasswordChangeDTO;
import com.resumeanalyzer.auth.dto.UserDetailsDTO;
import com.resumeanalyzer.auth.dto.UserUpdateDetailsDTO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.auth.exceptions.DifferentPasswordException;
import com.resumeanalyzer.auth.exceptions.InvalidEmailFormatException;
import com.resumeanalyzer.auth.exceptions.InvalidPasswordException;
import com.resumeanalyzer.auth.exceptions.UserNameOrEmailEmptyException;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;
import com.resumeanalyzer.common.utils.AuthServiceUtils;
import com.resumeanalyzer.common.utils.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

	private final AuthDAO authDao;

	private final AuthServiceUtils utils;

	private final PasswordEncoder passwordEncoder;

	private final JwtUtils jwtUtils;

	private final ActivityService activityService;

	public AuthService(AuthDAO authDao, AuthServiceUtils utils, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
			ActivityService activityService) {
		this.authDao = authDao;
		this.utils = utils;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
		this.activityService = activityService;
	}

	@Transactional
	public int signup(User user) {

		// bcrypt hashing of password
		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

		// validate email address
		utils.validateEmail(user.getEmail());

		return authDao.signUp(user).getUserId();
	}

	@Transactional
	public Jwt signin(Login login) {

		User user = null;

		if (login.getUserName() != null && !login.getUserName().isBlank()) {

			// checking of the username passed by the UI form is an email address.
			try {
				utils.validateEmail(login.getUserName());
				login.setEmail(login.getUserName());
				user = authDao.getUserByEmail(login.getEmail());
			} catch (InvalidEmailFormatException e) {

				// username passed in the form is not email format, checking for valid username
				user = authDao.getUserByUserName(login.getUserName());
			}

		} else {
			throw new UserNameOrEmailEmptyException("User Login field is empty: Provide Username or Email ", null);
		}

		if (!passwordEncoder.matches(login.getPassword(), user.getPasswordHash()))
			throw new InvalidPasswordException("Incorrect password", null);

		return jwtUtils.generateJwtToken(user);
	}

	@Transactional
	public Jwt guestLogin() {
		return jwtUtils.generateGuestToken();
	}

	public UserDetailsDTO getUserDetails(JwtUserDetails userDetails) {

		User user = authDao.getUserById(userDetails.getUserId());
		return new UserDetailsDTO(user.getUserId(), user.getName(), user.getUserName(), user.getEmail(),
				user.getCurrentCompany(), user.getCurrentRole());
	}

	@Transactional
	public UserDetailsDTO updateUserDetails(UserUpdateDetailsDTO userDetails, JwtUserDetails user) {

		try {
			User userObject = authDao.getUserById(user.getUserId());
			userObject.setName(userDetails.getName());
			userObject.setCurrentCompany(userDetails.getCurrentCompany());
			userObject.setCurrentRole(userDetails.getCurrentRole());
			userObject = authDao.updateUserDetails(userObject);

			activityService.record(ActionType.UPDATE, "User", "Details", "User details updated successfully !",
					ActivityStatus.SUCCESS, user);
			return new UserDetailsDTO(userObject.getUserId(), userObject.getName(), userObject.getUserName(),
					userObject.getEmail(), userObject.getCurrentCompany(), userObject.getCurrentRole());
		} catch (Exception e) {
			activityService.record(ActionType.UPDATE, "User", "Details", "User details update failed !",
					ActivityStatus.FAILURE, user);
			throw e;
		}

	}

	@Transactional
	public void deleteUser(JwtUserDetails user) {
		User userObject = authDao.getUserById(user.getUserId());
		authDao.deleteUser(userObject);

	}

	@Transactional
	public String changePassword(PasswordChangeDTO pwd, JwtUserDetails user) {
		try {
			User userObject = authDao.getUserById(user.getUserId());

			if (!passwordEncoder.matches(pwd.getCurrentPassword(), userObject.getPasswordHash()))
				throw new InvalidPasswordException("Incorrect password", null);

			if (!pwd.getNewPassword().equals(pwd.getConfirmPassword()))
				throw new DifferentPasswordException("New Password and confirm password are different.", null);

			userObject.setPasswordHash(passwordEncoder.encode(pwd.getNewPassword()));

			activityService.record(ActionType.UPDATE, "User", "Password", "User password updated successfully !",
					ActivityStatus.SUCCESS, user);
			return authDao.changePassword(userObject);
		} catch (Exception e) {
			activityService.record(ActionType.UPDATE, "User", "Password", "User password update failed !",
					ActivityStatus.FAILURE, user);
			throw e;
		}

	}

	public String getGenerateToken(JwtUserDetails userDetails) {
		User user = authDao.getUserById(userDetails.getUserId());
		return jwtUtils.generateJwtToken(user).getToken();
	}

}
