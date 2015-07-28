package com.eclat.mcws.dto;

public class UserProfile {

	private Integer userId;
	private String userRole;
	private String name;
	private String remWorkload;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemWorkload() {
		return remWorkload;
	}

	public void setRemWorkload(String remWorkload) {
		this.remWorkload = remWorkload;
	}

}
