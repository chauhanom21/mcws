package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "client_details")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClientDetails implements com.eclat.mcws.persistence.entity.Entity {

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "client_name")
	private String name;

	@Column(name = "new_client")
	private boolean newClient;

	@Column(name = "location")
	private String location;

	@Column(name = "daf")
	private int daf;

	@Column(name = "audit_percentage")
	private int auditPercentage;

	@Column(name = "dat")
	private Integer dat;
	
	@Column(name = "dist_order")
	private String distOrder;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNewClient() {
		return newClient;
	}

	public void setNewClient(boolean newClient) {
		this.newClient = newClient;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getDaf() {
		return daf;
	}

	public void setDaf(int daf) {
		this.daf = daf;
	}

	public int getAuditPercentage() {
		return auditPercentage;
	}

	public void setAuditPercentage(int auditPercentage) {
		this.auditPercentage = auditPercentage;
	}

	public Integer getDat() {
		return dat;
	}

	public void setDat(Integer dat) {
		this.dat = dat;
	}

	public String getDistOrder() {
		return distOrder;
	}

	public void setDistOrder(String distOrder) {
		this.distOrder = distOrder;
	}

	@Override
	public String toString() {
		return "ClientDetails [id=" + id + ", name=" + name + ", newClient="
				+ newClient + ", location=" + location + ", daf=" + daf
				+ ", auditPercentage=" + auditPercentage + ", distOrder=" + distOrder + "]";
	}
	
	
	

}
