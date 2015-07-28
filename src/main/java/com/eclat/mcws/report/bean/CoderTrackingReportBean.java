package com.eclat.mcws.report.bean;

import java.io.Serializable;

public class CoderTrackingReportBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5177037043015233231L;
	private Integer sNo;
	private String coderFirstName;
	private String coderLastName;
	private String coderEmpCode;
	private String fromDate;
	private String toDate;
	private Integer inCompleteItemsCount;
	private Integer completedItemsCount;
	private Integer cnrItemsCount;
	private Integer miscItemsCount;
	private Integer codingInProgressItemsCount;
	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}
	public String getCoderFirstName() {
		return coderFirstName;
	}
	public void setCoderFirstName(String coderFirstName) {
		this.coderFirstName = coderFirstName;
	}
	public String getCoderLastName() {
		return coderLastName;
	}
	public void setCoderLastName(String coderLastName) {
		this.coderLastName = coderLastName;
	}
	public String getCoderEmpCode() {
		return coderEmpCode;
	}
	public void setCoderEmpCode(String coderEmpCode) {
		this.coderEmpCode = coderEmpCode;
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
	public Integer getInCompleteItemsCount() {
		return inCompleteItemsCount;
	}
	public void setInCompleteItemsCount(Integer inCompleteItemsCount) {
		this.inCompleteItemsCount = inCompleteItemsCount;
	}
	public Integer getCompletedItemsCount() {
		return completedItemsCount;
	}
	public void setCompletedItemsCount(Integer completedItemsCount) {
		this.completedItemsCount = completedItemsCount;
	}
	public Integer getCnrItemsCount() {
		return cnrItemsCount;
	}
	public void setCnrItemsCount(Integer cnrItemsCount) {
		this.cnrItemsCount = cnrItemsCount;
	}
	public Integer getMiscItemsCount() {
		return miscItemsCount;
	}
	public void setMiscItemsCount(Integer miscItemsCount) {
		this.miscItemsCount = miscItemsCount;
	}
	public Integer getCodingInProgressItemsCount() {
		return codingInProgressItemsCount;
	}
	public void setCodingInProgressItemsCount(Integer codingInProgressItemsCount) {
		this.codingInProgressItemsCount = codingInProgressItemsCount;
	}
	
	
}
