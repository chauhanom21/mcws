package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;

public interface UserClientChartDao {

	public List<UsersClient> getUsersClientDetailByUserId(final int userId);

	public UserCharts getUserChartsById(final int Id);

	public List<UserCharts> getUsersChartsDetailByUserId(final int userId);
	
	/**
	 * 
	 * @param userId
	 * @param specializationIds
	 * @return
	 */
	public List<UserCharts> getUserChartByUserIdAndSpecializationIds(final int userId, 
			final List<Integer> specializationIds);
	
	
}
