package com.eclat.mcws.report.bean;

public class LocalQADailyProductivityBean {

	private String empcode;
	private String empname;
	private String location;
	private String clientName;
	private String chartType;
	private String chartSpecialization;
	private int cnrFileCount;
	private String cnrHours = "0";
	private int reviewFilecount;
	private String reviewFileHours = "0";
	private int auditsFileCount;
	private String auditsFileHours = "0";
	private long totalCharts;
	private String totalHours = "0";
	private String avgHours = "0";

	public String getEmpcode() {
		return empcode;
	}

	public void setEmpcode(String empcode) {
		this.empcode = empcode;
	}

	public String getEmpname() {
		return empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getChartSpecialization() {
		return chartSpecialization;
	}

	public void setChartSpecialization(String chartSpecialization) {
		this.chartSpecialization = chartSpecialization;
	}

	public int getCnrFileCount() {
		return cnrFileCount;
	}

	public void setCnrFileCount(int cnrFileCount) {
		this.cnrFileCount = cnrFileCount;
	}

	public String getCnrHours() {
		return cnrHours;
	}

	public void setCnrHours(String cnrHours) {
		this.cnrHours = cnrHours;
	}

	public int getReviewFilecount() {
		return reviewFilecount;
	}

	public void setReviewFilecount(int reviewFilecount) {
		this.reviewFilecount = reviewFilecount;
	}

	public String getReviewFileHours() {
		return reviewFileHours;
	}

	public void setReviewFileHours(String reviewFileHours) {
		this.reviewFileHours = reviewFileHours;
	}

	public int getAuditsFileCount() {
		return auditsFileCount;
	}

	public void setAuditsFileCount(int auditsFileCount) {
		this.auditsFileCount = auditsFileCount;
	}

	public String getAuditsFileHours() {
		return auditsFileHours;
	}

	public void setAuditsFileHours(String auditsFileHours) {
		this.auditsFileHours = auditsFileHours;
	}

	public long getTotalCharts() {
		return totalCharts;
	}

	public void setTotalCharts(long totalCharts) {
		this.totalCharts = totalCharts;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}

	public String getAvgHours() {
		return avgHours;
	}

	public void setAvgHours(String avgHours) {
		this.avgHours = avgHours;
	}
	
	
}
