package com.resumeanalyzer.jd.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.utils.ParserUtils;
import com.resumeanalyzer.jd.dto.JdCandidateResponseDTO;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.dto.SelectJdValueRequest;
import com.resumeanalyzer.jd.dto.UploadJdTextRequest;
import com.resumeanalyzer.jd.service.JdCandidateService;
import com.resumeanalyzer.jd.service.JdService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
public class JdController {

	private JdService service;

	private JdCandidateService candidateService;

	public JdController(JdService service, JdCandidateService candidateService) {
		this.service = service;
		this.candidateService = candidateService;
	}

	@PostMapping("/api/jd/upload")
	public ResponseEntity<JdResponseDTO> uploadJd(@Valid @RequestBody UploadJdTextRequest content,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadJdText(content, user));
	}

	// API to get all candidates for a JD
	@GetMapping("/api/jd/{jdId}/candidates")
	public ResponseEntity<List<JdCandidateResponseDTO>> getJdCandidates(@PathVariable long jdId,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(candidateService.getCandidates(jdId, user));
	}

	@PostMapping("/api/jd/{jdId}/select")
	public ResponseEntity<JdResponseDTO> selectCandidateForJd(@PathVariable long jdId,
			@RequestBody SelectJdValueRequest jdSelectRequest, @AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(candidateService.selectJdValueFromCandidates(jdId, jdSelectRequest, user));

	}

	// getJdById
	@GetMapping("/api/jd/get")
	public ResponseEntity<JdResponseDTO> getJd(@RequestParam long jdId, @AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getJdByJdId(jdId, user));
	}

	// getJdByCompanyName()
	@GetMapping("/api/jd/company/{companyName}")
	public ResponseEntity<List<JdResponseDTO>> getJdByCompanyName(@PathVariable String companyName,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getJdByCompanyName(companyName, user));
	}

	// getJdByTitle()
	@GetMapping("/api/jd/title/{title}")
	public ResponseEntity<List<JdResponseDTO>> getJdByTitle(@PathVariable String title,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getJdByTitle(title, user));
	}

	@GetMapping("/api/jd/getall")
	public ResponseEntity<List<JdResponseDTO>> getAllJd(@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.getAllJdbyUser(user));
	}

	// deleteJd()
	@Transactional
	@DeleteMapping("/api/jd/delete")
	public ResponseEntity<Void> deleteJdByJdId(@RequestParam long jdId, @AuthenticationPrincipal JwtUserDetails user) {
		service.deleteJdByJdId(jdId, user);
		return ResponseEntity.noContent().build();
	}

	// deleteallJdByUser()
	@Transactional
	@DeleteMapping("/api/jd/deleteall")
	public ResponseEntity<Void> deleteJdByUser(@AuthenticationPrincipal JwtUserDetails user) {
		service.deleteJdsByUser(user);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/Jd/download")
	public ResponseEntity<ByteArrayResource> downloadJd(@RequestParam long JdId,
			@RequestParam(required = false, defaultValue = "txt") String format,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException {

		JdResponseDTO Jd = service.getJdByJdId(JdId, user);
		byte[] contentBytes = null;

		if ("pdf".equalsIgnoreCase(format)) {
			contentBytes = ParserUtils.generatePdfFromText(Jd.getContent());
		} else if ("docx".equalsIgnoreCase(format)) {
//            contentBytes = Jd.getContent().getBytes(StandardCharsets.UTF_8);
		} else {
			contentBytes = Jd.getContent().getBytes(StandardCharsets.UTF_8);
		}

		ByteArrayResource resource = new ByteArrayResource(contentBytes);

		MediaType contentType = switch (format.toLowerCase()) {
		case "pdf" -> MediaType.APPLICATION_PDF;
		case "docx" ->
			MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		default -> MediaType.TEXT_PLAIN;
		};

		String safeFileName = Jd.getTitle().replaceAll("[^a-zA-Z0-9._-]", "_");

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + safeFileName + "." + format)
				.contentType(contentType).contentLength(contentBytes.length).body(resource);
	}

	@PostMapping("/api/jd/uploadfile")
	public ResponseEntity<JdResponseDTO> uploadJdFile(@RequestParam MultipartFile file,
			@AuthenticationPrincipal JwtUserDetails user) throws IOException, TikaException {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadJdFile(file, user));
	}

	@GetMapping("/api/jd/search")
	public ResponseEntity<Page<JdResponseDTO>> searchJdByQuery(@RequestParam String query,
			@RequestParam(defaultValue = "0", required = false) int page,
			@RequestParam(defaultValue = "20", required = false) int size,
			@AuthenticationPrincipal JwtUserDetails user) {
		return ResponseEntity.ok(service.searchJds(query, page, size, user));

	}

}
