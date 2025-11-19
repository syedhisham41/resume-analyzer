package com.resumeanalyzer.analyzer.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.resume.entity.Resume;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "analysis")
public class Analyze {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long analyzeId;

	@ManyToOne
	@JoinColumn(name = "jd_id", nullable = false)
	private JobDescription jd;

	@ManyToOne
	@JoinColumn(name = "resume_id", nullable = false)
	private Resume resume;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	User user;

	@Column(name = "skills_match_pct")
	private double skillMatch;

	@Column(name = "verb_match_pct")
	private double verbMatch;

	@Column(name = "title_match_pct")
	private double titleMatch;

	@Column(name = "qual_match_pct")
	private double qualificationMatch;

	@Column(name = "overall_fit", nullable = false)
	private double overallFit;

	@Column(name = "jd_skills", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> jdSkills;

	@Column(name = "resume_skills", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> resumeSkills;

	@Column(name = "matched_skills", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
//	private List<String> matchedSkills;
	private HashMap<String, Double> matchedSkills;

	@Column(name = "un_matched_skills", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> unMatchedSkills;

	@Column(name = "jd_verbs", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> jdVerbs;

	@Column(name = "resume_verbs", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> resumeVerbs;

	@Column(name = "matched_verbs", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private HashMap<String, Double> matchedVerbs;

	@Column(name = "un_matched_verbs", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> unMatchedVerbs;

	@Column(name = "jd_titles", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> jdTitles;

	@Column(name = "resume_titles", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> resumeTitles;

	@Column(name = "jd_qualifications", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> jdQualifications;

	@Column(name = "resume_qualifications", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> resumeQualifications;

	@Column(name = "matched_qualifications", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> matchedQualifications;

	@Column(name = "un_matched_qualifications", columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> unMatchedQualifications;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Analyze() {
	}

	public Analyze(JobDescription jd, Resume resume, User user, double skillMatch, double verbMatch,
			double titleMatch, double qualificationMatch, double overallFit, List<String> jdSkills,
			List<String> resumeSkills, HashMap<String, Double> matchedSkills, List<String> unMatchedSkills,
			List<String> jdVerbs, List<String> resumeVerbs, HashMap<String, Double> matchedVerbs,
			List<String> unMatchedVerbs, List<String> jdTitles, List<String> resumeTitles,
			List<String> jdQualifications, List<String> resumeQualifications, List<String> matchedQualifications,
			List<String> unMatchedQualifications, LocalDateTime createdAt) {
		super();
		this.jd = jd;
		this.resume = resume;
		this.user = user;
		this.skillMatch = skillMatch;
		this.verbMatch = verbMatch;
		this.titleMatch = titleMatch;
		this.qualificationMatch = qualificationMatch;
		this.overallFit = overallFit;
		this.jdSkills = jdSkills;
		this.resumeSkills = resumeSkills;
		this.matchedSkills = matchedSkills;
		this.unMatchedSkills = unMatchedSkills;
		this.jdVerbs = jdVerbs;
		this.resumeVerbs = resumeVerbs;
		this.matchedVerbs = matchedVerbs;
		this.unMatchedVerbs = unMatchedVerbs;
		this.jdTitles = jdTitles;
		this.resumeTitles = resumeTitles;
		this.jdQualifications = jdQualifications;
		this.resumeQualifications = resumeQualifications;
		this.matchedQualifications = matchedQualifications;
		this.unMatchedQualifications = unMatchedQualifications;
		this.createdAt = createdAt;
	}

	public long getAnalyzeId() {
		return analyzeId;
	}

	public void setAnalyzeId(long analyzeId) {
		this.analyzeId = analyzeId;
	}

	public JobDescription getJd() {
		return jd;
	}

	public void setJd(JobDescription jd) {
		this.jd = jd;
	}

	public Resume getResume() {
		return resume;
	}

	public void setResume(Resume resume) {
		this.resume = resume;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getSkillMatch() {
		return skillMatch;
	}

	public void setSkillMatch(double skillMatch) {
		this.skillMatch = skillMatch;
	}

	public double getVerbMatch() {
		return verbMatch;
	}

	public void setVerbMatch(double verbMatch) {
		this.verbMatch = verbMatch;
	}

	public double getTitleMatch() {
		return titleMatch;
	}

	public void setTitleMatch(double titleMatch) {
		this.titleMatch = titleMatch;
	}

	public double getQualificationMatch() {
		return qualificationMatch;
	}

	public void setQualificationMatch(double qualificationMatch) {
		this.qualificationMatch = qualificationMatch;
	}

	public double getOverallFit() {
		return overallFit;
	}

	public void setOverallFit(double overallFit) {
		this.overallFit = overallFit;
	}

	public HashMap<String, Double> getMatchedSkills() {
		return matchedSkills;
	}

	public void setMatchedSkills(HashMap<String, Double> matchedSkills) {
		this.matchedSkills = matchedSkills;
	}

	public List<String> getUnMatchedSkills() {
		return unMatchedSkills;
	}

	public void setUnMatchedSkills(List<String> unMatchedSkills) {
		this.unMatchedSkills = unMatchedSkills;
	}

	public HashMap<String, Double> getMatchedVerbs() {
		return matchedVerbs;
	}

	public void setMatchedVerbs(HashMap<String, Double> matchedVerbs) {
		this.matchedVerbs = matchedVerbs;
	}

	public List<String> getUnMatchedVerbs() {
		return unMatchedVerbs;
	}

	public void setUnMatchedVerbs(List<String> unMatchedVerbs) {
		this.unMatchedVerbs = unMatchedVerbs;
	}

	public List<String> getJdTitles() {
		return jdTitles;
	}

	public void setJdTitles(List<String> jdTitles) {
		this.jdTitles = jdTitles;
	}

	public List<String> getResumeTitles() {
		return resumeTitles;
	}

	public void setResumeTitles(List<String> resumeTitles) {
		this.resumeTitles = resumeTitles;
	}

	public List<String> getJdQualifications() {
		return jdQualifications;
	}

	public void setJdQualifications(List<String> jdQualifications) {
		this.jdQualifications = jdQualifications;
	}

	public List<String> getResumeQualifications() {
		return resumeQualifications;
	}

	public void setResumeQualifications(List<String> resumeQualifications) {
		this.resumeQualifications = resumeQualifications;
	}

	public List<String> getMatchedQualifications() {
		return matchedQualifications;
	}

	public void setMatchedQualifications(List<String> matchedQualifications) {
		this.matchedQualifications = matchedQualifications;
	}

	public List<String> getUnMatchedQualifications() {
		return unMatchedQualifications;
	}

	public void setUnMatchedQualifications(List<String> unMatchedQualifications) {
		this.unMatchedQualifications = unMatchedQualifications;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<String> getJdSkills() {
		return jdSkills;
	}

	public void setJdSkills(List<String> jdSkills) {
		this.jdSkills = jdSkills;
	}

	public List<String> getResumeSkills() {
		return resumeSkills;
	}

	public void setResumeSkills(List<String> resumeSkills) {
		this.resumeSkills = resumeSkills;
	}

	public List<String> getJdVerbs() {
		return jdVerbs;
	}

	public void setJdVerbs(List<String> jdVerbs) {
		this.jdVerbs = jdVerbs;
	}

	public List<String> getResumeVerbs() {
		return resumeVerbs;
	}

	public void setResumeVerbs(List<String> resumeVerbs) {
		this.resumeVerbs = resumeVerbs;
	}
}
