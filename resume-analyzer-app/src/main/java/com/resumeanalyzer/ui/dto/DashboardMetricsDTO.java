package com.resumeanalyzer.ui.dto;

public class DashboardMetricsDTO {

	private int resumeCount;

	private int jdCount;

	private int analyzeCount;

	private double averageOverallFit;

	private String name;

	private String userName;

	private String email;

	private String currentCompany;

	private String currentRole;

	public DashboardMetricsDTO() {
	}

	public DashboardMetricsDTO(int resumeCount, int jdCount, int analyzeCount, double averageOverallFit, String name,
			String userName, String email, String currentCompany, String currentRole) {
		super();
		this.resumeCount = resumeCount;
		this.jdCount = jdCount;
		this.analyzeCount = analyzeCount;
		this.averageOverallFit = averageOverallFit;
		this.name = name;
		this.userName = userName;
		this.email = email;
		this.currentCompany = currentCompany;
		this.currentRole = currentRole;
	}

	public int getResumeCount() {
		return resumeCount;
	}

	public void setResumeCount(int resumeCount) {
		this.resumeCount = resumeCount;
	}

	public int getJdCount() {
		return jdCount;
	}

	public void setJdCount(int jdCount) {
		this.jdCount = jdCount;
	}

	public int getAnalyzeCount() {
		return analyzeCount;
	}

	public void setAnalyzeCount(int analyzeCount) {
		this.analyzeCount = analyzeCount;
	}

	public double getAverageOverallFit() {
		return averageOverallFit;
	}

	public void setAverageOverallFit(double averageOverallFit) {
		this.averageOverallFit = averageOverallFit;
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
