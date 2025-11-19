package com.resumeanalyzer.auth.dto;

public class Signup {

	private String name;

	private String userName;

	private String email;

	private String password;

	private String currentCompany;

	private String currentRole;

	public Signup(String name, String userName, String email, String password, String currentCompany,
			String currentRole) {
		super();
		this.name = name;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.currentCompany = currentCompany;
		this.currentRole = currentRole;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
