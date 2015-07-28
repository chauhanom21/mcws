package com.eclat.mcws.persistence.entity;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "review_worklist")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReviewWorklist {
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private long id;

	@Column(name = "user_reviewd")
	private int userReviewd;

	@Column(name = "user_role")
	private String userRole;

	@Column(name = "created_date")
	@Type(type = "timestamp")
	private Timestamp createdDate;

	@Column(name = "lastupdated_date")
	@Type(type = "timestamp")
	private Timestamp updatedDate;

	@Column(name = "comment")
	private String comment;

	@Column(name = "status")
	private String status;

	@Column(name = "previous_status")
	private String previousStatus;

	@Column(name = "work_start_time")
	private Timestamp workStartTime;

	@Column(name = "total_work_time_mins", nullable = false, columnDefinition = "int default 0")
	private Integer totalTimeTaken;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "worklist_id")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private WorkListItem workListItem;

	@ManyToOne
	@JoinColumn(name = "user_reviewd", insertable = false, updatable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Coders coders;

	@Column(name = "coding_details")
	private String codingDetails;

	@Column(name = "is_temp_data", columnDefinition = "int default 0")
	private Boolean isTempData;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserReviewd() {
		return userReviewd;
	}

	public void setUserReviewd(int userReviewd) {
		this.userReviewd = userReviewd;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public WorkListItem getWorkListItem() {
		return workListItem;
	}

	public void setWorkListItem(WorkListItem workListItem) {
		this.workListItem = workListItem;
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

	public Timestamp getWorkStartTime() {
		return workStartTime;
	}

	public void setWorkStartTime(Timestamp workStartTime) {
		this.workStartTime = workStartTime;
	}

	public Integer getTotalTimeTaken() {
		return totalTimeTaken;
	}

	public void setTotalTimeTaken(Integer totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	public Coders getCoders() {
		return coders;
	}

	public void setCoders(Coders coders) {
		this.coders = coders;
	}

	public String getCodingDetails() {
		return codingDetails;
	}

	public void setCodingDetails(String codingDetails) {
		this.codingDetails = codingDetails;
	}

	public Boolean getIsTempData() {
		return isTempData;
	}

	public void setIsTempData(Boolean isTempData) {
		this.isTempData = isTempData;
	}

	@Override
	public String toString() {
		return "ReviewWorklist [id=" + id + ", userReviewd=" + userReviewd
				+ ", userRole=" + userRole + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", comment=" + comment
				+ ", status=" + status + ", totalTimeTaken=" + totalTimeTaken
				+ ", coders=" + coders + "]";
	}

}
