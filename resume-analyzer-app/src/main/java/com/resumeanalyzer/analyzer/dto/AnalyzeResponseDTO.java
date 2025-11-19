package com.resumeanalyzer.analyzer.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnalyzeResponseDTO {

	private long analyzeId;

	private String jdTitle;

	private String jdCompany;

	private String resumeTitle;

	private int userId;

	@JsonFormat(pattern = "dd MMM yyyy")
	private LocalDateTime createdAt;

	@JsonProperty("skill_match_pct")
	private double skillMatch;

	@JsonProperty("verb_match_pct")
	private double verbMatch;

	@JsonProperty("title_match_pct")
	private double titleMatch;

	@JsonProperty("qual_match_pct")
	private double qualificationMatch;

	@JsonProperty("overall_fit")
	private double overallFit;

	@JsonProperty("jd_skills")
	private List<String> jdSkills;

	@JsonProperty("resume_skills")
	private List<String> resumeSkills;

	@JsonProperty("matched_skills")
	private HashMap<String, Double> matchedSkills;

	@JsonProperty("unmatched_skills")
	private List<String> unMatchedSkills;

	@JsonProperty("jd_verbs")
	private List<String> jdVerbs;

	@JsonProperty("resume_verbs")
	private List<String> resumeVerbs;

	@JsonProperty("matched_verb_phrases")
	private HashMap<String, Double> matchedVerbs;

	@JsonProperty("unmatched_verb_phrases")
	private List<String> unMatchedVerbs;

	@JsonProperty("jd_titles")
	private List<String> jdTitles;

	@JsonProperty("resume_titles")
	private List<String> resumeTitles;

	@JsonProperty("jd_qualifications")
	private List<String> jdQualifications;

	@JsonProperty("resume_qualifications")
	private List<String> resumeQualifications;

	@JsonProperty("matched_qualifications")
	private List<String> matchedQualifications;

	@JsonProperty("unmatched_qualifications")
	private List<String> unMatchedQualifications;

	public AnalyzeResponseDTO() {
	}

	public AnalyzeResponseDTO(long analyzeId, String jdTitle, String jdCompany, String resumeTitle, int userId,
			LocalDateTime createdAt, double skillMatch, double verbMatch, double titleMatch, double qualificationMatch,
			double overallFit, List<String> jdSkills, List<String> resumeSkills, HashMap<String, Double> matchedSkills,
			List<String> unMatchedSkills, List<String> jdVerbs, List<String> resumeVerbs,
			HashMap<String, Double> matchedVerbs, List<String> unMatchedVerbs, List<String> jdTitles,
			List<String> resumeTitles, List<String> jdQualifications, List<String> resumeQualifications,
			List<String> matchedQualifications, List<String> unMatchedQualifications) {
		super();
		this.analyzeId = analyzeId;
		this.jdTitle = jdTitle;
		this.jdCompany = jdCompany;
		this.resumeTitle = resumeTitle;
		this.userId = userId;
		this.createdAt = createdAt;
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

	public long getAnalyzeId() {
		return analyzeId;
	}

	public void setAnalyzeId(long analyzeId) {
		this.analyzeId = analyzeId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getJdTitle() {
		return jdTitle;
	}

	public void setJdTitle(String jdTitle) {
		this.jdTitle = jdTitle;
	}

	public String getJdCompany() {
		return jdCompany;
	}

	public void setJdCompany(String jdCompany) {
		this.jdCompany = jdCompany;
	}

	public String getResumeTitle() {
		return resumeTitle;
	}

	public void setResumeTitle(String resumeTitle) {
		this.resumeTitle = resumeTitle;
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
