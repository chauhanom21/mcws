package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.WorkListItem;

public class WorkListComparator implements Comparator<WorkListItem>  {

	
	public int compare(WorkListItem o1, WorkListItem o2) {
		// write logic for remaining TAT here--- write a pvt method..
		//return Double.compare(o1.getTat(), o2.getTat());
		return Double.compare(o1.getRemainingTat(), o2.getRemainingTat());
	}

}
