package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.Coders;

/**
 * 
 * @author Om Prakash chauhan
 * 
 * Compare coder's total daily load (dailyWorkload + completedLoad)
 *
 */
public class CoderDailyLoadComparator implements Comparator<Coders> {

	@Override
	public int compare(Coders o1, Coders o2) {
		// TODO Auto-generated method stub
		return new Integer((new Double(o1.getCompleteLoad() + o1.getCoderDailyWorkLoad())).compareTo(o2
				.getCompleteLoad() + o2.getCoderDailyWorkLoad()));

	}

}
