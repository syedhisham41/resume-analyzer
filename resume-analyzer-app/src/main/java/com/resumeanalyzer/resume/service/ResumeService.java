package com.resumeanalyzer.resume.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;
import com.resumeanalyzer.common.utils.ParserUtils;
import com.resumeanalyzer.common.utils.ResumeServiceUtils;
import com.resumeanalyzer.resume.dto.ResumeResponseDTO;
import com.resumeanalyzer.resume.dto.UploadResumeTextRequest;
import com.resumeanalyzer.resume.entity.Resume;
import com.resumeanalyzer.resume.exceptions.AccessDeniedException;
import com.resumeanalyzer.resume.exceptions.ResumeNotFoundException;
import com.resumeanalyzer.resume.repository.ResumeRepository;

@Service
public class ResumeService {

	private final ResumeRepository repository;

	private final AuthDAO authDao;

	private final ActivityService activityService;

	public ResumeService(ResumeRepository repository, AuthDAO authDao, ActivityService activityService) {
		this.repository = repository;
		this.authDao = authDao;
		this.activityService = activityService;
	}

	public ResumeResponseDTO uploadResume(UploadResumeTextRequest content, JwtUserDetails user) {

		try {
			User userObject = authDao.getUserById(user.getUserId());
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			Resume resume = new Resume();
			resume.setParsedText(content.getContent());
			resume.setTitle("Resume_" + timestamp);
			resume.setUser(userObject);

			resume = repository.save(resume);
			activityService.record(ActionType.UPLOAD, "Resume", resume.getTitle(), "Resume uploaded successfully !",
					ActivityStatus.SUCCESS, user);
			return ResumeServiceUtils.mapResumeToResumeDTO(resume);
		} catch (Exception e) {
			activityService.record(ActionType.UPLOAD, "Resume", "", "Resume upload failed !", ActivityStatus.FAILURE,
					user);
			throw e;
		}

	}

	public ResumeResponseDTO uploadResumeFile(MultipartFile file, JwtUserDetails user)
			throws IOException, TikaException {

		try {
			User userObject = authDao.getUserById(user.getUserId());
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

			String content = ParserUtils.generateTextFromFile(file);
			Resume resume = new Resume();
			resume.setParsedText(content);
			resume.setTitle("Resume_" + timestamp);
			resume.setUser(userObject);

			resume = repository.save(resume);
			activityService.record(ActionType.UPLOAD, "Resume", resume.getTitle(), "Resume uploaded successfully !",
					ActivityStatus.SUCCESS, user);
			return ResumeServiceUtils.mapResumeToResumeDTO(resume);
		} catch (Exception e) {
			activityService.record(ActionType.UPLOAD, "Resume", "", "Resume upload failed !", ActivityStatus.FAILURE,
					user);
			throw e;
		}

	}

	public ResumeResponseDTO updateResume(long resumeId, UploadResumeTextRequest content, JwtUserDetails user) {

		Resume resume = repository.findById(resumeId)
				.orElseThrow(() -> new ResumeNotFoundException("Resume not found : " + resumeId, null));

		if (resume.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to update this resume", null);

		resume.setParsedText(content.getContent());
		return ResumeServiceUtils.mapResumeToResumeDTO(repository.save(resume));

	}

	public ResumeResponseDTO updateResumeTitle(long resumeId, String title, JwtUserDetails user) {

		Resume resume = repository.findById(resumeId)
				.orElseThrow(() -> new ResumeNotFoundException("Resume not found : " + resumeId, null));

		if (resume.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to update this resume", null);

		resume.setTitle(title);
		return ResumeServiceUtils.mapResumeToResumeDTO(repository.save(resume));

	}

	public ResumeResponseDTO getResumeByResumeId(long resumeId, JwtUserDetails user) {

		Resume resume = repository.findById(resumeId)
				.orElseThrow(() -> new ResumeNotFoundException("Resume not found : " + resumeId, null));

		if (resume.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to this resume", null);

		return ResumeServiceUtils.mapResumeToResumeDTO(resume);
	}

	public List<ResumeResponseDTO> getAllResumebyUser(JwtUserDetails user) {

		List<Resume> list = repository.findAllResumeByUserId(user.getUserId());

		if (list.isEmpty()) {
			throw new ResumeNotFoundException("resumes not found for the user :" + user.getUserId(), null);
		}
		List<ResumeResponseDTO> listDto = new ArrayList<>();
		for (Resume resume : list) {
			listDto.add(ResumeServiceUtils.mapResumeToResumeDTO(resume));
		}
		return listDto;
	}

	public String deleteResumeByResumeId(long resumeId, JwtUserDetails user) {

		try {
			Resume resume = repository.findById(resumeId).orElseThrow(
					() -> new ResumeNotFoundException("resume does not exist with resumeId: " + resumeId, null));
			if (resume.getUser().getUserId() != user.getUserId()) {
				throw new AccessDeniedException(
						"User doesnt have access to delete the resume with resumeId: " + resumeId, null);
			}

			repository.delete(resume);
			activityService.record(ActionType.DELETE, "Resume", resume.getTitle(), "Resume deleted successfully !",
					ActivityStatus.SUCCESS, user);
			return "resume with resumeId : " + resumeId + " deleted successfully";
		} catch (Exception e) {
			activityService.record(ActionType.DELETE, "Resume", "", "Resume delete failed !", ActivityStatus.FAILURE,
					user);
			throw e;
		}
	}

	public void deleteResumesByUser(JwtUserDetails user) {

		List<Resume> list = repository.findAllResumeByUserId(user.getUserId());
		if (list.isEmpty()) {
			throw new ResumeNotFoundException("resumes not found for the user :" + user.getUserId(), null);
		}

		repository.deleteAllByUser_userId(user.getUserId());

	}

	public ResumeResponseDTO getLatestUploadedResume(JwtUserDetails user) {
		Resume resume = repository.findTopByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
		if (resume == null)
			throw new ResumeNotFoundException("Resume not found for the user : " + user.getUserId(), null);
		return ResumeServiceUtils.mapResumeToResumeDTO(resume);
	}

	public List<ResumeResponseDTO> getLatestFiveUploadedResumes(JwtUserDetails user) {
		List<Resume> resumeList = repository.findTop5ByUser_UserIdOrderByCreatedAtDesc(user.getUserId());

		if (resumeList.isEmpty()) {
			throw new ResumeNotFoundException("Resumes not found for the user :" + user.getUserId(), null);
		}

		List<ResumeResponseDTO> listDto = new ArrayList<>();
		for (Resume resume : resumeList) {
			listDto.add(ResumeServiceUtils.mapResumeToResumeDTO(resume));
		}
		return listDto;
	}

	public Page<ResumeResponseDTO> searchResumes(String query, int page, int size, JwtUserDetails user) {
		Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Resume> resumes = repository.searchByTitle(query, p);

		return resumes.map(ResumeServiceUtils::mapResumeToResumeDTO);
	}

}
