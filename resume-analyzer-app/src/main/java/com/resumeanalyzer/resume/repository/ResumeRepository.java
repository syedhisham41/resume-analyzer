package com.resumeanalyzer.resume.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resumeanalyzer.resume.entity.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

	@Query("SELECT r FROM Resume r WHERE r.user.userId = :userId")
	List<Resume> findAllResumeByUserId(int userId);

	void deleteAllByUser_userId(int userId);

	Resume findTopByUser_UserIdOrderByCreatedAtDesc(int userId);

	List<Resume> findTop5ByUser_UserIdOrderByCreatedAtDesc(int userId);

	@Query("SELECT r FROM Resume r WHERE " + "LOWER(r.title) LIKE LOWER(CONCAT('%', :q, '%')) ")
	Page<Resume> searchByTitle(@Param("q") String query, Pageable p);
}
