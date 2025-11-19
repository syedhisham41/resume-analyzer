package com.resumeanalyzer.analyzer.dto;

import java.util.List;

public class ParseAllResponse {

	private List<String> noun_chunks;

	private List<String> verb_phrases;

	private List<String> titles;

	private List<String> skills;

	private List<NerEntitiesDTO> entities;

	private int noun_chunks_count;

	private int parsed_skill_count;

	public List<String> getNoun_chunks() {
		return noun_chunks;
	}

	public void setNoun_chunks(List<String> noun_chunks) {
		this.noun_chunks = noun_chunks;
	}

	public List<String> getVerb_phrases() {
		return verb_phrases;
	}

	public void setVerb_phrases(List<String> verb_phrases) {
		this.verb_phrases = verb_phrases;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public List<NerEntitiesDTO> getEntities() {
		return entities;
	}

	public void setEntities(List<NerEntitiesDTO> entities) {
		this.entities = entities;
	}

	public int getNoun_chunks_count() {
		return noun_chunks_count;
	}

	public void setNoun_chunks_count(int noun_chunks_count) {
		this.noun_chunks_count = noun_chunks_count;
	}

	public int getParsed_skill_count() {
		return parsed_skill_count;
	}

	public void setParsed_skill_count(int parsed_skill_count) {
		this.parsed_skill_count = parsed_skill_count;
	}

}
