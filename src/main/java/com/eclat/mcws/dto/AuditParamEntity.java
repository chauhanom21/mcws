package com.eclat.mcws.dto;

public class AuditParamEntity {

	private String title;
	private String fromDate;
	private String toDate;
	private Integer userId;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "AuditParamEntity [title=" + title + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", userId=" + userId + "]";
	}
	

}
