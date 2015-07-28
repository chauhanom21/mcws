package com.eclat.mcws.dto;

import java.io.Serializable;
import java.util.List;

import org.json.simple.JSONObject;

public class TaskDetails implements Serializable {

	private Long id;
	private Long reviewWorkListId;
	private Integer clientId;
	private String client;
	private Integer chartSpecId;
	private String chartType;
	private Integer tat;
	private String status;
	private String previousStatus;
	private String userStatus;
	private String coderName;
	private Integer coderId;
	private String remoteQAName;
	private Integer remoteQAId;
	private String localQAName;
	private Integer localQAId;
	private String em;
	private String spanClass;
	// Attributed required for Coders screen
	private int los;
	private String admitDate;
	private String dischargeDate;
	private String specilization;
	private String accountNumber;
	private String mrNumber;
	private String codingData;
	private JSONObject columnValCount;
	private String userRole;
	private String qaReviewed;
	private String comments;
	private String receivedDate;
	private String updateDate;
	private Long updateDateMiliis;
	private String patientName;
	// remainingTat
	private String remainingTat;
	private String taskAssignedTime;
	private String totalTimeTaken;
	private Long totalWorkTime;
	// Audit
	private String drg;
	private String insurance;

	private List<String> codingHistory;

	private List<WorkItemHistory> workitemHistory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReviewWorkListId() {
		return reviewWorkListId;
	}

	public void setReviewWorkListId(Long reviewWorkListId) {
		this.reviewWorkListId = reviewWorkListId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Integer getChartSpecId() {
		return chartSpecId;
	}

	public void setChartSpecId(Integer chartSpecId) {
		this.chartSpecId = chartSpecId;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Integer getTat() {
		return tat;
	}

	public void setTat(Integer tat) {
		this.tat = tat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getCoderName() {
		return coderName;
	}

	public void setCoderName(String coderName) {
		this.coderName = coderName;
	}

	public Integer getCoderId() {
		return coderId;
	}

	public void setCoderId(Integer coderId) {
		this.coderId = coderId;
	}

	public String getRemoteQAName() {
		return remoteQAName;
	}

	public void setRemoteQAName(String remoteQAName) {
		this.remoteQAName = remoteQAName;
	}

	public Integer getRemoteQAId() {
		return remoteQAId;
	}

	public void setRemoteQAId(Integer remoteQAId) {
		this.remoteQAId = remoteQAId;
	}

	public String getLocalQAName() {
		return localQAName;
	}

	public void setLocalQAName(String localQAName) {
		this.localQAName = localQAName;
	}

	public Integer getLocalQAId() {
		return localQAId;
	}

	public void setLocalQAId(Integer localQAId) {
		this.localQAId = localQAId;
	}

	public String getEm() {
		return em;
	}

	public void setEm(String em) {
		this.em = em;
	}

	public String getSpanClass() {
		return spanClass;
	}

	public void setSpanClass(String spanClass) {
		this.spanClass = spanClass;
	}

	public String getAdmitDate() {
		return admitDate;
	}

	public void setAdmitDate(String admitDate) {
		this.admitDate = admitDate;
	}

	public String getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(String dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public int getLos() {
		return los;
	}

	public void setLos(int los) {
		this.los = los;
	}

	public String getSpecilization() {
		return specilization;
	}

	public void setSpecilization(String specilization) {
		this.specilization = specilization;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getMrNumber() {
		return mrNumber;
	}

	public void setMrNumber(String mrNumber) {
		this.mrNumber = mrNumber;
	}

	public String getCodingData() {
		return codingData;
	}

	public void setCodingData(String codingData) {
		this.codingData = codingData;
	}

	public JSONObject getColumnValCount() {
		return columnValCount;
	}

	public void setColumnValCount(JSONObject columnValCount) {
		this.columnValCount = columnValCount;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getQaReviewed() {
		return qaReviewed;
	}

	public void setQaReviewed(String qaReviewed) {
		this.qaReviewed = qaReviewed;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Long getUpdateDateMiliis() {
		return updateDateMiliis;
	}

	public void setUpdateDateMiliis(Long updateDateMiliis) {
		this.updateDateMiliis = updateDateMiliis;
	}

	public String getRemainingTat() {
		return remainingTat;
	}

	public void setRemainingTat(String remainingTat) {
		this.remainingTat = remainingTat;
	}

	public String getTaskAssignedTime() {
		return taskAssignedTime;
	}

	public void setTaskAssignedTime(String taskAssignedTime) {
		this.taskAssignedTime = taskAssignedTime;
	}

	public String getTotalTimeTaken() {
		return totalTimeTaken;
	}

	public void setTotalTimeTaken(String totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	public List<String> getCodingHistory() {
		return codingHistory;
	}

	public void setCodingHistory(List<String> codingHistory) {
		this.codingHistory = codingHistory;
	}

	public Long getTotalWorkTime() {
		return totalWorkTime;
	}

	public void setTotalWorkTime(Long totalWorkTime) {
		this.totalWorkTime = totalWorkTime;
	}

	public String getDrg() {
		return drg;
	}

	public void setDrg(String drg) {
		this.drg = drg;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public List<WorkItemHistory> getWorkitemHistory() {
		return workitemHistory;
	}

	public void setWorkitemHistory(List<WorkItemHistory> workitemHistory) {
		this.workitemHistory = workitemHistory;
	}

}
