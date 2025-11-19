package com.resumeanalyzer.common.dto;

import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;

public class ActivityUploadDTO {

	private ActionType actionType;

	private String entity;

	private String entityName;

	private String details;

	private ActivityStatus status;

	public ActivityUploadDTO() {
	}

	public ActivityUploadDTO(ActionType actionType, String entity, String entityName, String details,
			ActivityStatus status) {
		super();
		this.actionType = actionType;
		this.entity = entity;
		this.entityName = entityName;
		this.details = details;
		this.status = status;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public ActivityStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
