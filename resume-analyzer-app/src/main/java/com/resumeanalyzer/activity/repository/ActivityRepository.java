package com.resumeanalyzer.activity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resumeanalyzer.activity.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findTop10ByUser_UserIdOrderByCreatedAtDesc(int userId);
}
