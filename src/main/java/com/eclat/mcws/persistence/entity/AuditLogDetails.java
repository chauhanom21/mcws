package com.eclat.mcws.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "audit_log_details")
public class AuditLogDetails {

	@Id
	@GeneratedValue
	private Long id;
	private String status;

	// @Column(name = "audit_log_id")
	// private Integer auditLogId;

	@Column(name = "audit_data_id")
	private Long auditDataId;

	@Column(name = "worklist_id")
	private Long worklistId;

	@Column(name = "client_id")
	private Integer auditClientId;

	@Column(name = "chart_spe_id")
	private Integer chartSpeId;

	@Column(name = "coder_id")
	private Integer auditCoderId;

	@ManyToOne
	@JoinColumn(name = "audit_log_id", nullable = false)
	private AuditLog auditLog;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getAuditDataId() {
		return auditDataId;
	}

	public void setAuditDataId(Long auditDataId) {
		this.auditDataId = auditDataId;
	}

	public Long getWorklistId() {
		return worklistId;
	}

	public void setWorklistId(Long worklistId) {
		this.worklistId = worklistId;
	}

	public Integer getAuditClientId() {
		return auditClientId;
	}

	public void setAuditClientId(Integer auditClientId) {
		this.auditClientId = auditClientId;
	}

	public Integer getAuditCoderId() {
		return auditCoderId;
	}

	public Integer getChartSpeId() {
		return chartSpeId;
	}

	public void setChartSpeId(Integer chartSpeId) {
		this.chartSpeId = chartSpeId;
	}

	public void setAuditCoderId(Integer auditCoderId) {
		this.auditCoderId = auditCoderId;
	}

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

}
