package com.eclat.mcws.persistence.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "audit_log")
public class AuditLog {

	@Id
	@GeneratedValue
	private int id;
	private String title;
	private Integer owner;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "audit_criteria")
	private String auditCriteria;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "auditLog")
	@Fetch(FetchMode.JOIN)
	private Set<AuditLogDetails> auditLogDetails;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getAuditCriteria() {
		return auditCriteria;
	}

	public void setAuditCriteria(String auditCriteria) {
		this.auditCriteria = auditCriteria;
	}

	public Set<AuditLogDetails> getAuditLogDetails() {
		return auditLogDetails;
	}

	public void setAuditLogDetails(Set<AuditLogDetails> auditLogDetails) {
		this.auditLogDetails = auditLogDetails;
	}

}
