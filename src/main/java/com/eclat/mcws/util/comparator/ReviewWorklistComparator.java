package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.ReviewWorklist;

public class ReviewWorklistComparator implements Comparator<ReviewWorklist>{

	@Override
	public int compare(ReviewWorklist o1, ReviewWorklist o2) {
		// TODO Auto-generated method stub
		return new Integer(o2.getUpdatedDate().compareTo(o1.getUpdatedDate()));
	}

}
