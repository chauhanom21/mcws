package com.eclat.mcws.persistence.dao;

import java.sql.Timestamp;
import java.util.List;

import com.eclat.mcws.persistence.entity.WorkListItem;

public interface ReportDao {
	

	/**
	 * 
	 * @param clients
	 * @param fromDate
	 * @param toDate
	 * @return List of Client wise consolidation report data 
	 */
	public List getWorklistForClientwiseReport(final Integer[] clientIds, final String fromDate, final String toDate);

	/**
	 * 
	 * @param clientId
	 * @param fromDate
	 * @param toDate
	 * @return List of pending invoice report data 
	 */
	public List<Object[]> getPendingInvoiceReportDataByClientAndDateRange(final Integer clientId, final String fromDate, final String toDate);
	
	/**
	 * 
	 * @param clientId
	 * @param fromDate
	 * @param toDate
	 * @return List of completed invoice report data 
	 */
	public List<Object[]> getCompletedInvoiceReportDataByClientAndDateRange(final Integer clientId, final String fromDate, final String toDate);
	

	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List 
	 */
	public List<WorkListItem> generateFilewiseTATReport(final Timestamp fromDate, final Timestamp toDate);

	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param clientIds
	 * @return List of entries of done by coders with respect to client, where status is CNR, InComplete and Completed
	 */
	public List<Object[]> getCoderProductivityDataByDate(final String fromDate, final String toDate, final List<Integer> clientIds);
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List of entries of done by LocalQA with respect to client, where previous status is CNR, Complete and CompletedNR
	 */
	public List<Object[]> getLocalQAProductivityDataByDate(final String fromDate, final String toDate)throws Exception;
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List 
	 */
	public List<Object[]> generateUserTypeTrackingReport(final String fromDate, final String toDate, final String userType);
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getUserCNRChartCountByDate(final String fromDate, final String toDate, final String userRole) throws Exception;
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<Object[]> getCoderCNRChartDetailsByDate(final String fromDate, final String toDate, final Integer userId) throws Exception;
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<Object[]> getQACNRChartDetailsByDate(final String fromDate, final String toDate, final Integer userId) throws Exception;
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public List<Object[]> getUsersQualityReportDataByDateRange(final String fromDate, final String toDate) throws Exception;
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getMiscellaneousReportDataByDateRange(final String fromDate, final String toDate) throws Exception;
}
