package com.resumeanalyzer.analyzer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resumeanalyzer.analyzer.entity.Analyze;

@Repository
public interface AnalyzeRepository extends JpaRepository<Analyze, Long> {

	List<Analyze> findByUser_UserId(int userId);

	Analyze findTopByUser_UserIdOrderByCreatedAtDesc(int userId);

	List<Analyze> findTop5ByUser_UserIdOrderByCreatedAtDesc(int userId);

}
