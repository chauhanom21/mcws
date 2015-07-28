package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.entity.ClientChartMapping;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UsersClient;

public interface ClientDetailsDao extends Dao<ClientDetails, Integer> {
	
	ClientDetails getClientDetailById(final int clientId);
	
	ClientDetails getClientByName(String name);
	
	List<UsersClient> getUsersClientByClientId(Integer clientId);
	
	List<ClientChartMapping> getClientChartsDetailsByClientId(final Integer clientId);
	
	List<Integer> getAllClientIds();
	
	List<ClientDetails> getClientDetailsNotINClientIds(List<Integer> clientIds);
	
	boolean saveOrUpdateUserClientDetails(final Coders user, final List<Integer> clientIds) throws Exception;
	
	public boolean saveOrUpdateUserChartSpeDetails(final Coders user, final List<Integer> chartSpeIds) throws Exception;
	

}
