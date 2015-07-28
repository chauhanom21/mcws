package com.eclat.mcws.service;

import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.UserDetails;
import com.eclat.mcws.dto.UserProfile;
import com.eclat.mcws.persistence.entity.Users;

public interface UserService {

	public UserProfile getUserProfile();
	
	public Users getLoggedInUserDetails();
	
	public Boolean updateUserAvailability(final CodersQADto coderQa);

}
