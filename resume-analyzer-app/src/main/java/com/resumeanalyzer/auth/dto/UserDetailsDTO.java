package com.resumeanalyzer.auth.dto;

public class UserDetailsDTO {

	private int userId;

	private String name;

	private String userName;

	private String email;

	private String currentCompany;

	private String currentRole;

	public UserDetailsDTO() {
	}

	public UserDetailsDTO(int userId, String name, String userName, String email, String currentCompany,
			String currentRole) {
		super();
		this.userId = userId;
		this.name = name;
		this.userName = userName;
		this.email = email;
		this.currentCompany = currentCompany;
		this.currentRole = currentRole;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCurrentCompany() {
		return currentCompany;
	}

	public void setCurrentCompany(String currentCompany) {
		this.currentCompany = currentCompany;
	}

	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

}
