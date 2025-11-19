package com.resumeanalyzer.auth.dto;

import java.util.Date;

public class Jwt {

	private String token;

	private Date createdAt;

	private Date expiryAt;

	public Jwt(String token, Date createdAt, Date expiryAt) {
		super();
		this.token = token;
		this.createdAt = createdAt;
		this.expiryAt = expiryAt;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getExpiryAt() {
		return expiryAt;
	}

	public void setExpiryAt(Date expiryAt) {
		this.expiryAt = expiryAt;
	}

}
