package com.eclat.mcws.report.bean;

public class ReportBean {

	private String client;
	private String chartType;
	private Integer completed;
	private Integer inComplete;
	private Integer misc;
	private Integer coderAssigned;
	private Integer localQAAssigned;
	private Integer globalQAAssigned;
	private Integer localCNR;
	private Integer globalCNR;
	private Integer coderInProgress;
	private Integer localQAInProgress;
	private Integer globalQAInProgress;
	private Integer localAudit;
	private Integer globalAudit;
	private Integer audited;
	private Integer open;
	private Long count;

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public Integer getCompleted() {
		return completed;
	}

	public void setCompleted(Integer completed) {
		this.completed = completed;
	}

	public Integer getInComplete() {
		return inComplete;
	}

	public void setInComplete(Integer inComplete) {
		this.inComplete = inComplete;
	}

	public Integer getMisc() {
		return misc;
	}

	public void setMisc(Integer misc) {
		this.misc = misc;
	}

	public Integer getCoderAssigned() {
		return coderAssigned;
	}

	public void setCoderAssigned(Integer coderAssigned) {
		this.coderAssigned = coderAssigned;
	}

	public Integer getLocalQAAssigned() {
		return localQAAssigned;
	}

	public void setLocalQAAssigned(Integer localQAAssigned) {
		this.localQAAssigned = localQAAssigned;
	}

	public Integer getGlobalQAAssigned() {
		return globalQAAssigned;
	}

	public void setGlobalQAAssigned(Integer globalQAAssigned) {
		this.globalQAAssigned = globalQAAssigned;
	}

	public Integer getLocalCNR() {
		return localCNR;
	}

	public void setLocalCNR(Integer localCNR) {
		this.localCNR = localCNR;
	}

	public Integer getGlobalCNR() {
		return globalCNR;
	}

	public void setGlobalCNR(Integer globalCNR) {
		this.globalCNR = globalCNR;
	}

	public Integer getCoderInProgress() {
		return coderInProgress;
	}

	public void setCoderInProgress(Integer coderInProgress) {
		this.coderInProgress = coderInProgress;
	}

	public Integer getLocalQAInProgress() {
		return localQAInProgress;
	}

	public void setLocalQAInProgress(Integer localQAInProgress) {
		this.localQAInProgress = localQAInProgress;
	}

	public Integer getGlobalQAInProgress() {
		return globalQAInProgress;
	}

	public void setGlobalQAInProgress(Integer globalQAInProgress) {
		this.globalQAInProgress = globalQAInProgress;
	}

	public Integer getLocalAudit() {
		return localAudit;
	}

	public void setLocalAudit(Integer localAudit) {
		this.localAudit = localAudit;
	}

	public Integer getGlobalAudit() {
		return globalAudit;
	}

	public void setGlobalAudit(Integer globalAudit) {
		this.globalAudit = globalAudit;
	}

	public Integer getAudited() {
		return audited;
	}

	public void setAudited(Integer audited) {
		this.audited = audited;
	}

	public Integer getOpen() {
		return open;
	}

	public void setOpen(Integer open) {
		this.open = open;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
