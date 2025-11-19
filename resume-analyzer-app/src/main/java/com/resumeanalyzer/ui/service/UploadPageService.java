package com.resumeanalyzer.ui.service;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.dto.UploadJdTextRequest;
import com.resumeanalyzer.jd.service.JdService;
import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.resume.dto.UploadResumeTextRequest;
import com.resumeanalyzer.resume.service.ResumeService;

@Service
public class UploadPageService {

	private final JdService jdService;

	private final ResumeService resumeService;

	public UploadPageService(JdService jdService, ResumeService resumeService) {
		super();
		this.jdService = jdService;
		this.resumeService = resumeService;
	}

	public JdResponseDTO readAndUploadJdText(String content, JwtUserDetails user) {
		return jdService.uploadJdText(new UploadJdTextRequest(null, null, content), user);
	}

	public JdResponseDTO updateAndUploadJdText(long jdId, String content, JwtUserDetails user) {
		return jdService.updateJdText(jdId, new UploadJdTextRequest(null, null, content), user);
	}

	public ResumeResponseDTO readAndUploadResumeText(String content, JwtUserDetails user) {
		return resumeService.uploadResume(new UploadResumeTextRequest(content), user);
	}

	public ResumeResponseDTO updateAndUploadResumeText(long resumeId, String content, JwtUserDetails user) {
		return resumeService.updateResume(resumeId, new UploadResumeTextRequest(content), user);
	}

	public ResumeResponseDTO updateResumeTitle(long resumeId, String title, JwtUserDetails user) {
		return resumeService.updateResumeTitle(resumeId, title, user);
	}

	public ResumeResponseDTO readAndUploadResumeFile(MultipartFile file, JwtUserDetails user)
			throws IOException, TikaException {
		return resumeService.uploadResumeFile(file, user);
	}

	public JdResponseDTO readAndUploadJdFile(MultipartFile file, JwtUserDetails user)
			throws IOException, TikaException {
		return jdService.uploadJdFile(file, user);
	}

}
