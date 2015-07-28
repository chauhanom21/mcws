package com.eclat.mcws.dto;


public class WorkItemHistory {
	private Long workListId;
	private String coderName;
	private String status;
	private String comments;
	private String codingData;

	public Long getWorkListId() {
		return workListId;
	}

	public void setWorkListId(Long workListId) {
		this.workListId = workListId;
	}

	public String getCoderName() {
		return coderName;
	}

	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCodingData() {
		return codingData;
	}

	public void setCodingData(String codingData) {
		this.codingData = codingData;
	}

}
