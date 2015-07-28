package com.eclat.mcws.persistence.entity;

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

@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "usercharts")
public class UserCharts {
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name="user_id", nullable = false)
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private com.eclat.mcws.persistence.entity.Coders coder;
	
	@Column(name = "chart_specialization_id")
	private int chartSpecializationId;

	@Column(name = "priority")
	private int priority;

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

	public int getChartSpecializationId() {
		return chartSpecializationId;
	}

	public void setChartSpecializationId(int chartSpecializationId) {
		this.chartSpecializationId = chartSpecializationId;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "UserCharts [id=" + id + ", coder=" + coder
				+ ", chartSpecializationId=" + chartSpecializationId
				+ ", priority=" + priority + "]";
	}
	
	

}
