package com.eclat.mcws.report.bean;

import java.io.Serializable;
import java.util.Date;

public class LocalQATrackingReportBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8628356309543907928L;
	private Integer sNo;
	private String qaFirstName;
	private String qaLastName;
	private String qaEmpCode;
	private String fromDate;
	private String toDate;
	private Integer inCompleteItemsCount;
	private Integer completedItemsCount;
	private Integer cnrRemoteQAItemsCount;
	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}
	public String getQaFirstName() {
		return qaFirstName;
	}
	public void setQaFirstName(String qaFirstName) {
		this.qaFirstName = qaFirstName;
	}
	public String getQaLastName() {
		return qaLastName;
	}
	public void setQaLastName(String qaLastName) {
		this.qaLastName = qaLastName;
	}
	public String getQaEmpCode() {
		return qaEmpCode;
	}
	public void setQaEmpCode(String qaEmpCode) {
		this.qaEmpCode = qaEmpCode;
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
	public Integer getCnrRemoteQAItemsCount() {
		return cnrRemoteQAItemsCount;
	}
	public void setCnrRemoteQAItemsCount(Integer cnrRemoteQAItemsCount) {
		this.cnrRemoteQAItemsCount = cnrRemoteQAItemsCount;
	}
	
}
