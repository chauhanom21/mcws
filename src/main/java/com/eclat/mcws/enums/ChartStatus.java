package com.eclat.mcws.enums;

public enum ChartStatus {
	NotStarted("Not Started"),
	InComplete("InComplete"),
	Completed("Completed"),
	MISC("MISC"),

	CoderAssigned("Coder Assigned"),
	LocalQAAssigned("LocalQA Assigned"),
	GlobalQAAssigned("GlobalQA Assigned"),
	
	CodingInProgress("Coding InProgress"),
	LocalQAInProgress("LocalQA InProgress"),
	GlobalQAInProgress("GlobalQA InProgress"),
	
	LocalCNR("Local CNR"),
	GlobalCNR("Global CNR"),

	LocalAudit("Local Audit"),
	GlobalAudit("Global Audit"),
	LocalAudited("Local Audited"),
	GlobalAudited("Global Audited"),
	//Audited("Audited"),
	
	CompletedNR("CompletedNR");
	
	private String status;
	
	public String getStatus() {
		return status;		
	}
	
	private ChartStatus(String status) {
		this.status = status;
	}
	
	public static ChartStatus getChartStatus(String value) {
		ChartStatus chartStatus = null;
		for (ChartStatus status : ChartStatus.values()) {
			if (status.getStatus().equalsIgnoreCase(value)) {
				chartStatus = status;
				break;
			}
		}
		return chartStatus;
	}
	
	public static String getStatusSpanClass(final String status) {
		String spanClass = null;
		if ( status != null && ChartStatus.getChartStatus(status) != null) {
			switch (ChartStatus.getChartStatus(status)) {
			case NotStarted:
				spanClass = "label label-open";
				break;
			case CoderAssigned:
				spanClass = "label label-inprogress";
				break;
			case LocalQAAssigned:
				spanClass = "label label-inprogress";
				break;
			case GlobalQAAssigned:
				spanClass = "label label-inprogress";
				break;
			case CodingInProgress:
				spanClass = "label label-inprogress";
				break;
			case LocalQAInProgress:
				spanClass = "label label-inprogress";
				break;
			case GlobalQAInProgress:
				spanClass = "label label-inprogress";
				break;
			case LocalCNR:
				spanClass = "label label-cnr";
				break;
			case GlobalCNR:
				spanClass = "label label-cnr";
				break;
			case InComplete:
				spanClass = "label label-incomplete";
				break;
			case Completed:
				spanClass = "label label-success";
				break;
			case MISC:
				spanClass = "label label-misc";
				break;
		
			default :
				spanClass = "label label-open";
				break;
			}
		} else {
			spanClass = "label label-inprogress";
		}
		return spanClass;
	}

}
