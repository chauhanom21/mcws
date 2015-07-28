package com.eclat.mcws.service;

import java.util.List;

import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;

public interface CommonUtilService {

	/**
	 * 
	 * @param coder
	 * @return role of the user
	 */
	public String findRoleOfUser(final Coders coder);
	
	/**
	 * 
	 * @return List<ClientDetails>
	 */
	public List<ClientDetails> getAllClientDetails();
	
	/**
	 * 
	 * @param clientId, auditorId
	 * @return List<CodersQADto>
	 */
	public List<CodersQADto> getAllCodersDetailByClient(Integer clientId, Integer auditorId);
}
