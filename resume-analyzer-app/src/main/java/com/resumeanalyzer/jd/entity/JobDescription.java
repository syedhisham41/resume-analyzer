package com.resumeanalyzer.jd.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.resumeanalyzer.analyzer.entity.Analyze;
import com.resumeanalyzer.auth.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_description")
public class JobDescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	User user;

	@Column(name = "parsedText", nullable = false, columnDefinition = "TEXT")
	private String parsedText;

	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Column(name = "companyName", nullable = true, length = 255)
	private String companyName;

	@CreationTimestamp
	@Column(name = "createdAt")
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "jobDescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobDescriptionCandidate> candidates = new ArrayList<>();
	
	@OneToMany(mappedBy = "jd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Analyze> analysis = new ArrayList<>();

	public JobDescription() {
	}

	public JobDescription(User user, String parsedText, String title, String companyName, LocalDateTime createdAt) {
		super();
		this.user = user;
		this.parsedText = parsedText;
		this.title = title;
		this.companyName = companyName;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getParsedText() {
		return parsedText;
	}

	public void setParsedText(String parsedText) {
		this.parsedText = parsedText;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<JobDescriptionCandidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<JobDescriptionCandidate> candidates) {
		this.candidates = candidates;
	}

	public List<Analyze> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(List<Analyze> analysis) {
		this.analysis = analysis;
	}

}
