package com.resumeanalyzer.resume.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.utils.ParserUtils;
import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.resume.dto.UploadResumeTextRequest;
import com.resumeanalyzer.resume.service.ResumeService;

import jakarta.transaction.Transactional;

@RestController
public class ResumeController {

	private ResumeService service;

	public ResumeController(ResumeService service) {
		this.service = service;
	}

	// UploadResume()

	@PostMapping("/api/resume/upload")
	public ResponseEntity<ResumeResponseDTO> uploadTextResume(@RequestBody UploadResumeTextRequest content,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadResume(content, user));
	}

	// getResume()

	@GetMapping("/api/resume/get")
	public ResponseEntity<ResumeResponseDTO> getResume(@RequestParam long resumeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getResumeByResumeId(resumeId, user));
	}

	// getAllResumeByUser()

	@GetMapping("/api/resume/getall")
	public ResponseEntity<List<ResumeResponseDTO>> getAllResumeByUser(@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getAllResumebyUser(user));
	}

	// deleteResume()
	@Transactional
	@DeleteMapping("/api/resume/delete")
	public ResponseEntity<Void> deleteResumeByResumeId(@RequestParam long resumeId,
			@AuthenticationPrincipal JwtUserDetails user) {
		service.deleteResumeByResumeId(resumeId, user);
		return ResponseEntity.noContent().build();
	}

	// deleteallResumeByUser()
	@Transactional
	@DeleteMapping("/api/resume/deleteall")
	public ResponseEntity<Void> deleteResumeByUser(@AuthenticationPrincipal JwtUserDetails user) {
		service.deleteResumesByUser(user);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/resume/download")
	public ResponseEntity<ByteArrayResource> downloadResume(@RequestParam long resumeId,
			@RequestParam(required = false, defaultValue = "txt") String format,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException {
		ResumeResponseDTO resume = service.getResumeByResumeId(resumeId, user);
		byte[] contentBytes = null;

		if ("pdf".equalsIgnoreCase(format)) {
			contentBytes = ParserUtils.generatePdfFromText(resume.getContent());
		} else if ("docx".equalsIgnoreCase(format)) {
//            contentBytes = Jd.getContent().getBytes(StandardCharsets.UTF_8);
		} else {
			contentBytes = resume.getContent().getBytes(StandardCharsets.UTF_8);
		}

		ByteArrayResource resource = new ByteArrayResource(contentBytes);

		MediaType contentType = switch (format.toLowerCase()) {
		case "pdf" -> MediaType.APPLICATION_PDF;
		case "docx" ->
			MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		default -> MediaType.TEXT_PLAIN;
		};

		String safeFileName = resume.getTitle().replaceAll("[^a-zA-Z0-9._-]", "_");

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + safeFileName + "." + format)
				.contentType(contentType).contentLength(contentBytes.length).body(resource);
	}

	@PostMapping("/api/resume/uploadfile")
	public ResponseEntity<ResumeResponseDTO> uploadResumeFile(@RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadResumeFile(file, user));
	}

	@GetMapping("/api/resume/search")
	public ResponseEntity<Page<ResumeResponseDTO>> searchResumeByQuery(@RequestParam String query,
			@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "20", required = false) int size,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.searchResumes(query, page, size, user));

	}

}
