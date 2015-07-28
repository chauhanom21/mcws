package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.Coders;

public class CoderCompletedLoadComparator implements Comparator<Coders> {

	@Override
	public int compare(Coders o1, Coders o2) {
		// TODO Auto-generated method stub
		return new Integer( (new Double(o1.getCompleteLoad()).compareTo
				(o2.getCompleteLoad())) );
		
	}

}
