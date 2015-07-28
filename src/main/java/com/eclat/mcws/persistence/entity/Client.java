package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="client_details")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Client {
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name="client_name")
	private String clientName;
	
	@Column(name="dat")
	private String dataAccFactor;
	
	@Column(name="location")
	private String location;
	
	@Column(name="new_client")
	private Boolean newClient;
	
	@Column(name="audit_percentage")
	private int auditPercentage;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getDataAccFactor() {
		return dataAccFactor;
	}
	public void setDataAccFactor(String dataAccFactor) {
		this.dataAccFactor = dataAccFactor;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Boolean getNewClient() {
		return newClient;
	}
	public void setNewClient(Boolean newClient) {
		this.newClient = newClient;
	}
	public int getAuditPercentage() {
		return auditPercentage;
	}
	public void setAuditPercentage(int auditPercentage) {
		this.auditPercentage = auditPercentage;
	}
	
	

}
