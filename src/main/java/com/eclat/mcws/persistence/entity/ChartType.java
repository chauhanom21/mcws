package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chart_types")
public class ChartType implements com.eclat.mcws.persistence.entity.Entity{
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "chart_type", nullable = false)
	private String chartType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
}
