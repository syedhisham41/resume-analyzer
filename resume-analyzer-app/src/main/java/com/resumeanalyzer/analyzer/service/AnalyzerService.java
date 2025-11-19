package com.resumeanalyzer.analyzer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.analyzer.dto.AnalyzeResponseDTO;
import com.resumeanalyzer.analyzer.dto.ParseAnalyzeText;
import com.resumeanalyzer.analyzer.entity.Analyze;
import com.resumeanalyzer.analyzer.exceptions.AnalyzeNotFoundException;
import com.resumeanalyzer.analyzer.repository.AnalyzeRepository;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;
import com.resumeanalyzer.common.utils.AIUtils;
import com.resumeanalyzer.common.utils.AnalyzerServiceUtils;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.jd.exceptions.JdNotFoundException;
import com.resumeanalyzer.jd.repository.JobDescriptionRepository;
import com.resumeanalyzer.resume.entity.Resume;
import com.resumeanalyzer.resume.exceptions.AccessDeniedException;
import com.resumeanalyzer.resume.exceptions.ResumeNotFoundException;
import com.resumeanalyzer.resume.repository.ResumeRepository;

@Service
public class AnalyzerService {

	private AnalyzeRepository analyzeRepository;

	private ResumeRepository resumeRepository;

	private JobDescriptionRepository jdRepository;

	private ActivityService activityService;

	private AIUtils aiUtils;

	public AnalyzerService(AnalyzeRepository analyzeRepository, ResumeRepository resumeRepository,
			JobDescriptionRepository jdRepository, AIUtils aiUtils, ActivityService activityService) {
		super();
		this.analyzeRepository = analyzeRepository;
		this.resumeRepository = resumeRepository;
		this.jdRepository = jdRepository;
		this.aiUtils = aiUtils;
		this.activityService = activityService;
	}

//	public AnalyzeResponseDTO analyzeUsingRawText(ParseAnalyzeText analyzeText, JwtUserDetails user) {
//		AnalyzeResponseDTO response = aiUtils.runAnalyzeEngine(analyzeText);
//		
//		Analyze analyzeResult = AnalyzerServiceUtils.mapAnalyzeResponseDTOtoAnalyze(response);
//		
//		return analyzeRepository.save(response);
//	}

	public AnalyzeResponseDTO analyze(long jdId, long resumeId, JwtUserDetails user) {

		try {
			JobDescription jd = jdRepository.findById(jdId)
					.orElseThrow(() -> new JdNotFoundException("JD not found : " + jdId, null));

			Resume resume = resumeRepository.findById(resumeId)
					.orElseThrow(() -> new ResumeNotFoundException("Resume not found : " + resumeId, null));

			if (jd.getUser().getUserId() != user.getUserId() || resume.getUser().getUserId() != user.getUserId()) {
				throw new AccessDeniedException("User does not have access to this JD or Resume", null);
			}

			ParseAnalyzeText analyzeText = new ParseAnalyzeText(jd.getParsedText(), resume.getParsedText());
			AnalyzeResponseDTO response = aiUtils.runAnalyzeEngine(analyzeText);

			Analyze analyzeResult = AnalyzerServiceUtils.mapAnalyzeResponseDTOtoAnalyze(response);

			analyzeResult.setJd(jd);
			analyzeResult.setResume(resume);
			analyzeResult.setUser(jd.getUser());

			analyzeResult = analyzeRepository.save(analyzeResult);

			response.setJdTitle(jd.getTitle());
			response.setJdCompany(jd.getCompanyName());
			response.setResumeTitle(resume.getTitle());
			response.setUserId(user.getUserId());
			response.setAnalyzeId(analyzeResult.getAnalyzeId());

			activityService.record(ActionType.ANALYSIS, "Analysis", "analyze",
					"Analysis completed successfully !" + " AnalyseFit :" + analyzeResult.getOverallFit() * 100 + " %",
					ActivityStatus.SUCCESS, user);
			return response;
		} catch (Exception e) {
			activityService.record(ActionType.ANALYSIS, "Analysis", "", "Analysis failed !", ActivityStatus.FAILURE,
					user);
			throw e;
		}

	}

	public List<AnalyzeResponseDTO> getAllAnalyzesPerUser(JwtUserDetails user) {

		List<Analyze> analyzeResults = analyzeRepository.findByUser_UserId(user.getUserId());

		List<AnalyzeResponseDTO> list = new ArrayList<>();
		for (Analyze each : analyzeResults) {
			list.add(AnalyzerServiceUtils.mapAnalyzeToAnalyzeResponseDTO(each));
		}
		return list;
	}

	public double getAverageOverallFit(JwtUserDetails user) {

		List<Analyze> analyzeResults = analyzeRepository.findByUser_UserId(user.getUserId());
		int sizeOfAnalyzeResults = analyzeResults.size();

		if (sizeOfAnalyzeResults == 0)
			return 0;

		double overallFitSum = 0;

		for (Analyze each : analyzeResults) {
			overallFitSum += each.getOverallFit();
		}

		System.out.println("overall fit sum : " + overallFitSum);
		return overallFitSum / sizeOfAnalyzeResults;
	}

	public AnalyzeResponseDTO getLatestAnalyzeResult(JwtUserDetails user) {
		Analyze result = analyzeRepository.findTopByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
		if (result == null)
			throw new AnalyzeNotFoundException("Analysis result not found for the user : " + user.getUserId(), null);
		return AnalyzerServiceUtils.mapAnalyzeToAnalyzeResponseDTO(result);
	}

	public List<AnalyzeResponseDTO> getLatest5AnalyzesPerUser(JwtUserDetails user) {
		List<Analyze> analyzeResults = analyzeRepository.findTop5ByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

		List<AnalyzeResponseDTO> list = new ArrayList<>();
		for (Analyze each : analyzeResults) {
			list.add(AnalyzerServiceUtils.mapAnalyzeToAnalyzeResponseDTO(each));
		}
		return list;
	}

	public String deleteAnalysisByAnalyzeId(long analyzeId, JwtUserDetails user) {

		Analyze analysis = analyzeRepository.findById(analyzeId).orElseThrow(
				() -> new AnalyzeNotFoundException("no analysis found with AnalyzeId : " + analyzeId, null));

		if (user.getUserId() != analysis.getUser().getUserId())
			throw new AccessDeniedException("user does not have access to delete this Analysis record : " + analyzeId,
					null);

		analyzeRepository.delete(analysis);

		return "Analysis with AnalyzeId : " + analyzeId + " deleted successfully";
	}

	public AnalyzeResponseDTO getAnalysisByAnalyzeId(long analyzeId, JwtUserDetails user) {

		Analyze analysis = analyzeRepository.findById(analyzeId).orElseThrow(
				() -> new AnalyzeNotFoundException("no analysis found with AnalyzeId : " + analyzeId, null));

		if (user.getUserId() != analysis.getUser().getUserId())
			throw new AccessDeniedException("user does not have access to delete this Analysis record : " + analyzeId,
					null);

		return AnalyzerServiceUtils.mapAnalyzeToAnalyzeResponseDTO(analysis);
	}

}
