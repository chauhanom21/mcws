package com.eclat.mcws.dto;

import org.json.simple.JSONArray;

public class DashboardBean {
	private Double totalEM;
	private Double totalCodersEM;
	private JSONArray capacityArray;
	private JSONArray workArray;

	public Double getTotalEM() {
		return totalEM;
	}

	public void setTotalEM(Double totalEM) {
		this.totalEM = totalEM;
	}

	public Double getTotalCodersEM() {
		return totalCodersEM;
	}

	public void setTotalCodersEM(Double totalCodersEM) {
		this.totalCodersEM = totalCodersEM;
	}

	public JSONArray getCapacityArray() {
		return capacityArray;
	}

	public void setCapacityArray(JSONArray capacityArray) {
		this.capacityArray = capacityArray;
	}

	public JSONArray getWorkArray() {
		return workArray;
	}

	public void setWorkArray(JSONArray workArray) {
		this.workArray = workArray;
	}

}