package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.persistence.entity.UsersClient;

public class ClientComparator implements Comparator<UsersClient>  {

	
	public int compare(UsersClient o1, UsersClient o2) {
		return new Integer(o1.getPriority()).compareTo(o2.getPriority());
	}

}
