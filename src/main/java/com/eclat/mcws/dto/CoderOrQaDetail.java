package com.eclat.mcws.dto;

import java.io.Serializable;
import java.util.List;

public class CoderOrQaDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int userId;

	private String userName;

	private String primaryClient;

	private String primaryChart;

	private String role;

	private String experience;

	private List<ChartDetail> chartDetails;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPrimaryClient() {
		return primaryClient;
	}

	public void setPrimaryClient(String primaryClient) {
		this.primaryClient = primaryClient;
	}

	public String getPrimaryChart() {
		return primaryChart;
	}

	public void setPrimaryChart(String primaryChart) {
		this.primaryChart = primaryChart;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public List<ChartDetail> getChartDetails() {
		return chartDetails;
	}

	public void setChartDetails(List<ChartDetail> chartDetails) {
		this.chartDetails = chartDetails;
	}

}
