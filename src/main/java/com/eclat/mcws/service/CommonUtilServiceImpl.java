package com.eclat.mcws.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UsersClient;

@Service
public class CommonUtilServiceImpl implements CommonUtilService {

	@Autowired
	ClientDetailsDao clientDetailDao;
	
	@Autowired
	CoderDao coderDao;
	
	public String findRoleOfUser(final Coders coder) {
		if (coder.getCoder()) {
			return UserRoles.Coder.toString();
		} else if (coder.getLocalQa()) {
				return UserRoles.LocalQA.toString();
		} else if(coder.getRemoteQa()) {
				return UserRoles.RemoteQA.toString();
		}
		return null;
	}
	
	public List<ClientDetails> getAllClientDetails() {
		return clientDetailDao.findAll();
	}
	
	public List<CodersQADto> getAllCodersDetailByClient(Integer clientId, Integer auditorId) {
		final Coders auditorUser = coderDao.find(auditorId); 
		final List<UsersClient> usersClients = coderDao.getAllUserClientsByClientId(clientId);
		List<CodersQADto> codersQAList = new ArrayList<CodersQADto>();
		for(UsersClient userClient : usersClients){
			final Coders coder  = userClient.getCoder();
			CodersQADto coderQA = new CodersQADto(coder.getUserId(), coder.getFirstname());
			
			if(auditorUser.getRemoteQa() && !auditorUser.getLocalQa() && coder.getLocalQa()) {
				codersQAList.add(coderQA);
			} else if(auditorUser.getLocalQa() && !coder.getRemoteQa() && coder.getCoder()){
				codersQAList.add(coderQA);
			}
			
		}
		return codersQAList;
	}
}
