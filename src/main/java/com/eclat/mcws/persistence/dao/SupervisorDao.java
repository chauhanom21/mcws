package com.eclat.mcws.persistence.dao;


public interface SupervisorDao {

	// quartz job
	public void executeResetCompletedLoadForAll(String userType);
	
	
}
