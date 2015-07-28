package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.WorkListItem;

public class WorklistDisDateComparator implements Comparator<WorkListItem> {

	@Override
	public int compare(WorkListItem o1, WorkListItem o2) {
		// TODO Auto-generated method stub
		return new Integer(o1.getDischargedDate().compareTo(o2.getDischargedDate()));
	}
}
