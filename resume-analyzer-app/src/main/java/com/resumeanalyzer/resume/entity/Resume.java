package com.resumeanalyzer.resume.entity;

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

@Entity
public class Resume {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	User user;

	@Column(name = "parsedText", columnDefinition = "TEXT")
	private String parsedText;

	@Column(name = "title")
	private String title;

	@CreationTimestamp
	@Column(name = "createdAt")
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Analyze> analysis = new ArrayList<>();

	public Resume() {
	}

	public Resume(User user, String parsedText, String title, LocalDateTime createdAt) {
		super();
		this.user = user;
		this.parsedText = parsedText;
		this.title = title;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<Analyze> getAnalysis() {
		return analysis;
	}

	public void setAnalysis(List<Analyze> analysis) {
		this.analysis = analysis;
	}

}
