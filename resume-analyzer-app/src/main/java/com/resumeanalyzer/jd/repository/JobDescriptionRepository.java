package com.resumeanalyzer.jd.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resumeanalyzer.jd.entity.JobDescription;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

	@Query("SELECT r FROM JobDescription r WHERE r.user.userId = :userId")
	List<JobDescription> findAllJdByUserId(int userId);

	void deleteAllByUser_userId(int userId);

	@Query("SELECT r FROM JobDescription r WHERE r.user.userId = :userId AND r.companyName = :companyName")
	List<JobDescription> findAllJdByCompanyName(@Param("companyName") String companyName, @Param("userId") int userId);

	@Query("SELECT r FROM JobDescription r WHERE r.user.userId = :userId AND r.title = :title")
	List<JobDescription> findAllJdByTitle(@Param("title") String title, @Param("userId") int userId);

	JobDescription findTopByUser_UserIdOrderByCreatedAtDesc(int userId);

	List<JobDescription> findTop5ByUser_UserIdOrderByCreatedAtDesc(int userId);

	@Query("SELECT j FROM JobDescription j WHERE " + "LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%')) OR "
			+ "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :q, '%'))")
	Page<JobDescription> searchByTitleOrCompany(@Param("q") String query, Pageable pageable);

}
