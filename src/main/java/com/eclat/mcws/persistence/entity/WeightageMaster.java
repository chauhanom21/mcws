package com.eclat.mcws.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "weightage_master")
public class WeightageMaster {

	@Id
	private int id;
	@Column(name = "chart_type")
	private String chartType;
    @Column(name = "column_name")
	private String columnName;
    @Column(name = "weightage")
	private Double waightage;

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

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Double getWaightage() {
		return waightage;
	}

	public void setWaightage(Double waightage) {
		this.waightage = waightage;
	}

}
