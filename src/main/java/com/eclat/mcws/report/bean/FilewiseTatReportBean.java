package com.eclat.mcws.report.bean;

import java.io.Serializable;

public class FilewiseTatReportBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9072215275280317339L;
	private Integer sNo;
	private String accountNo;
	private String mrNo;
	private String coderCode;
	private String coderName;
	private String localQACode;
	private String localQAName;
	private String client;
	private String chartType;
	private String chartSpecialization;
	private String outOfTatHrs;
	private String currentStatus;
	
	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getMrNo() {
		return mrNo;
	}
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public String getCoderCode() {
		return coderCode;
	}
	public void setCoderCode(String coderCode) {
		this.coderCode = coderCode;
	}
	public String getCoderName() {
		return coderName;
	}
	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}
	public String getLocalQACode() {
		return localQACode;
	}
	public void setLocalQACode(String localQACode) {
		this.localQACode = localQACode;
	}
	public String getLocalQAName() {
		return localQAName;
	}
	public void setLocalQAName(String localQAName) {
		this.localQAName = localQAName;
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
	public String getChartSpecialization() {
		return chartSpecialization;
	}
	public void setChartSpecialization(String chartSpecialization) {
		this.chartSpecialization = chartSpecialization;
	}
	public String getOutOfTatHrs() {
		return outOfTatHrs;
	}
	public void setOutOfTatHrs(String outOfTatHrs) {
		this.outOfTatHrs = outOfTatHrs;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	
}
