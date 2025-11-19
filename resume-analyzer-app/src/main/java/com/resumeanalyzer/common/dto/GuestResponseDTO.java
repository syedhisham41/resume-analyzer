package com.resumeanalyzer.common.dto;

import java.util.List;

public class GuestResponseDTO {

	private double overallFit;

	private List<String> topSkills;

	public GuestResponseDTO() {
	}

	public GuestResponseDTO(double overallFit, List<String> topSkills) {
		super();
		this.overallFit = overallFit;
		this.topSkills = topSkills;
	}

	public double getOverallFit() {
		return overallFit;
	}

	public void setOverallFit(double overallFit) {
		this.overallFit = overallFit;
	}

	public List<String> getTopSkills() {
		return topSkills;
	}

	public void setTopSkills(List<String> topSkills) {
		this.topSkills = topSkills;
	}
}
