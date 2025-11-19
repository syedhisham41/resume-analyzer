package com.resumeanalyzer.activity.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.common.enums.ActivityEnums.ActionType;
import com.resumeanalyzer.common.enums.ActivityEnums.ActivityStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity")
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@Column(name = "action_type")
	private ActionType actionType;

	@Column(name = "entity")
	private String entity;

	@Column(name = "entity_name")
	private String entityName;

	@Column(name = "details")
	private String details;

	@Column(name = "status")
	private ActivityStatus status; // e.g., SUCCESS, FAILURE

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Activity() {
	}

	public Activity(User user, ActionType actionType, String entity, String entityName, String details,
			ActivityStatus status) {
		super();
		this.user = user;
		this.actionType = actionType;
		this.entity = entity;
		this.entityName = entityName;
		this.details = details;
		this.status = status;
	}

	public Activity(ActionType actionType, String entity, String entityName, String details, ActivityStatus status) {
		super();
		this.actionType = actionType;
		this.entity = entity;
		this.entityName = entityName;
		this.details = details;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public ActivityStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
