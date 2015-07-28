package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.WorkListItem;

public class WorklistIdDescComparator implements Comparator<WorkListItem> {

	@Override
	public int compare(WorkListItem o1, WorkListItem o2) {
		// TODO Auto-generated method stub
		return (o2.getId().compareTo(o1.getId()));
	}
}
