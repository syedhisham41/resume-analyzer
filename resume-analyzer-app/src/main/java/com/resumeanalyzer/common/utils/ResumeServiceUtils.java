package com.resumeanalyzer.common.utils;

import org.springframework.core.io.ByteArrayResource;

import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.resume.entity.Resume;

public class ResumeServiceUtils {

	public static ResumeResponseDTO mapResumeToResumeDTO(Resume resume) {

		return new ResumeResponseDTO(resume.getId(), resume.getParsedText(), resume.getTitle(), resume.getCreatedAt(),
				resume.getUser().getUserId());
	}
	
	//TODO
	public static ByteArrayResource generateFile(Resume resume, String format) {
		return null;
		
	}
}
