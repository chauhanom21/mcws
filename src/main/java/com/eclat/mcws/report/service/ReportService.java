package com.eclat.mcws.report.service;

import java.util.List;
import java.util.Map;

import com.eclat.mcws.report.bean.DailyProductivityBean;
import com.eclat.mcws.report.bean.ReportQueryParam;
import com.eclat.mcws.report.bean.UserCNRReportBean;


public interface ReportService {
	
	/**
	 * 
	 * @param particularDate
	 * @return List
	 * @throws Exception
	 */
	public List<DailyProductivityBean> generateCoderDailyProductivityReportData(ReportQueryParam reportQueryParam) throws Exception; 
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return true/false
	 * @throws Exception
	 */
	public boolean generateLocalQADailyProductivityReportData(ReportQueryParam reportQueryParam) throws Exception; 
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return true/false
	 * @throws Exception
	 */
	public Map<String, Object> generateInvoiceReports(final ReportQueryParam reportQueryParam, Map<String, Object> responseMap) throws Exception;
	

	/**
	 * 
	 * @param clients
	 * @param fromDate
	 * @param toDate
	 * @return List
	 * @throws Exception
	 */
	public String generatetClientConsolidationReport(final String clients, final String fromDate, final String toDate) throws Exception;
	
	// generateFilewiseTATReportData
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return generated report path
	 * @throws Exception
	 */
	public String generateFilewiseTATReportData( final String fromDate, final String toDate) throws Exception;
	
	//generateUserTypeTrackingReportData
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return responseMap
	 * @throws Exception
	 */
	public Map<String, Object> generateUserTypeTrackingReportData( final String fromDate, final String toDate, Map<String,Object> responseMap ) throws Exception;
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return List
	 * @throws Exception
	 */
	public List<UserCNRReportBean> getUserCNRChartsCount(ReportQueryParam reportQueryParam) throws Exception;
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return List
	 * @throws Exception
	 */
	public List<UserCNRReportBean> getUserCNRChartDetails(UserCNRReportBean reportQueryParam) throws Exception;
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return true/false
	 * @throws Exception
	 */
	public boolean generateCodersQualityReports(final ReportQueryParam reportQueryParam, final String excelFileName) throws Exception;
	
	/**
	 * 
	 * @param reportQueryParam
	 * @return true/false
	 * @throws Exception
	 */
	public boolean generateSupervisorReport(final ReportQueryParam reportQueryParam) throws Exception;
	
	/**
	 * 
	 * @param queryParam
	 * @param excelFileName
	 * @return
	 * @throws Exception
	 */
	public boolean generateMiscellaneousReport(final ReportQueryParam queryParam, final String excelFileName) throws Exception;
	
}
