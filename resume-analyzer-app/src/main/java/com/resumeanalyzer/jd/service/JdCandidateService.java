package com.resumeanalyzer.jd.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.utils.JdServiceUtils;
import com.resumeanalyzer.jd.dto.JdCandidateResponseDTO;
import com.resumeanalyzer.jd.dto.JdResponseDTO;
import com.resumeanalyzer.jd.dto.SelectJdValueRequest;
import com.resumeanalyzer.jd.entity.JobDescription;
import com.resumeanalyzer.jd.entity.JobDescriptionCandidate;
import com.resumeanalyzer.jd.entity.JobDescriptionCandidate.CandidateType;
import com.resumeanalyzer.jd.exceptions.AccessDeniedException;
import com.resumeanalyzer.jd.exceptions.JdNotFoundException;
import com.resumeanalyzer.jd.repository.JobDescriptionCandidateRepository;
import com.resumeanalyzer.jd.repository.JobDescriptionRepository;

import jakarta.transaction.Transactional;

@Service
public class JdCandidateService {

	private JobDescriptionCandidateRepository jdCandidateRepository;

	private JobDescriptionRepository jdRepository;

	public JdCandidateService(JobDescriptionCandidateRepository jdCandidateRepository,
			JobDescriptionRepository jdRepository) {
		super();
		this.jdCandidateRepository = jdCandidateRepository;
		this.jdRepository = jdRepository;
	}

	public List<JdCandidateResponseDTO> getCandidates(long jdId, JwtUserDetails user) {

		JobDescription jd = jdRepository.findById(jdId)
				.orElseThrow(() -> new JdNotFoundException("JD not found : " + jdId, null));

		if (jd.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to this JD", null);

		List<JobDescriptionCandidate> response = jdCandidateRepository.findByjobDescription_Id(jdId);
		List<JdCandidateResponseDTO> candidateResponse = new ArrayList<>();

		for (JobDescriptionCandidate candidate : response) {
			candidateResponse.add(JdServiceUtils.mapJdCandidateToCandidateResponseDTO(candidate));
		}
		return candidateResponse;

	}

	@Transactional
	public JdResponseDTO selectJdValueFromCandidates(long jdId, SelectJdValueRequest jdValueRequest,
			JwtUserDetails user) {

		JobDescription jd = jdRepository.findById(jdId)
				.orElseThrow(() -> new JdNotFoundException("JD not found : " + jdId, null));

		if (jd.getUser().getUserId() != user.getUserId())
			throw new AccessDeniedException("User does not have access to this JD", null);

		if (jdValueRequest.getJdValueType() == CandidateType.COMPANY) {
			jd.setCompanyName(jdValueRequest.getSelectedJdValue());
		} else if (jdValueRequest.getJdValueType() == CandidateType.TITLE) {
			jd.setTitle(jdValueRequest.getSelectedJdValue());
		}

		jd = jdRepository.save(jd);

		// update the isSelected value in candidates Repository
		List<JobDescriptionCandidate> jdCandidateList = jdCandidateRepository.findByjobDescription_Id(jdId);

		jdCandidateList.stream().filter(candidate -> candidate.getType().equals(jdValueRequest.getJdValueType()))
				.forEach(candidate -> candidate
						.setSelected(jdValueRequest.getSelectedJdValue().equals(candidate.getCandidateValue())));
		
		jdCandidateRepository.saveAll(jdCandidateList);

		return JdServiceUtils.mapJdtoJdResponseDTO(jd);

	}
}
