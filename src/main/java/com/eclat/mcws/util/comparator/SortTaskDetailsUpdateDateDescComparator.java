package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.dto.TaskDetails;

public class SortTaskDetailsUpdateDateDescComparator implements Comparator<TaskDetails> {

	@Override
	public int compare(TaskDetails o1, TaskDetails o2) {
		// TODO Auto-generated method stub
		return new Integer(o2.getUpdateDateMiliis().compareTo(o1.getUpdateDateMiliis()));
	}
}
