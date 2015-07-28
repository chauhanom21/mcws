package com.eclat.mcws.admin.config.service;

import java.util.List;

import com.eclat.mcws.admin.config.dto.UserDetails;
import com.eclat.mcws.persistence.entity.Coders;

public interface UserConfigService {
	/**
	 * 
	 * @return All user's details
	 */
	public List<UserDetails> getAllUserDetails();
	
	/**
	 * 
	 * @param userId
	 * @param loadClientDetail
	 * @return user detail by user id
	 */
	public UserDetails getUserDetailsById(final Integer userId, final boolean loadClientDetail);
	
	/**
	 * 
	 * @param userDetails
	 * @return user detail
	 */
	public Coders saveOrUpdateUser(final UserDetails userDetails);
	
	/**
	 * 
	 * @param userId
	 * @param clientIds
	 * @return true/false
	 */
	public boolean saveOrUpdateUserClientDetails(final Integer userId, final List<Integer> clientIds) throws Exception;
	
	/**
	 * 
	 * @param userId
	 * @param chartSpeIds
	 * @return true/false
	 * @throws Exception
	 */
	public boolean saveOrUpdateUserChartSpeDetails(final Integer userId, final List<Integer> chartSpeIds) throws Exception;
	
}
