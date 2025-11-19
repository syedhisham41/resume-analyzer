package com.resumeanalyzer.jd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.analyzer.dto.ParseAllResponse;
import com.resumeanalyzer.analyzer.dto.ParseText;
import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;
import com.resumeanalyzer.common.utils.AIUtils;
import com.resumeanalyzer.common.utils.JdServiceUtils;
import com.resumeanalyzer.common.utils.ParserUtils;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.dto.UploadJdTextRequest;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.jd.entity.JobDescriptionCandidate;
import com.resumeanalyzer.jd.exceptions.JdNotFoundException;
import com.resumeanalyzer.jd.repository.JobDescriptionCandidateRepository;
import com.resumeanalyzer.jd.repository.JobDescriptionRepository;
import com.resumeanalyzer.resume.exceptions.AccessDeniedException;

import jakarta.transaction.Transactional;

@Service
public class JdService {

	private JobDescriptionRepository repository;

	private JobDescriptionCandidateRepository jdCandidateRepository;

	private AuthDAO authDao;

	private ActivityService activityService;

	private AIUtils aiUtils;

	public JdService(JobDescriptionRepository repository, JobDescriptionCandidateRepository jdCandidateRepository,
			AuthDAO authDao, AIUtils aiUtils, ActivityService activityService) {
		super();
		this.repository = repository;
		this.authDao = authDao;
		this.aiUtils = aiUtils;
		this.jdCandidateRepository = jdCandidateRepository;
		this.activityService = activityService;
	}

	public JdResponseDTO uploadJdText(UploadJdTextRequest content, JwtUserDetails user) {

		try {
			// Step 1: Parse the JD text
			ParseAllResponse parsed = aiUtils.parseRawContent(new ParseText(content.getContent()));

			// Step 2: Extract candidates if user didn’t provide
			List<String> companyNames = (content.getCompanyName() == null || content.getCompanyName().isEmpty())
					? (parsed != null ? JdServiceUtils.extractCompanyNamesFromParsedAllJd(parsed) : new ArrayList<>())
					: content.getCompanyName();

			List<String> titles = (content.getTitle() == null || content.getTitle().isEmpty())
					? (parsed != null ? parsed.getTitles() : new ArrayList<>())
					: content.getTitle();

			// Step 3: Create base JD
			User userObject = authDao.getUserById(user.getUserId());

			JobDescription jd = new JobDescription();
			jd.setCompanyName(!companyNames.isEmpty() ? companyNames.get(0) : "Unknown Company"); // default pick
			jd.setTitle(!titles.isEmpty() ? titles.get(0) : "Untitled JD");
			jd.setParsedText(content.getContent());
			jd.setUser(userObject);

			JobDescription savedJd = repository.save(jd); // persist JD first

			// Step 4: Save company candidates
			Set<String> companySet = new HashSet<>(companyNames);
			if (companySet != null && !companySet.isEmpty()) {
				companySet.forEach(name -> {
					JobDescriptionCandidate candidate = new JobDescriptionCandidate();
					candidate.setJobDescription(savedJd);
					candidate.setType(JobDescriptionCandidate.CandidateType.COMPANY);
					candidate.setCandidateValue(name);
					candidate.setSelected(name.equals(savedJd.getCompanyName())); // mark default one as selected
					jdCandidateRepository.save(candidate);
				});
			}

			// Step 5: Save title candidates
			if (titles != null && !titles.isEmpty()) {
				titles.forEach(title -> {
					JobDescriptionCandidate candidate = new JobDescriptionCandidate();
					candidate.setJobDescription(savedJd);
					candidate.setType(JobDescriptionCandidate.CandidateType.TITLE);
					candidate.setCandidateValue(title);
					candidate.setSelected(title.equals(savedJd.getTitle()));
					jdCandidateRepository.save(candidate);
				});
			}

			activityService.record(ActionType.UPLOAD, "Jd", savedJd.getTitle() + " @ " + savedJd.getCompanyName(),
					"Jd uploaded successfully !", ActivityStatus.SUCCESS, user);
			return JdServiceUtils.mapJdtoJdResponseDTO(savedJd);
		} catch (Exception e) {
			activityService.record(ActionType.UPLOAD, "Jd", "", "Jd upload failed !", ActivityStatus.FAILURE,
					user);

			throw e;
		}
	}

