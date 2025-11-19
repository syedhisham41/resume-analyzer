package com.resumeanalyzer.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.resumeanalyzer.analyzer.dto.NerEntitiesDTO;
import com.resumeanalyzer.analyzer.dto.ParseAllResponse;
import com.resumeanalyzer.jd.dto.JdCandidateResponseDTO;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.jd.entity.JobDescriptionCandidate;

public class JdServiceUtils {

	public static JdResponseDTO mapJdtoJdResponseDTO(JobDescription jd) {

		return new JdResponseDTO(jd.getId(), jd.getTitle(), jd.getCompanyName(), jd.getParsedText(),
				jd.getUser().getUserId(), jd.getCreatedAt());
	}

	public static List<String> extractCompanyNamesFromParsedAllJd(ParseAllResponse response) {
		List<String> companyNames = new ArrayList<>();
		for (NerEntitiesDTO each : response.getEntities()) {
			if (each.getLabel().equals("ORG"))
				companyNames.add(each.getText());
		}
		return companyNames;
	}

	public static JdCandidateResponseDTO mapJdCandidateToCandidateResponseDTO(JobDescriptionCandidate jdCandidate) {
		return new JdCandidateResponseDTO(jdCandidate.getId(), jdCandidate.getCandidateValue(), jdCandidate.getType(),
				jdCandidate.isSelected());
	}
}
