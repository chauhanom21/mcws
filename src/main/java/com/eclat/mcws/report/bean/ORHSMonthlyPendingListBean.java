package com.eclat.mcws.report.bean;

import java.util.Date;

public class ORHSMonthlyPendingListBean {
	
	private Integer sNo;
	
	private String accountNo;
	
	//This only for MDA chart
	private String mrNo;
	
	private Date dischargeDate;
	
	private String Notes;

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

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	@Override
	public String toString() {
		return "ORHSMonthlyPendingListBean [sNo=" + sNo + ", accountNo="
				+ accountNo + ", mrNo=" + mrNo + ", dischargeDate="
				+ dischargeDate + ", Notes=" + Notes + "]";
	}
}
