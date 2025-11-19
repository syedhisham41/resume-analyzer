package com.resumeanalyzer.jd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resumeanalyzer.jd.entity.JobDescriptionCandidate;

@Repository
public interface JobDescriptionCandidateRepository extends JpaRepository<JobDescriptionCandidate, Long> {

	List<JobDescriptionCandidate> findByjobDescription_Id(long jdId);

	void deleteAllByjobDescription_Id(long jdId);

}
