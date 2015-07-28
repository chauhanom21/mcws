package com.eclat.mcws.report.bean;

import java.util.Date;

public class ORHSMonthlyCodedListBean {
	
	private Integer sNo;
	private String accountNo;
	
	// This is for only MDA chart Type
	private String mrNo;
	
	private Date dischargeDate;
	private Date receivedDate;
	private Date completedDate;
	
	// This is for only  IP charts
	private Integer los;
	private Integer tat;
	private String notes;
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
	public Date getDischargeDate() {
		return dischargeDate;
	}
	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Date getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}
	public Integer getLos() {
		return los;
	}
	public void setLos(Integer los) {
		this.los = los;
	}
	public Integer getTat() {
		return tat;
	}
	public void setTat(Integer tat) {
		this.tat = tat;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		return "ORHSMonthlyCodedListBean [sNo=" + sNo + ", accountNo="
				+ accountNo + ", mrNo=" + mrNo + ", dischargeDate="
				+ dischargeDate + ", receivedDate=" + receivedDate
				+ ", completedDate=" + completedDate + ", los=" + los
				+ ", tat=" + tat + ", notes=" + notes + "]";
	}		
}
 