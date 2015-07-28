package com.eclat.mcws.service;

import java.util.List;

import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.CodingChartData;
import com.eclat.mcws.dto.TaskDetails;

public interface CoderService {
	
	List<TaskDetails> getActiveCharts(int coder) throws Exception;
	
	boolean isCoder(int coder);
	
	List<TaskDetails> getCompletedCharts(int coder, String date) throws Exception;
	
	List<TaskDetails> getCompletedChartsByDateRange(int coderId, String fromDate, String toDate) throws Exception;
	
	List<TaskDetails> getInProgressCharts(int coder) throws Exception;
	
	List<TaskDetails> fetchAllWorkLists(int coderId, String date) throws Exception;
	
	TaskDetails updateWorkList(long workListId, String status) throws Exception;	
	
	public List<CodersQADto> getAllCodersData(final Integer client, final Integer chartSpecId);
	
	public List<CodersQADto> getAllLocalQAData(final Integer client, final Integer chartSpecId);
	
	public List<CodersQADto> getAllRemoteQAData(final Integer client, final Integer chartSpecId);
	
	public List<String> getAllUsersName();
	
	public List<String> getAllClientsName();
	
	//getAllClientIds
	public List<Integer> getAllClientIds();
	TaskDetails getWorkListDetails(String queryString) throws Exception;
	
	public boolean  saveOrUpdateCodingChartDetails(final CodingChartData codingChartData) throws Exception;	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void updateWorkStartTimeAndTaskStatus(final Long worklistId, final Integer userId, final String workType) throws Exception;
	
	/**
	 * 
	 * @param notes
	 */
	public void saveOrUpdateNotes(final String notes, final Integer userId) throws Exception;
	
	/**
	 * 
	 * @param userId
	 * @return String
	 * @throws Exception
	 */
	public String loadUserNotesValue(final Integer userId) throws Exception;
	
	public Boolean updateStatusToInProgress(Long taskId) throws Exception;
	
	/**
	 * 
	 * @param client
	 * @param chartType
	 * @param chartSpl
	 * @param status
	 * @return
	 */
	public List<CodersQADto> getAllCodersORLocalQAByClientChartTypeAndChartSpl(final String client, final String chartType,
			final String chartSpl, final String status);
	
}
