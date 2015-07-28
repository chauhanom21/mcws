package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "userclients")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UsersClient {

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "user_id")
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonBackReference
	private com.eclat.mcws.persistence.entity.Coders coder;

	@Column(name = "client_id")
	private int clientId;

	@Column(name = "priority")
	private int priority;

	@Column(name = "audit_percentage")
	private int auditPercentage;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public com.eclat.mcws.persistence.entity.Coders getCoder() {
		return coder;
	}

	public void setCoder(com.eclat.mcws.persistence.entity.Coders coder) {
		this.coder = coder;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getAuditPercentage() {
		return auditPercentage;
	}

	public void setAuditPercentage(int auditPercentage) {
		this.auditPercentage = auditPercentage;
	}

	@Override
	public String toString() {
		return "UsersClient [id=" + id + ", clientId=" + clientId
				+ ", priority=" + priority + "]";
	}

}
