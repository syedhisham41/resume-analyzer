package com.resumeanalyzer.auth.dto;

public class UserUpdateDetailsDTO {

	private String name;

	private String currentCompany;

	private String currentRole;

	public UserUpdateDetailsDTO() {
	}

	public UserUpdateDetailsDTO(String name, String currentCompany, String currentRole) {
		super();
		this.name = name;
		this.currentCompany = currentCompany;
		this.currentRole = currentRole;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
