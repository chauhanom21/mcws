package com.eclat.mcws.dto;

import org.json.simple.JSONObject;

public class GradingSheetDetails {

	private Long worklistId;
	private Integer clientId;
	private String client;
	private Integer chartSpecId;
	private String specilization;
	private String chartType;
	private String accountNumber;
	private String mrNumber;
	private Double totalAccuracy;
	private Boolean drg;
	private JSONObject gradingsheet;
	private Integer userCompleted;
	private Integer userAudited;
	
	public Long getWorklistId() {
		return worklistId;
	}

	public void setWorklistId(Long worklistId) {
		this.worklistId = worklistId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Integer getChartSpecId() {
		return chartSpecId;
	}

	public void setChartSpecId(Integer chartSpecId) {
		this.chartSpecId = chartSpecId;
	}

	public String getSpecilization() {
		return specilization;
	}

	public void setSpecilization(String specilization) {
		this.specilization = specilization;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getMrNumber() {
		return mrNumber;
	}

	public void setMrNumber(String mrNumber) {
		this.mrNumber = mrNumber;
	}

	public Double getTotalAccuracy() {
		return totalAccuracy;
	}

	public void setTotalAccuracy(Double totalAccuracy) {
		this.totalAccuracy = totalAccuracy;
	}

	public Boolean getDrg() {
		return drg;
	}

	public void setDrg(Boolean drg) {
		this.drg = drg;
	}

	public JSONObject getGradingsheet() {
		return gradingsheet;
	}

	public void setGradingsheet(JSONObject gradingsheet) {
		this.gradingsheet = gradingsheet;
	}

	public Integer getUserCompleted() {
		return userCompleted;
	}

	public void setUserCompleted(Integer userCompleted) {
		this.userCompleted = userCompleted;
	}

	public Integer getUserAudited() {
		return userAudited;
	}

	public void setUserAudited(Integer userAudited) {
		this.userAudited = userAudited;
	}

	@Override
	public String toString() {
		return "GradingSheetDetails [worklistId=" + worklistId + ", clientId="
				+ clientId + ", client=" + client + ", chartSpecId="
				+ chartSpecId + ", specilization=" + specilization
				+ ", chartType=" + chartType + ", accountNumber="
				+ accountNumber + ", mrNumber=" + mrNumber + ", totalAccuracy="
				+ totalAccuracy + ", drg=" + drg + ", gradingsheet="
				+ gradingsheet + ", userCompleted=" + userCompleted
				+ ", userAudited=" + userAudited + "]";
	}

}
