package com.eclat.mcws.service;

import java.util.List;
import java.util.Map;

import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.dto.GradingSheetDetails;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.dto.TaskDetails;

public interface AuditDataService {

	/**
	 * 
	 * @param restQueryDetails
	 * @param response
	 * @return
	 * @throws Exception
	 */
	TaskDetails fetchTaskToAudit(ChartRestQueryDetails restQueryDetails, Response<TaskDetails> response) throws Exception;

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	List<TaskDetails> getAuditInProgressCharts(Integer userId) throws Exception;
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param coderId
	 * @return
	 * @throws Exception
	 */
	public List<TaskDetails> getGlobalQaAuditItemsByUpdateDateRange(
			String startDate, String endDate, Integer coderId) throws Exception;
	
	/**
	 * 
	 * @param userId
	 * @param taskId
	 * @throws Exception
	 */
	public void assignedAuditChartToUser(Integer userId, Long taskId)
			throws Exception ;
	
	/**
	 * 
	 * @param chartType
	 * @return
	 * @throws Exception
	 */
	public Map<String, Double> getAllWeightageMasterDataByChartType(final String chartType)  throws Exception;
	
	/**
	 * 
	 * @param gradingSheetDetails
	 * @return
	 * @throws Exception
	 */
	public boolean saveAuditChart(final GradingSheetDetails gradingSheetDetails) throws Exception;
	
	/**
	 * 
	 * @param chartId
	 * @param userId
	 * @return GradingSheetDetails with all require detail
	 * 
	 * This method construct GradingSheetDetails and assign the selected chart to auditor
	 */
	public GradingSheetDetails getAuditedChartDetails(final long chartId, final int userId);
}
