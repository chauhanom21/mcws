package com.eclat.mcws.report.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="clientwise_report_view")
//@Table()
public class ClientwiseReportBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5486176832605731087L;
	//@Id
	private Integer sNo;
	private String empCode;
	private String empName;
	private String client;
	@Id
	private String accountNo;
	private String mrNo;
	private String childAccount;
	private String parentAccount;
	private String patientName;
	private String insurance;
	private String chartType;
	private String chartSpecialization;
	private Date admitDate;
	private Date dischargeDate;
	private Date receivedDate;
	private Date codedDate;
	private Date completedDate;
	private Integer los;
	private String localQACode;
	private String localQAName;
	//setting this for whether the workitem is 100% reviewed or not.
	// possible values are Yes  & No  (Not sure : "-")
	private String review100;
	private String status;
	private String comments;
	
	// Adding serviceType & campusCode for ORHS on 15-Dec-14
	private String serviceType;	
	private String campusCode;

	public Integer getsNo() {
		return sNo;
	}
	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
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
	public String getChildAccount() {
		return childAccount;
	}
	public void setChildAccount(String childAccount) {
		this.childAccount = childAccount;
	}
	public String getParentAccount() {
		return parentAccount;
	}
	public void setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
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
	public Date getAdmitDate() {
		return admitDate;
	}
	public void setAdmitDate(Date admitDate) {
		this.admitDate = admitDate;
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
	public Date getCodedDate() {
		return codedDate;
	}
	public void setCodedDate(Date codedDate) {
		this.codedDate = codedDate;
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
	public String getReview100() {
		return review100;
	}
	public void setReview100(String review100) {
		this.review100 = review100;
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
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getCampusCode() {
		return campusCode;
	}
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}
	@Override
	public String toString() {
		return "ClientwiseReportBean [sNo=" + sNo + ", empCode=" + empCode
				+ ", empName=" + empName + ", client=" + client
				+ ", accountNo=" + accountNo + ", mrNo=" + mrNo
				+ ", childAccount=" + childAccount + ", parentAccount="
				+ parentAccount + ", patientName=" + patientName
				+ ", insurance=" + insurance + ", chartType=" + chartType
				+ ", chartSpecialization=" + chartSpecialization
				+ ", admitDate=" + admitDate + ", dischargeDate="
				+ dischargeDate + ", receivedDate=" + receivedDate
				+ ", codedDate=" + codedDate + ", completedDate="
				+ completedDate + ", los=" + los + ", localQACode="
				+ localQACode + ", localQAName=" + localQAName + ", review100="
				+ review100 + ", status=" + status + ", comments=" + comments
				+ "]";
	}
		
}
