package com.eclat.mcws.enums;

public enum ChartTypes {
	IP("IP"),
	OP("OP"), 
	OPO("OPO"), 
	ER("ER"), 
	ANC("ANC"), 
	M2("M2"), 
	MDA("MDA"), 
	EKG("EKG"), 
	EKG_ECG("EKG_ECG"), 
	Physician("Physician"), 
	GE("GE"),
	ED("ED");
	
	private String chartType;
	
	public String getChartType() {
		return chartType;
	}

	private ChartTypes(String chartType) {
		this.chartType = chartType;
	}
}
