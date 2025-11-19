package com.resumeanalyzer.ui.controller;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.ui.service.UploadPageService;

import jakarta.validation.Valid;

@RestController
public class UploadPageController {

	private final UploadPageService uploadPageService;

	public UploadPageController(UploadPageService uploadPageService) {
		super();
		this.uploadPageService = uploadPageService;
	}

	@PostMapping("/api/uploadpage/jd")
	public JdResponseDTO uploadJdText(@Valid @RequestBody String content,
			@AuthenticationPrincipal JwtUserDetails user) {

		return uploadPageService.readAndUploadJdText(content, user);
	}

	@PostMapping("/api/uploadpage/jdpdf")
	public JdResponseDTO uploadJdPdf(@Valid @RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {

		return uploadPageService.readAndUploadJdFile(file, user);
	}

	@PostMapping("/api/uploadpage/jddocx")
	public JdResponseDTO uploadJdDocx(@Valid @RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {

		return uploadPageService.readAndUploadJdFile(file, user);
	}

	@PutMapping("/api/uploadpage/jd/{jdId}")
	public JdResponseDTO updateJdText(@PathVariable long jdId, @Valid @RequestBody String content,
			@AuthenticationPrincipal JwtUserDetails user) {

		return uploadPageService.updateAndUploadJdText(jdId, content, user);
	}

	@PostMapping("/api/uploadpage/resume")
	public ResumeResponseDTO uploadResumeText(@Valid @RequestBody String content,
			@AuthenticationPrincipal JwtUserDetails user) {

		return uploadPageService.readAndUploadResumeText(content, user);
	}

	@PostMapping("/api/uploadpage/resumepdf")
	public ResumeResponseDTO uploadResumePdf(@Valid @RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {

		return uploadPageService.readAndUploadResumeFile(file, user);
	}

	@PostMapping("/api/uploadpage/resumedocx")
	public ResumeResponseDTO uploadResumeDocx(@Valid @RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {

		return uploadPageService.readAndUploadResumeFile(file, user);
	}

	@PutMapping("/api/uploadpage/resume/{resumeId}")
	public ResumeResponseDTO updateResumeText(@PathVariable long resumeId, @Valid @RequestBody String content,
			@AuthenticationPrincipal JwtUserDetails user) {

		return uploadPageService.updateAndUploadResumeText(resumeId, content, user);
	}

	@PutMapping("/api/uploadpage/resume/{resumeId}/title")
	public ResumeResponseDTO updateResumeTitle(@PathVariable long resumeId, @Valid @RequestBody String title,
			@AuthenticationPrincipal JwtUserDetails user) {

		return uploadPageService.updateResumeTitle(resumeId, title, user);
	}
}
