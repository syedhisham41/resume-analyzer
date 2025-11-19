package com.resumeanalyzer.jd.dto;

import com.resumeanalyzer.jd.entity.JobDescriptionCandidate.CandidateType;

public class JdCandidateResponseDTO {

	private long id;

	private String candidateValue;

	private CandidateType type;

	private boolean isSelected;

	public JdCandidateResponseDTO() {
	}

	public JdCandidateResponseDTO(long id, String candidateValue, CandidateType type, boolean isSelected) {
		super();
		this.id = id;
		this.candidateValue = candidateValue;
		this.type = type;
		this.isSelected = isSelected;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCandidateValue() {
		return candidateValue;
	}

	public void setCandidateValue(String candidateValue) {
		this.candidateValue = candidateValue;
	}

	public CandidateType getType() {
		return type;
	}

	public void setType(CandidateType type) {
		this.type = type;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
