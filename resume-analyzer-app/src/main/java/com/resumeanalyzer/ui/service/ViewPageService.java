package com.resumeanalyzer.ui.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resumeanalyzer.analyzer.dto.ParseAllResponse;
import com.resumeanalyzer.analyzer.dto.ParseText;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.service.JdService;
import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.resume.service.ResumeService;
import com.resumeanalyzer.ui.dto.ViewPageDataDTO;
import com.resumeanalyzer.ui.dto.ViewPageExtractedContentDTO;

@Service
public class ViewPageService {

	private JdService jdService;

	private ResumeService resumeService;

	private DashboardService dashboardService;

	public ViewPageService(JdService jdService, ResumeService resumeService, DashboardService dashboardService) {
		super();
		this.jdService = jdService;
		this.resumeService = resumeService;
		this.dashboardService = dashboardService;
	}

	// get total JD count
	public int getTotalJds(JwtUserDetails user) {
		return dashboardService.getTotalJdCount(user);
	}

	public JdResponseDTO getLastUploadedJd(JwtUserDetails user) {
		return jdService.getLatestUploadedJd(user);
	}

	public List<JdResponseDTO> getLastFiveUploadedJds(JwtUserDetails user) {
		return jdService.getLatestFiveUploadedJds(user);
	}

	public List<JdResponseDTO> getAllJds(JwtUserDetails user) {
		return jdService.getAllJdbyUser(user);
	}

	public String deleteJd(long jdId, JwtUserDetails user) {
		return jdService.deleteJdByJdId(jdId, user);
	}

	public int getTotalResumes(JwtUserDetails user) {
		return dashboardService.getTotalResumeCount(user);
	}

	public ResumeResponseDTO getLastUploadedResume(JwtUserDetails user) {
		return resumeService.getLatestUploadedResume(user);
	}

	public List<ResumeResponseDTO> getLastFiveUploadedResumes(JwtUserDetails user) {
		return resumeService.getLatestFiveUploadedResumes(user);
	}

	public List<ResumeResponseDTO> getAllResumes(JwtUserDetails user) {
		return resumeService.getAllResumebyUser(user);
	}

	public String deleteResume(long resumeId, JwtUserDetails user) {
		return resumeService.deleteResumeByResumeId(resumeId, user);
	}

	public ViewPageDataDTO getViewPageData(JwtUserDetails user, String type) {

		if (type.equals("jd")) {
			return new ViewPageDataDTO(getTotalJds(user), getLastUploadedJd(user).getCreatedAt(),
					getLastUploadedJd(user), getLastFiveUploadedJds(user), getAllJds(user));
		} else
			return new ViewPageDataDTO(getTotalResumes(user), getLastUploadedResume(user).getCreatedAt(),
					getLastUploadedResume(user), getLastFiveUploadedResumes(user), getAllResumes(user));
	}

	public ViewPageExtractedContentDTO getExtractedDetails(JwtUserDetails user, ParseText content) {
		ParseAllResponse response = jdService.extractJdDetails(content, user);
		return new ViewPageExtractedContentDTO(response.getSkills(), response.getParsed_skill_count());
	}
}
