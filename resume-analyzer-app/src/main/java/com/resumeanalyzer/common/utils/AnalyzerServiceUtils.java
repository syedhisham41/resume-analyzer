package com.resumeanalyzer.common.utils;

import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.entity.Analyze;

public class AnalyzerServiceUtils {

	public static AnalyzeResponseDTO mapAnalyzeToAnalyzeResponseDTO(Analyze analyzeResult) {
		return new AnalyzeResponseDTO(analyzeResult.getAnalyzeId(), analyzeResult.getJd().getTitle(),
				analyzeResult.getJd().getCompanyName(), analyzeResult.getResume().getTitle(),
				analyzeResult.getUser().getUserId(), analyzeResult.getCreatedAt(), analyzeResult.getSkillMatch(),
				analyzeResult.getVerbMatch(), analyzeResult.getTitleMatch(), analyzeResult.getQualificationMatch(),
				analyzeResult.getOverallFit(), analyzeResult.getJdSkills(), analyzeResult.getResumeSkills(),
				analyzeResult.getMatchedSkills(), analyzeResult.getUnMatchedSkills(), analyzeResult.getJdVerbs(),
				analyzeResult.getResumeVerbs(), analyzeResult.getMatchedVerbs(), analyzeResult.getUnMatchedVerbs(),
				analyzeResult.getJdTitles(), analyzeResult.getResumeTitles(), analyzeResult.getJdQualifications(),
				analyzeResult.getResumeQualifications(), analyzeResult.getMatchedQualifications(),
				analyzeResult.getUnMatchedQualifications());
	}

	public static Analyze mapAnalyzeResponseDTOtoAnalyze(AnalyzeResponseDTO analyzeResponseDto) {
		return new Analyze(null, null, null, analyzeResponseDto.getSkillMatch(), analyzeResponseDto.getVerbMatch(),
				analyzeResponseDto.getTitleMatch(), analyzeResponseDto.getQualificationMatch(),
				analyzeResponseDto.getOverallFit(), analyzeResponseDto.getJdSkills(),
				analyzeResponseDto.getResumeSkills(), analyzeResponseDto.getMatchedSkills(),
				analyzeResponseDto.getUnMatchedSkills(), analyzeResponseDto.getJdVerbs(),
				analyzeResponseDto.getResumeVerbs(), analyzeResponseDto.getMatchedVerbs(),
				analyzeResponseDto.getUnMatchedVerbs(), analyzeResponseDto.getJdTitles(),
				analyzeResponseDto.getResumeTitles(), analyzeResponseDto.getJdQualifications(),
				analyzeResponseDto.getResumeQualifications(), analyzeResponseDto.getMatchedQualifications(),
				analyzeResponseDto.getUnMatchedQualifications(), null);
	}
}
