package com.eclat.mcws.report.bean;

import java.io.Serializable;

public class InvoiceReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1746486112707767059L;
	private Long sno;
	private Integer clientId;
	private String account;
	private String dischargeDate;
	private String receivedDate;
	private String completedDate;
	private String chartType;
	private Integer los;
	private Integer tat;
	private String notes;
	private String status;

	public Long getSno() {
		return sno;
	}

	public void setSno(Long sno) {
		this.sno = sno;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(String dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "InvoiceReportBean [sno=" + sno + ", clientId=" + clientId + ", account=" + account + ", dischargeDate="
				+ dischargeDate + ", receivedDate=" + receivedDate + ", completedDate=" + completedDate
				+ ", chartType=" + chartType + ", los=" + los + ", tat=" + tat + ", notes=" + notes + "]";
	}

}
