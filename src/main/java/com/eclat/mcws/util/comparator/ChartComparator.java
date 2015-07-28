package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.UserCharts;

public class ChartComparator implements Comparator<UserCharts>  {
	
	public int compare(UserCharts o1, UserCharts o2) {
		return new Integer(o1.getPriority()).compareTo(o2.getPriority());
	}

}
