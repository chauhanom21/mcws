package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.report.bean.LocalQADailyProductivityBean;

public class ProductivityReportComparator implements Comparator<LocalQADailyProductivityBean>  {
	
	@Override
	public int compare(LocalQADailyProductivityBean o1, LocalQADailyProductivityBean o2) {
		// TODO Auto-generated method stub
		int diff = new String(o1.getEmpcode()).compareTo(o2.getEmpcode());
		if(diff == 0) {
			diff = o1.getClientName().compareTo(o2.getClientName());
		}
		return diff;
	}

}
