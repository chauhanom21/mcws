package com.eclat.mcws.dto;

public class UserDetails {

	private int id;

	private String username;

	private String role;

	private String message;

	public UserDetails(final int id, final String username, final String role,
			final String message) {
		this.id = id;
		this.username = username;
		this.role = role;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
