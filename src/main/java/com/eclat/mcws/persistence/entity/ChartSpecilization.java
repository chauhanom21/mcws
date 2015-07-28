package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "chart_specialization")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ChartSpecilization implements com.eclat.mcws.persistence.entity.Entity {

	@Id
	@GeneratedValue
	private int id;

	@Column(name = "chart_type", nullable = false)
	private String chartType;

	@Column(name = "chart_specialization", nullable = false)
	private String chartSpelization;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public String getChartSpelization() {
		return chartSpelization;
	}

	public void setChartSpelization(String chartSpelization) {
		this.chartSpelization = chartSpelization;
	}

}
