package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grading_sheet")
public class GradingSheet implements com.eclat.mcws.persistence.entity.Entity {

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "user_completed")
	private int userCompleted;
	
	@Column(name = "user_audited")
	private int userAudited;

	@Column(name = "worklist_id")
	private long worklistId;

	@Column(name = "client_id")
	private Integer clientId;

	@Column(name = "chart_specialization")
	private Integer chartSpecializationId;

	@Column
	private Boolean drg;

	@Column
	private Double accuracy;

	@Column(name = "grading_sheet_data")
	private String gradingSheetData;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserCompleted() {
		return userCompleted;
	}

	public void setUserCompleted(int userCompleted) {
		this.userCompleted = userCompleted;
	}

	public int getUserAudited() {
		return userAudited;
	}

	public void setUserAudited(int userAudited) {
		this.userAudited = userAudited;
	}

	public long getWorklistId() {
		return worklistId;
	}

	public void setWorklistId(long worklistId) {
		this.worklistId = worklistId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getChartSpecializationId() {
		return chartSpecializationId;
	}

	public void setChartSpecializationId(Integer chartSpecializationId) {
		this.chartSpecializationId = chartSpecializationId;
	}

	public Boolean getDrg() {
		return drg;
	}

	public void setDrg(Boolean drg) {
		this.drg = drg;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public String getGradingSheetData() {
		return gradingSheetData;
	}

	public void setGradingSheetData(String gradingSheetData) {
		this.gradingSheetData = gradingSheetData;
	}

}
