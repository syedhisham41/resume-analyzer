package com.resumeanalyzer.auth.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.resumeanalyzer.analyzer.entity.Analyze;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.resume.entity.Resume;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int userId;

	@Column(name = "name")
	private String name;

	@Column(name = "user_name", unique = true)
	private String userName;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "password_hash")
	private String passwordHash;

	@Column(name = "current_company")
	private String currentCompany;

	@Column(name = "current_role")
	private String currentRole;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobDescription> jobDescription = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resume = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Analyze> analyze = new ArrayList<>();

	public User() {
	}

	public User(String name, String user_name, String email, String password_hash, String current_company,
			String current_role) {
		super();
		this.name = name;
		this.userName = user_name;
		this.email = email;
		this.passwordHash = password_hash;
		this.currentCompany = current_company;
		this.currentRole = current_role;
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

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
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
	

	public List<JobDescription> getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(List<JobDescription> jobDescription) {
		this.jobDescription = jobDescription;
	}

	public List<Resume> getResume() {
		return resume;
	}

	public void setResume(List<Resume> resume) {
		this.resume = resume;
	}

	public List<Analyze> getAnalyze() {
		return analyze;
	}

	public void setAnalyze(List<Analyze> analyze) {
		this.analyze = analyze;
	}

}
