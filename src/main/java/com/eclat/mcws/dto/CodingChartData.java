package com.eclat.mcws.dto;

import org.json.simple.JSONObject;

public class CodingChartData {

	private String insurance;
	private JSONObject codingData;
	private boolean tempData;
	private String currentStatus;

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public JSONObject getCodingData() {
		return codingData;
	}

	public void setCodingData(JSONObject codingData) {
		this.codingData = codingData;
	}

	public boolean isTempData() {
		return tempData;
	}

	public void setTempData(boolean tempData) {
		this.tempData = tempData;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

}
