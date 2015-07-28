package com.eclat.mcws.report.bean;

public class ReportQueryParam {

	private String fromDate;
	private String toDate;
	private String queryClient;
	private Integer clientId;
	private String type;
	private String relativePath;
	private boolean isDataAvailable;
	private String chartType;

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getQueryClient() {
		return queryClient;
	}

	public void setQueryClient(String queryClient) {
		this.queryClient = queryClient;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public boolean getIsDataAvailable() {
		return isDataAvailable;
	}

	public void setIsDataAvailable(boolean isDataAvailable) {
		this.isDataAvailable = isDataAvailable;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	@Override
	public String toString() {
		return "ReportQueryParam [fromDate=" + fromDate + ", toDate=" + toDate
				+ ", queryClient=" + queryClient + ", clientId=" + clientId
				+ ", type=" + type + "]";
	}

}
