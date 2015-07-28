package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coding_charts")
public class CodingCharts {
	@Id
	private long id;

	@Column(name = "worklist_id")
	private long worklistId;

	@Column(name = "client")
	private String client;

	@Column(name = "chart_type")
	private String chartType;

	@Column(name = "coding_data")
	private String codingData;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWorklistId() {
		return worklistId;
	}

	public void setWorklistId(long worklistId) {
		this.worklistId = worklistId;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getCodingData() {
		return codingData;
	}

	public void setCodingData(String codingData) {
		this.codingData = codingData;
	}

}
