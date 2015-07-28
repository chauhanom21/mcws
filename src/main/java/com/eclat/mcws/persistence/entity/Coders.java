package com.eclat.mcws.persistence.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@javax.persistence.Entity
@Table(name = "coders")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
public class Coders implements com.eclat.mcws.persistence.entity.Entity {

	@Id
	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(name = "coder_max_workload")
	private int coderMaxWorkLoad;

	@Column(name = "coder_daily_workload")
	private Double coderDailyWorkLoad;

	@Column(name = "job_level", nullable = false)
	private String jobLevel;

	@Column(name = "is_new_coder")
	private Boolean newCoder;

	@Column(name = "is_coder")
	private Boolean coder;

	@Column(name = "is_remote_qa")
	private Boolean remoteQa;

	@Column(name = "is_local_qa")
	private Boolean localQa;

	@Column(name = "completeLoad")
	private Double completeLoad;

	@Column(name = "em_value")
	private Double emValue;

	@OneToMany(mappedBy = "coder", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonManagedReference
	private Set<UsersClient> userClients;

	@OneToMany(mappedBy = "coder", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonManagedReference
	private Set<UserCharts> userCharts;

	@Column(name = "last_update_date")
	private Timestamp updatedDate;

	@Column(name = "notes")
	private String notes;

	@Column(name = "employee_id")
	private String employeeId;

	@Transient
	private String username;
	@Transient
	private String firstname;
	@Transient
	private String lastname;
	@Transient
	private String location;
	@Transient
	private UserRole userRole;
	@Transient
	private Boolean isAvailable;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCoderMaxWorkLoad() {
		return coderMaxWorkLoad;
	}

	public void setCoderMaxWorkLoad(int coderMaxWorkLoad) {
		this.coderMaxWorkLoad = coderMaxWorkLoad;
	}

	public Double getCoderDailyWorkLoad() {
		return coderDailyWorkLoad;
	}

	public void setCoderDailyWorkLoad(Double coderDailyWorkLoad) {
		this.coderDailyWorkLoad = coderDailyWorkLoad;
	}

	public String getJobLevel() {
		return jobLevel;
	}

	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}

	public Boolean getNewCoder() {
		return newCoder;
	}

	public void setNewCoder(Boolean newCoder) {
		this.newCoder = newCoder;
	}

	public Boolean getCoder() {
		return coder;
	}

	public void setCoder(Boolean coder) {
		this.coder = coder;
	}

	public Boolean getRemoteQa() {
		return remoteQa;
	}

	public void setRemoteQa(Boolean remoteQa) {
		this.remoteQa = remoteQa;
	}

	public Boolean getLocalQa() {
		return localQa;
	}

	public void setLocalQa(Boolean localQa) {
		this.localQa = localQa;
	}

	public Double getCompleteLoad() {
		return completeLoad;
	}

	public void setCompleteLoad(Double completeLoad) {
		this.completeLoad = completeLoad;
	}

	public Double getEmValue() {
		return emValue;
	}

	public void setEmValue(Double emValue) {
		this.emValue = emValue;
	}

	public Set<UsersClient> getUserClients() {
		return userClients;
	}

	public void setUserClients(Set<UsersClient> userClients) {
		this.userClients = userClients;
	}

	public Set<UserCharts> getUserCharts() {
		return userCharts;
	}

	public void setUserCharts(Set<UserCharts> userCharts) {
		this.userCharts = userCharts;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

}
