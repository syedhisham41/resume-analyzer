package com.resumeanalyzer.jd.entity;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_description_candidate")
public class JobDescriptionCandidate {

	public enum CandidateType {
		COMPANY, TITLE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "jd_id", nullable = false)
	private JobDescription jobDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 50)
	private CandidateType type;

	@Column(name = "candidate_value", nullable = false, length = 255)
	private String candidateValue;

	@Column(name = "is_selected", nullable = false)
	private boolean isSelected = false;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt;

	public JobDescriptionCandidate() {
	}

	public JobDescriptionCandidate(long id, JobDescription jobDescription, CandidateType type, String candidateValue,
			boolean isSelected, Timestamp createdAt) {
		super();
		this.id = id;
		this.jobDescription = jobDescription;
		this.type = type;
		this.candidateValue = candidateValue;
		this.isSelected = isSelected;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public JobDescription getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(JobDescription jobDescription) {
		this.jobDescription = jobDescription;
	}

	public CandidateType getType() {
		return type;
	}

	public void setType(CandidateType type) {
		this.type = type;
	}

	public String getCandidateValue() {
		return candidateValue;
	}

	public void setCandidateValue(String candidateValue) {
		this.candidateValue = candidateValue;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
