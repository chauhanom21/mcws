package com.eclat.mcws.dto;

public class CodersQADto {

	private int id;

	private String name;

	private String role;

	private String client;

	private int completedCharts;

	private int inCompleteCharts;

	private int miscCharts;

	private int cnrCharts;

	private int totalCharts;

	private String actualLoad;

	private String dailyLoad;

	private String completeLoad;

	private String available;

	private String location;

	private String jobLevel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CodersQADto [id=" + id + ", name=" + name + "]";
	}

	public CodersQADto(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public CodersQADto() {
		// TODO Auto-generated constructor stub
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public int getCompletedCharts() {
		return completedCharts;
	}

	public void setCompletedCharts(int completedCharts) {
		this.completedCharts = completedCharts;
	}

	public int getInCompleteCharts() {
		return inCompleteCharts;
	}

	public void setInCompleteCharts(int inCompleteCharts) {
		this.inCompleteCharts = inCompleteCharts;
	}

	public int getMiscCharts() {
		return miscCharts;
	}

	public void setMiscCharts(int miscCharts) {
		this.miscCharts = miscCharts;
	}

	public int getCnrCharts() {
		return cnrCharts;
	}

	public void setCnrCharts(int cnrCharts) {
		this.cnrCharts = cnrCharts;
	}

	public int getTotalCharts() {
		return totalCharts;
	}

	public void setTotalCharts(int totalCharts) {
		this.totalCharts = totalCharts;
	}

	public String getActualLoad() {
		return actualLoad;
	}

	public void setActualLoad(String actualLoad) {
		this.actualLoad = actualLoad;
	}

	public String getDailyLoad() {
		return dailyLoad;
	}

	public void setDailyLoad(String dailyLoad) {
		this.dailyLoad = dailyLoad;
	}

	public String getCompleteLoad() {
		return completeLoad;
	}

	public void setCompleteLoad(String completeLoad) {
		this.completeLoad = completeLoad;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJobLevel() {
		return jobLevel;
	}

	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}

}
