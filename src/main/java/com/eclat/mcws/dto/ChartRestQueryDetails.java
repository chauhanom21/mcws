package com.eclat.mcws.dto;

import java.util.List;

public class ChartRestQueryDetails {

	private Integer userId;
	private boolean coderTask;
	private Integer clientId;
	private Integer coderId;
	private Long taskId;
	private String workType;
	private String startDate;
	private String endDate;
	private String status;
	private List<Long> taskIds;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public boolean isCoderTask() {
		return coderTask;
	}

	public void setCoderTask(boolean coderTask) {
		this.coderTask = coderTask;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public Integer getCoderId() {
		return coderId;
	}

	public void setCoderId(Integer coderId) {
		this.coderId = coderId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Long> getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}

	@Override
	public String toString() {
		return "ChartRestQueryDetails [userId=" + userId + ", coderTask="
				+ coderTask + "]";
	}

}
