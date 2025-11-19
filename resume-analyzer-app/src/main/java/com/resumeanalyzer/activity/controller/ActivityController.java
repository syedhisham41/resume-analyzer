package com.resumeanalyzer.activity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.resumeanalyzer.activity.service.ActivityService;
import com.resumeanalyzer.common.dto.ActivityUploadDTO;
import com.resumeanalyzer.common.dto.JwtUserDetails;

@RestController
public class ActivityController {

	private final ActivityService activityService;

	public ActivityController(ActivityService activityService) {
		super();
		this.activityService = activityService;
	}

	@PostMapping("/api/activity/upload")
	public  ResponseEntity<Void> uploadActivity(@RequestBody ActivityUploadDTO activity, @AuthenticationPrincipal JwtUserDetails user) {
		
		if (activity.getActionType() == null || activity.getStatus() == null) {
	        return ResponseEntity.badRequest().build();
	    }
		
		activityService.record(activity.getActionType(), activity.getEntity(), activity.getEntityName(),
				activity.getDetails(), activity.getStatus(), user);
		return ResponseEntity.ok().build();
	}
}
