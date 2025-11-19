package com.resumeanalyzer.ui.dto;

import java.util.List;

public class ViewPageExtractedContentDTO {

	private List<String> skills;

	private int parsed_skill_count;

	public ViewPageExtractedContentDTO() {
	}

	public ViewPageExtractedContentDTO(List<String> skills, int parsed_skill_count) {
		super();
		this.skills = skills;
		this.parsed_skill_count = parsed_skill_count;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public int getParsed_skill_count() {
		return parsed_skill_count;
	}

	public void setParsed_skill_count(int parsed_skill_count) {
		this.parsed_skill_count = parsed_skill_count;
	}

}
