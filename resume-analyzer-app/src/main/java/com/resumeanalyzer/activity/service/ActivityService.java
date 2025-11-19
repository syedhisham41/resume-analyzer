package com.resumeanalyzer.activity.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.resumeanalyzer.activity.entity.Activity;
import com.resumeanalyzer.activity.repository.ActivityRepository;
import com.resumeanalyzer.auth.dao.AuthDAO;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;

@Service
public class ActivityService {

	private final ActivityRepository repository;

	private final AuthDAO authDao;

	public ActivityService(ActivityRepository repository, AuthDAO authDao) {
		super();
		this.repository = repository;
		this.authDao = authDao;
	}

	@Async
	public void record(ActionType actionType, String entity, String entityName, String details, ActivityStatus status,
			JwtUserDetails user) {

		User userObject = authDao.getUserById(user.getUserId());

		if (userObject == null) {
			throw new IllegalArgumentException("User not found for ID: " + user.getUserId());
		}

		Activity activity = new Activity(userObject, actionType, entity, entityName, details, status);
		repository.save(activity);
	}

	@Async
	// for frontend calls
	public void record(ActionType actionType, String entity, String entityName, String details, ActivityStatus status) {
		Activity activity = new Activity(actionType, entity, entityName, details, status);
		repository.save(activity);
	}

	public List<Activity> getLatest10Records(JwtUserDetails user) {
		return repository.findTop10ByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
	}
}
