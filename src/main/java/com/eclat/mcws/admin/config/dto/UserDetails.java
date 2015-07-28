package com.eclat.mcws.admin.config.dto;

import java.util.List;

import com.eclat.mcws.persistence.entity.ClientDetails;

public class UserDetails {

	private Integer userId;
	private String firstname;
	private String lastname;
	private String username;
	private String password;
	private String location;
	private String jobLevel;
	private String role;
	private String gender;
	private Boolean newCoder;
	private Boolean coder;
	private Boolean localQA;
	private Boolean remoteQA;
	private Integer maxWorkload;
	private String employeeId;

	private List<ClientDetails> clientDetails;
	private List<ChartSpeDetails> chartSpeDetails;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJobLevel() {
		return jobLevel;
	}

	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Boolean getNewCoder() {
		return newCoder;
	}

	public void setNewCoder(Boolean newCoder) {
		this.newCoder = newCoder;
	}

	public Boolean getCoder() {
		return coder;
	}

	public void setCoder(Boolean coder) {
		this.coder = coder;
	}

	public Boolean getLocalQA() {
		return localQA;
	}

	public void setLocalQA(Boolean localQA) {
		this.localQA = localQA;
	}

	public Boolean getRemoteQA() {
		return remoteQA;
	}

	public void setRemoteQA(Boolean remoteQA) {
		this.remoteQA = remoteQA;
	}

	public Integer getMaxWorkload() {
		return maxWorkload;
	}

	public void setMaxWorkload(Integer maxWorkload) {
		this.maxWorkload = maxWorkload;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public List<ClientDetails> getClientDetails() {
		return clientDetails;
	}

	public void setClientDetails(List<ClientDetails> clientDetails) {
		this.clientDetails = clientDetails;
	}

	public List<ChartSpeDetails> getChartSpeDetails() {
		return chartSpeDetails;
	}

	public void setChartSpeDetails(List<ChartSpeDetails> chartSpeDetails) {
		this.chartSpeDetails = chartSpeDetails;
	}

	@Override
	public String toString() {
		return "UserDetails [userId=" + userId + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", username=" + username
				+ ", password=" + password + ", location=" + location
				+ ", jobLevel=" + jobLevel + ", role=" + role + ", gender="
				+ gender + ", newCoder=" + newCoder + ", coder=" + coder
				+ ", localQA=" + localQA + ", remoteQA=" + remoteQA
				+ ", maxWorkload=" + maxWorkload + "]";
	}

}
