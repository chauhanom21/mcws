package com.eclat.mcws.service;

import java.util.List;

import com.eclat.mcws.dto.TaskDetails;

public interface SearchService {

	/**
	 * 
	 * @param taskId
	 * @return LIST
	 */
	public List<TaskDetails> getTaskDetails(final Long taskId);
	
	/**
	 * 
	 * @param queryParam
	 * @return List
	 */
	public List<TaskDetails> searchTaskDetailsByAccountMRNumber(final String queryParam);
	
}
