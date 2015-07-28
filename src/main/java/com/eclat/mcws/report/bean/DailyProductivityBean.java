package com.eclat.mcws.report.bean;

import java.io.Serializable;

public class DailyProductivityBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8635582502602155898L;
	private String empcode;
	private String empname;
	private String location;

	// ORHS client charts
	private int orhsIPChart;
	private String orhsIPChartHrs = "0";
	private int orhsERChart;
	private String orhsERChartHrs ="0";
	private int orhsOPChart;
	private String orhsOPChartHrs = "0";
	private int orhsANCChart;
	private String orhsANCChartHrs = "0";
	private int orhsM2Chart;
	private String orhsM2ChartHrs = "0";
	private int orhsMDACChart;
	private String orhsMDAChartHrs = "0";
	private int orhsEKGECGChart;
	private String orhsEKGECGChartHrs = "0";
	private int orhsPhyChart;
	private String orhsPhyChartHrs = "0";
	// HC client charts
	private int hcIPChart;
	private String hcIPChartHrs = "0";
	private int hcOPChart;
	private String hcOPChartHrs = "0";
	private int hcERChart;
	private String hcERChartHrs = "0";

	// MCCG client charts
	private int mccgIPChart;
	private String mccgIPChartHrs = "0";

	// PC client charts
	private int pcIPChart;
	private String pcIPChartHrs = "0";
	private int pcOPChart;
	private String pcOPChartHrs = "0";

	// Halifax
	private int halifaxEDChart;
	private String halifaxEDChartHrs = "0";
	private int halifaxIPChart;
	private String halifaxIPChartHrs = "0";

	private Long totalCharts;
	private String totalHours = "0";
	private String avgHours = "0";

	public String getEmpcode() {
		return empcode;
	}

	public void setEmpcode(String empcode) {
		this.empcode = empcode;
	}

	public String getEmpname() {
		return empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getTotalCharts() {
		return totalCharts;
	}

	public void setTotalCharts(Long totalCharts) {
		this.totalCharts = totalCharts;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}

	public String getAvgHours() {
		return avgHours;
	}

	public void setAvgHours(String avgHours) {
		this.avgHours = avgHours;
	}

	public int getOrhsIPChart() {
		return orhsIPChart;
	}

	public void setOrhsIPChart(int orhsIPChart) {
		this.orhsIPChart = orhsIPChart;
	}

	public String getOrhsIPChartHrs() {
		return orhsIPChartHrs;
	}

	public void setOrhsIPChartHrs(String orhsIPChartHrs) {
		this.orhsIPChartHrs = orhsIPChartHrs;
	}

	public int getOrhsERChart() {
		return orhsERChart;
	}

	public void setOrhsERChart(int orhsERChart) {
		this.orhsERChart = orhsERChart;
	}

	public String getOrhsERChartHrs() {
		return orhsERChartHrs;
	}

	public void setOrhsERChartHrs(String orhsERChartHrs) {
		this.orhsERChartHrs = orhsERChartHrs;
	}

	public int getOrhsOPChart() {
		return orhsOPChart;
	}

	public void setOrhsOPChart(int orhsOPChart) {
		this.orhsOPChart = orhsOPChart;
	}

	public String getOrhsOPChartHrs() {
		return orhsOPChartHrs;
	}

	public void setOrhsOPChartHrs(String orhsOPChartHrs) {
		this.orhsOPChartHrs = orhsOPChartHrs;
	}

	public int getOrhsANCChart() {
		return orhsANCChart;
	}

	public void setOrhsANCChart(int orhsANCChart) {
		this.orhsANCChart = orhsANCChart;
	}

	public String getOrhsANCChartHrs() {
		return orhsANCChartHrs;
	}

	public void setOrhsANCChartHrs(String orhsANCChartHrs) {
		this.orhsANCChartHrs = orhsANCChartHrs;
	}

	public int getOrhsM2Chart() {
		return orhsM2Chart;
	}

	public void setOrhsM2Chart(int orhsM2Chart) {
		this.orhsM2Chart = orhsM2Chart;
	}

	public String getOrhsM2ChartHrs() {
		return orhsM2ChartHrs;
	}

	public void setOrhsM2ChartHrs(String orhsM2ChartHrs) {
		this.orhsM2ChartHrs = orhsM2ChartHrs;
	}

	public int getOrhsMDACChart() {
		return orhsMDACChart;
	}

	public void setOrhsMDACChart(int orhsMDACChart) {
		this.orhsMDACChart = orhsMDACChart;
	}

	public String getOrhsMDAChartHrs() {
		return orhsMDAChartHrs;
	}

	public void setOrhsMDAChartHrs(String orhsMDAChartHrs) {
		this.orhsMDAChartHrs = orhsMDAChartHrs;
	}

	public int getOrhsEKGECGChart() {
		return orhsEKGECGChart;
	}

	public void setOrhsEKGECGChart(int orhsEKGECGChart) {
		this.orhsEKGECGChart = orhsEKGECGChart;
	}

	public String getOrhsEKGECGChartHrs() {
		return orhsEKGECGChartHrs;
	}

	public void setOrhsEKGECGChartHrs(String orhsEKGECGChartHrs) {
		this.orhsEKGECGChartHrs = orhsEKGECGChartHrs;
	}

	public int getOrhsPhyChart() {
		return orhsPhyChart;
	}

	public void setOrhsPhyChart(int orhsPhyChart) {
		this.orhsPhyChart = orhsPhyChart;
	}

	public String getOrhsPhyChartHrs() {
		return orhsPhyChartHrs;
	}

	public void setOrhsPhyChartHrs(String orhsPhyChartHrs) {
		this.orhsPhyChartHrs = orhsPhyChartHrs;
	}

	public int getHcIPChart() {
		return hcIPChart;
	}

	public void setHcIPChart(int hcIPChart) {
		this.hcIPChart = hcIPChart;
	}

	public String getHcIPChartHrs() {
		return hcIPChartHrs;
	}

	public void setHcIPChartHrs(String hcIPChartHrs) {
		this.hcIPChartHrs = hcIPChartHrs;
	}

	public int getHcOPChart() {
		return hcOPChart;
	}

	public void setHcOPChart(int hcOPChart) {
		this.hcOPChart = hcOPChart;
	}

	public String getHcOPChartHrs() {
		return hcOPChartHrs;
	}

	public void setHcOPChartHrs(String hcOPChartHrs) {
		this.hcOPChartHrs = hcOPChartHrs;
	}

	public int getHcERChart() {
		return hcERChart;
	}

	public void setHcERChart(int hcERChart) {
		this.hcERChart = hcERChart;
	}

	public String getHcERChartHrs() {
		return hcERChartHrs;
	}

	public void setHcERChartHrs(String hcERChartHrs) {
		this.hcERChartHrs = hcERChartHrs;
	}

	public int getMccgIPChart() {
		return mccgIPChart;
	}

	public void setMccgIPChart(int mccgIPChart) {
		this.mccgIPChart = mccgIPChart;
	}

	public String getMccgIPChartHrs() {
		return mccgIPChartHrs;
	}

	public void setMccgIPChartHrs(String mccgIPChartHrs) {
		this.mccgIPChartHrs = mccgIPChartHrs;
	}

	public int getPcIPChart() {
		return pcIPChart;
	}

	public void setPcIPChart(int pcIPChart) {
		this.pcIPChart = pcIPChart;
	}

	public String getPcIPChartHrs() {
		return pcIPChartHrs;
	}

	public void setPcIPChartHrs(String pcIPChartHrs) {
		this.pcIPChartHrs = pcIPChartHrs;
	}

	public int getPcOPChart() {
		return pcOPChart;
	}

	public void setPcOPChart(int pcOPChart) {
		this.pcOPChart = pcOPChart;
	}

	public String getPcOPChartHrs() {
		return pcOPChartHrs;
	}

	public void setPcOPChartHrs(String pcOPChartHrs) {
		this.pcOPChartHrs = pcOPChartHrs;
	}

	public int getHalifaxEDChart() {
		return halifaxEDChart;
	}

	public void setHalifaxEDChart(int halifaxEDChart) {
		this.halifaxEDChart = halifaxEDChart;
	}

	public String getHalifaxEDChartHrs() {
		return halifaxEDChartHrs;
	}

	public void setHalifaxEDChartHrs(String halifaxEDChartHrs) {
		this.halifaxEDChartHrs = halifaxEDChartHrs;
	}

	public int getHalifaxIPChart() {
		return halifaxIPChart;
	}

	public void setHalifaxIPChart(int halifaxIPChart) {
		this.halifaxIPChart = halifaxIPChart;
	}

	public String getHalifaxIPChartHrs() {
		return halifaxIPChartHrs;
	}

	public void setHalifaxIPChartHrs(String halifaxIPChartHrs) {
		this.halifaxIPChartHrs = halifaxIPChartHrs;
	}

}
