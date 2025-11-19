package com.resumeanalyzer.jd.dto;

import com.resumeanalyzer.jd.entity.JobDescriptionCandidate.CandidateType;

public class SelectJdValueRequest {

	private CandidateType jdValueType;

	private String selectedJdValue;

	public SelectJdValueRequest(CandidateType jdValueType, String selectedJdValue) {
		super();
		this.jdValueType = jdValueType;
		this.selectedJdValue = selectedJdValue;
	}

	public CandidateType getJdValueType() {
		return jdValueType;
	}

	public void setJdValueType(CandidateType jdValueType) {
		this.jdValueType = jdValueType;
	}

	public String getSelectedJdValue() {
		return selectedJdValue;
	}

	public void setSelectedJdValue(String selectedJdValue) {
		this.selectedJdValue = selectedJdValue;
	}

}
