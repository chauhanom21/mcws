package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.WorkListItem;

public class EMWorkListComparator implements Comparator<WorkListItem>  {

	// list with DESC order w.r.t em value
	public int compare(WorkListItem o1, WorkListItem o2) {
		return Double.compare(o2.getEffortMetric(), o1.getEffortMetric());
	}

}
