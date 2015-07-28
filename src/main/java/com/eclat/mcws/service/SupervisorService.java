package com.eclat.mcws.service;

import java.util.List;

import org.json.simple.JSONArray;

import com.eclat.mcws.dto.AuditParamEntity;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.CoderOrQaDetail;
import com.eclat.mcws.dto.DashboardBean;
import com.eclat.mcws.dto.TaskDetails;

public interface SupervisorService {

	public List<CodersQADto> getAllCoderAndQADetails() throws Exception;

	public CoderOrQaDetail getCoderOrQaDetailByIdAndDate(final int userId, final String fromDate, final String toDate) throws Exception;
	
	public List<TaskDetails> getAllTaskDetails(final String date) throws Exception;
	
	/**
	 * 
	 * @param id
	 * @param coderId
	 * @param localQAId
	 * @param remoteQAId
	 * @param effortMetric
	 * @param status
	 * @return True/False
	 */
	public boolean updateWorkListItem(final long id, final int coderId, final int localQAId, final int remoteQAId, 
			final double effortMetric, final String status);
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	public JSONArray getSupervisorReportsData(final String fromDate, final String toDate) throws Exception;
	
	/**
	 * 
	 * @param auditParam
	 * @param message
	 * @return True/False
	 * @throws Exception
	 */
	public boolean createAuditData(final AuditParamEntity auditParam, String message) throws Exception;
	
	//quartz job
	public void executeResetCompletedLoadForAll(String userType) throws Exception;
	
	/**
	 * 
	 * @return Dashboard 
	 */
	public DashboardBean getSupervisorDashboardData();
		
	/**
	 * 
	 * @param taskId
	 * @param coderId
	 * @param status
	 * @return True/False
	 */
	public boolean assignWorkListItemToCoder(final List<Long> taskId, final Integer coderId, final String status);
	
	/**
	 * 
	 * @param ids
	 * @return True if data successfully  removed else return False.
	 */
	public boolean deleteWorklistItems(final List<Long> ids);
}