	public JdResponseDTO getJdByJdId(long jdId, JwtUserDetails user) {

		JobDescription jd = repository.findById(jdId)
				.orElseThrow(() -> new JdNotFoundException("JD not found : " + jdId, null));

		if (jd.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to this JD", null);

		return JdServiceUtils.mapJdtoJdResponseDTO(jd);
	}

	public List<JdResponseDTO> getAllJdbyUser(JwtUserDetails user) {

		List<JobDescription> list = repository.findAllJdByUserId(user.getUserId());

		if (list.isEmpty()) {
			throw new JdNotFoundException("Jds not found for the user :" + user.getUserId(), null);
		}
		List<JdResponseDTO> listDto = new ArrayList<>();
		for (JobDescription Jd : list) {
			listDto.add(JdServiceUtils.mapJdtoJdResponseDTO(Jd));
		}
		return listDto;
	}

	public String deleteJdByJdId(long JdId, JwtUserDetails user) {

		try {
			JobDescription Jd = repository.findById(JdId)
					.orElseThrow(() -> new JdNotFoundException("Jd does not exist with JdId: " + JdId, null));

			if (Jd.getUser().getUserId() != user.getUserId()) {
				throw new AccessDeniedException("User doesnt have access to delete the Jd with JdId: " + JdId, null);
			}

			repository.delete(Jd);
			activityService.record(ActionType.DELETE, "Jd", Jd.getTitle() + " @ " + Jd.getCompanyName(),
					"Jd deleted successfully !", ActivityStatus.SUCCESS, user);
			return "Jd with JdId : " + JdId + " deleted successfully";
		} catch (Exception e) {
			activityService.record(ActionType.DELETE, "Jd", "", "Jd delete failed !", ActivityStatus.FAILURE,
					user);
			throw e;
		}
	}

	public void deleteJdsByUser(JwtUserDetails user) {

		List<JobDescription> list = repository.findAllJdByUserId(user.getUserId());

		if (list.isEmpty()) {
			throw new JdNotFoundException("Jds not found for the user :" + user.getUserId(), null);
		}

		repository.deleteAllByUser_userId(user.getUserId());

	}

	public List<JdResponseDTO> getJdByCompanyName(String companyName, JwtUserDetails user) {

		List<JobDescription> list = repository.findAllJdByCompanyName(companyName, user.getUserId());

		if (list.isEmpty()) {
			throw new JdNotFoundException(
					"Jds not found for the user :" + user.getUserId() + "with company : " + companyName, null);
		}
		List<JdResponseDTO> listDto = new ArrayList<>();
		for (JobDescription Jd : list) {
			listDto.add(JdServiceUtils.mapJdtoJdResponseDTO(Jd));
		}
		return listDto;
	}

	public List<JdResponseDTO> getJdByTitle(String title, JwtUserDetails user) {
		List<JobDescription> list = repository.findAllJdByTitle(title, user.getUserId());

		if (list.isEmpty()) {
			throw new JdNotFoundException("Jds not found for the user : " + user.getUserId() + " with title : " + title,
					null);
		}
		List<JdResponseDTO> listDto = new ArrayList<>();
		for (JobDescription Jd : list) {
			listDto.add(JdServiceUtils.mapJdtoJdResponseDTO(Jd));
		}
		return listDto;
	}

	public JdResponseDTO getLatestUploadedJd(JwtUserDetails user) {
		JobDescription jd = repository.findTopByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
		if (jd == null)
			throw new JdNotFoundException("Jd not found for the user : " + user.getUserId(), null);
		return JdServiceUtils.mapJdtoJdResponseDTO(jd);
	}

	public List<JdResponseDTO> getLatestFiveUploadedJds(JwtUserDetails user) {
		List<JobDescription> jdList = repository.findTop5ByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

		if (jdList.isEmpty()) {
			throw new JdNotFoundException("Jds not found for the user :" + user.getUserId(), null);
		}

		List<JdResponseDTO> listDto = new ArrayList<>();
		for (JobDescription Jd : jdList) {
			listDto.add(JdServiceUtils.mapJdtoJdResponseDTO(Jd));
		}
		return listDto;
	}

	public ParseAllResponse extractJdDetails(ParseText content, JwtUserDetails user) {
		return aiUtils.parseRawContent(content);
	}

	@Transactional
	public JdResponseDTO updateJdText(long jdId, UploadJdTextRequest content, JwtUserDetails user) {

		JobDescription jd = repository.findById(jdId)
				.orElseThrow(() -> new JdNotFoundException("JD not found : " + jdId, null));

		jd.setParsedText(content.getContent());

		ParseAllResponse parsed = aiUtils.parseRawContent(new ParseText(content.getContent()));

		// Step 2: Extract candidates if user didn’t provide
		List<String> companyNames = (content.getCompanyName() == null || content.getCompanyName().isEmpty())
				? (parsed != null ? JdServiceUtils.extractCompanyNamesFromParsedAllJd(parsed) : new ArrayList<>())
				: content.getCompanyName();

		List<String> titles = (content.getTitle() == null || content.getTitle().isEmpty())
				? (parsed != null ? parsed.getTitles() : new ArrayList<>())
				: content.getTitle();

		// Step 3: Create base JD
		jd.setCompanyName(!companyNames.isEmpty() ? companyNames.get(0) : "Unknown Company"); // default pick
		jd.setTitle(!titles.isEmpty() ? titles.get(0) : "Untitled JD");

		JobDescription savedJd = repository.save(jd); // persist JD first

		// delete current candidates
		jdCandidateRepository.deleteAllByjobDescription_Id(jdId);

		// Save new company candidates
		Set<String> companySet = new HashSet<>(companyNames);

		List<JobDescriptionCandidate> companyCandidates = companySet.stream().map(name -> {
			JobDescriptionCandidate c = new JobDescriptionCandidate();
			c.setJobDescription(savedJd);
			c.setType(JobDescriptionCandidate.CandidateType.COMPANY);
			c.setCandidateValue(name);
			c.setSelected(name.equals(savedJd.getCompanyName()));
			return c;
		}).toList();

		jdCandidateRepository.saveAll(companyCandidates);

		// Step 5: Save title candidates
		List<JobDescriptionCandidate> titleCandidates = titles.stream().map(name -> {
			JobDescriptionCandidate c = new JobDescriptionCandidate();
			c.setJobDescription(savedJd);
			c.setType(JobDescriptionCandidate.CandidateType.TITLE);
			c.setCandidateValue(name);
			c.setSelected(name.equals(savedJd.getTitle()));
			return c;
		}).toList();

		jdCandidateRepository.saveAll(titleCandidates);

		return JdServiceUtils.mapJdtoJdResponseDTO(savedJd);
	}

	public JdResponseDTO uploadJdFile(MultipartFile file, JwtUserDetails user) throws IOException, TikaException {
		String content = ParserUtils.generateTextFromFile(file);
		return uploadJdText(new UploadJdTextRequest(null, null, content), user);
	}
	
	public Page<JdResponseDTO> searchJds(String query, int page, int size, JwtUserDetails user) {
	    Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	    Page<JobDescription> jds = repository.searchByTitleOrCompany(query, p);
	    
	    return jds.map(JdServiceUtils::mapJdtoJdResponseDTO);
	}

}
