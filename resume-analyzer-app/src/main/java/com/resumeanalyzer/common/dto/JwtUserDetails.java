package com.resumeanalyzer.common.dto;

public class JwtUserDetails {
	
	private String userName;
	
	private String email;
	
	private int userId;
	
	public JwtUserDetails() {}

	public JwtUserDetails(String userName, String email, int userId) {
		super();
		this.userName = userName;
		this.email = email;
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	

}
