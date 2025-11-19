package com.resumeanalyzer.common.utils;

import org.springframework.stereotype.Component;

import com.resumeanalyzer.auth.exceptions.InvalidEmailFormatException;

@Component
public class AuthServiceUtils {

	public void validateEmail(String email) {
		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			throw new InvalidEmailFormatException("Invalid Email format", null);
		}
	}
}
