package com.resumeanalyzer.common.dto;

public class GuestUserDetails {

	private String guestId;

	private String role;

	public GuestUserDetails() {
	}

	public GuestUserDetails(String guestId, String role) {
		super();
		this.guestId = guestId;
		this.role = role;
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
