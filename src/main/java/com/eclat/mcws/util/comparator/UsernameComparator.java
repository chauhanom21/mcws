package com.eclat.mcws.util.comparator;

import java.util.Comparator;

import com.eclat.mcws.admin.config.dto.UserDetails;

public class UsernameComparator implements Comparator<UserDetails>  {
	
	@Override
	public int compare(UserDetails o1, UserDetails o2) {
		// TODO Auto-generated method stub
		return new String(o1.getUsername()).compareTo(o2.getUsername());
	}

}
