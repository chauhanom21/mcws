package com.eclat.mcws.report.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.ReportDao;
import com.eclat.mcws.persistence.dao.SupervisorDao;
import com.eclat.mcws.persistence.entity.ClientChartMapping;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.bean.ClientwiseReportBean;
import com.eclat.mcws.report.bean.CoderTrackingReportBean;
import com.eclat.mcws.report.bean.DailyProductivityBean;
import com.eclat.mcws.report.bean.InvoiceReportBean;
import com.eclat.mcws.report.bean.LocalQADailyProductivityBean;
import com.eclat.mcws.report.bean.LocalQATrackingReportBean;
import com.eclat.mcws.report.bean.ReportBean;
import com.eclat.mcws.report.bean.ReportQueryParam;
import com.eclat.mcws.report.bean.UserCNRReportBean;
import com.eclat.mcws.report.utility.ClientwiseReportUtility;
import com.eclat.mcws.report.utility.CoderTrackingReportUtility;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.report.utility.FilewiseTATReportUtility;
import com.eclat.mcws.report.utility.LocalQATrackingReportUtility;
import com.eclat.mcws.report.utility.ReportsUtility;
import com.eclat.mcws.util.comparator.ProductivityReportComparator;

@Service
public class ReportServiceImpl implements ReportService {

	private static final String REPORT_FILES_LOCATION = System.getProperty("catalina.base")+"/reports/";
	private static final String COMPLETED = "Completed";
	private static final String PENDING = "Pending";
	
	@Log
	private Logger logger;

	@Autowired
	private ClientwiseReportUtility clientwiseReportUtility;
	
	@Autowired
	private ReportsUtility reportUtility;
	
	@Autowired
	private FilewiseTATReportUtility filewiseTATREportUtility;
	
	@Autowired
	private LocalQATrackingReportUtility localQATrackingUtility;
	
	@Autowired CoderTrackingReportUtility coderTrackingUtility;

	@Autowired
	private SupervisorDao supervisorDao;

	@Autowired
	private ReportDao reportDao;

	@Autowired
	private ClientDetailsDao clientDetailDao;
	
	@Autowired
	private JasperDatasourceService datasourceService;

	@Override
	public List<DailyProductivityBean> generateCoderDailyProductivityReportData(ReportQueryParam reportQueryParam) throws Exception {
		// Modified End date value to that day 12 PM night time.
		// From date value to that day 12 AM start time.
		final String toDate = reportQueryParam.getToDate() + " 23:59:59:00";
		final String fromDate = reportQueryParam.getToDate() +" 00:00:01:00";
		
		final List<Integer> clientIds = clientDetailDao.getAllClientIds();
		final List<Object[]> coderTaskEntities = reportDao.getCoderProductivityDataByDate(fromDate, toDate, clientIds);
		if(coderTaskEntities != null) {
			
			List<DailyProductivityBean> reportBeanList  = reportUtility.generateCoderProductivityReportbeanList(coderTaskEntities);
			logger.debug("---> Constructed Daily Productivity Bean List for Coder :  "+reportBeanList);
			return reportBeanList;
		}
		return null;
	}
	
	@Override
	public boolean generateLocalQADailyProductivityReportData(ReportQueryParam reportQueryParam) throws Exception {
		// Modified End date value to that day 12 PM night time.
		// From date value to that day 12 AM start time.
		final String toDate = reportQueryParam.getToDate() + " 23:59:59:00";
		final String fromDate = reportQueryParam.getToDate() +" 00:00:01:00";
		
		final List<Object[]> localQATaskEntities = reportDao.getLocalQAProductivityDataByDate(fromDate, toDate);
		String csvFileName = "";
		if(localQATaskEntities != null && localQATaskEntities.size() > 0) {
			final String dirpath = REPORT_FILES_LOCATION + "daily_productivity_reports/";
			List<LocalQADailyProductivityBean> reportData  = reportUtility.generateLocaQAProductivityReportbeanList(localQATaskEntities);
			if(reportData != null && reportData.size() > 0) {
				/*
				 * Sorting the bean list by coder id and client name.
				 */
				Collections.sort(reportData, new ProductivityReportComparator());
				
				//construct csv file
				csvFileName = dirpath + "Daily Productivity-local QA.csv";
				reportUtility.createFileDirectory(dirpath, csvFileName);
				clientwiseReportUtility.generateLocalQAReportCSVFiles(reportData, csvFileName);
				logger.debug(" Generated CSV for invoice report !! @ the location : " + csvFileName);
			}
			reportQueryParam.setRelativePath(csvFileName);
			return true;
		}
		return false;
	}
	
	/**
	 * for Client wise Consolidation Report, 
	 * Generates the CSV file and return the file name. 
	 * Else return null if data not exist or in case of execption.
	 */
	public String generatetClientConsolidationReport(final String clients, final String startDate,
			final String toDate) throws Exception {
		// Modified End date value to that day night time.
		final String endDate = toDate + " 23:59:59";
		final Integer[] clientIds = parseStringToIntegerArray(clients);
		final List<WorkListItem> entities = reportDao.getWorklistForClientwiseReport(clientIds, startDate, endDate);
		if (null != entities && entities.size() > 0) {
			List<ClientwiseReportBean> reportList = clientwiseReportUtility.generateClientwiseReportBeanList(entities);
			if (null != reportList && reportList.size() > 0) {
				final String csvDirPath = REPORT_FILES_LOCATION + "client_consolidation_reports/";
				final String csvFileName = csvDirPath + "Clientwise_Consolidation_Report.csv";
				Boolean generatedCSV = clientwiseReportUtility.generateCsvFromList(reportList, csvDirPath, csvFileName);
				if (generatedCSV) {
					logger.debug(" Generated CSv for clientwise report !! @ the location : " + csvFileName);
					return csvFileName;
					
					//generatedExcelPath = clientwiseReportUtility.convertCsvToExcel(csvFileName);
					//logger.debug(" generated Excel file for clientwise report !!!");
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
		return null;
	}
		
	
	/**
	 * 
	 * @param clientId
	 * @param startDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> generateInvoiceReports(ReportQueryParam reportQueryParam, Map<String, Object> responseMap) throws Exception {
		// Modified End date value to that day night time.
		final String toDate = reportQueryParam.getToDate() + " 23:59:59";
		final Integer clientId = reportQueryParam.getClientId();
		final String fromDate = reportQueryParam.getFromDate();
		final ClientDetails clientDetail = clientDetailDao.find(clientId);

		reportQueryParam.setQueryClient(clientDetail.getName());
		final List<ClientChartMapping> clientChartDetails = clientDetailDao.getClientChartsDetailsByClientId(clientId);
		final String dirpath = REPORT_FILES_LOCATION + "invoice_reports/" + clientDetail.getName();
		
	
		final List<Object[]> compEntities = reportDao.getCompletedInvoiceReportDataByClientAndDateRange(clientId, fromDate, toDate);
		final List<Object[]> pendingEntities = reportDao.getPendingInvoiceReportDataByClientAndDateRange(clientId, fromDate, toDate);
		
		if( (compEntities == null || compEntities.size() == 0) && (pendingEntities == null || pendingEntities.size() == 0)){
			// seeting properties to responseMap
			responseMap.put("isDataAvailable", false);
		} else {
			// seeting properties to responseMap
			responseMap.put("isDataAvailable", true);
			
			if(compEntities != null && compEntities.size() >= 0) {
				logger.debug("=====> Generating reports data for COMPLETED charts ...");
				
				Map<String, List<InvoiceReportBean>> reportData = clientwiseReportUtility.
						generateInvoiceReportBeanListForCompletedChart(compEntities, clientChartDetails);
				generateCSVFiles(reportData, dirpath, COMPLETED, clientDetail.getName());
			}
			if(pendingEntities != null && pendingEntities.size() >= 0 ) {
				logger.debug("=====> Generating reports data for PENDING charts ...");
				
				Map<String, List<InvoiceReportBean>> reportData = clientwiseReportUtility.
						generateInvoiceReportBeanListForPendingChart(pendingEntities, clientChartDetails);
				generateCSVFiles(reportData, dirpath, PENDING, clientDetail.getName());
			}
		}
		reportQueryParam.setRelativePath(dirpath);
		responseMap.put("isCSVFilesGenerated", true);
		return responseMap;
	}

	private void generateCSVFiles( final Map<String, List<InvoiceReportBean>> reportData, final String dirpath,
			final String type, final String clientName) throws Exception {
		
		if ( reportData !=null && reportData.size() > 0 ) {
			
			logger.debug("=====> Generating CSV files for "+ type +" charts ...");
		
			reportUtility.createFileDirectory(dirpath, null);
			clientwiseReportUtility.generateInvoiceReportsCSVFile(reportData, dirpath, type, clientName);
		}
	}
	
	/**
	 * 
	 * @param startDate
	 * @param toDate
	 * @return true/false
	 * @throws Exception
	 */
	@Override
	public String generateFilewiseTATReportData( final String startDate, final String endDate) throws Exception {
		// Modified End date value to that day night time.
		final Date fromDate = com.eclat.mcws.util.DateUtil.convertStrToDate(startDate + " 00:00:00");
		final Date toDate = com.eclat.mcws.util.DateUtil.convertStrToDate(endDate + " 23:59:59");
		final List<WorkListItem> entities = reportDao.generateFilewiseTATReport(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()));
		
		if (null != entities && entities.size() > 0) {
			Map<WorkListItem, String> tatExceededItems = filewiseTATREportUtility.getExceededTATCharts(entities);
			logger.debug(" ====> Total TAT Exceeded items : "+tatExceededItems.size());
			if(tatExceededItems.size() > 0 ) {			 
				final String csvDirPath = REPORT_FILES_LOCATION + "file_wise_tat_reports/" ;
				String csvFileName = csvDirPath + "Filewise_TAT_Report.csv";
				return filewiseTATREportUtility.generateFilewiseTATReport(tatExceededItems, csvDirPath, csvFileName);
			} 
		} 
		return null;
	}
	
	
	
	@Override
	public Map<String, Object> generateUserTypeTrackingReportData( final String fromDate, final String toDate, Map<String,Object> responseMap ) throws Exception{
		
		// Modified End date value to that day night time.
		final String endDate = toDate + " 23:59:59";
		String generatedExcelPath = null;
		final String userType = responseMap.get("userType").toString();
		final List<Object[]> entities = reportDao.generateUserTypeTrackingReport(fromDate, endDate, userType);
		
		if(null == entities || entities.size() == 0) {
			responseMap.put("isDataAvailable", false);
			return responseMap;
		} else {
			// seeting properties to responseMap
			responseMap.put("isDataAvailable", true);
			
			if("LocalQA".equalsIgnoreCase(userType)){
				List<LocalQATrackingReportBean> entitiesList = reportUtility.constructLocaQATrackingReportbeanList(entities);
				Integer count = 1;
				for (LocalQATrackingReportBean bean : entitiesList) {
					bean.setsNo(count);
					bean.setFromDate(fromDate);
					bean.setToDate(toDate);
					count ++;
				}
				responseMap.put("reportData", entitiesList);
				
				final String csvDirPath = REPORT_FILES_LOCATION + "localQA_tracking_reports/" ;
				String csvFileName = csvDirPath + "LocalQA_Tracking_Report.csv";
				generatedExcelPath = localQATrackingUtility.generateLocalQATrackingReport(entitiesList, csvDirPath, csvFileName, responseMap);

			} else if("Coder".equalsIgnoreCase(userType)){
				List<CoderTrackingReportBean> entitiesList = reportUtility.constructCoderTrackingReportbeanList(entities);
				Integer count = 1;
				for (CoderTrackingReportBean bean : entitiesList) {
					bean.setsNo(count);
					bean.setFromDate(fromDate);
					bean.setToDate(toDate);
					count ++;
				}
				responseMap.put("reportData", entitiesList);
				
				final String csvDirPath = REPORT_FILES_LOCATION + "coder_tracking_reports/" ;
				String csvFileName = csvDirPath + "Coder_Tracking_Report.csv";
				generatedExcelPath = coderTrackingUtility.generateCoderTrackingReport(entitiesList, csvDirPath, csvFileName, responseMap);
				// seeting properties to responseMap
			}
			responseMap.put("downloadFilePath", generatedExcelPath);
			logger.debug(" generated Excel file for  UserType(qa/coder) Tracking report !!!");	
			
		}
		
		return responseMap;
		
	}
		
	public List<UserCNRReportBean> getUserCNRChartsCount(ReportQueryParam reportQueryParam) throws Exception {
		/**
		 *  Modified toDate value to that day 12 PM night time.
		 *  And fromDate value to that day 12 AM start time.
		 */
		final String toDate = reportQueryParam.getToDate() + " 23:59:59:00";
		final String fromDate = reportQueryParam.getFromDate() +" 00:00:01:00";
		final List<Object[]> dataEntities = reportDao.getUserCNRChartCountByDate(fromDate, toDate, reportQueryParam.getType());
		
		if(dataEntities != null) {
			final List<UserCNRReportBean> userCNRReportList = new ArrayList<UserCNRReportBean>();
			
			for(Object[] entity : dataEntities) {
				UserCNRReportBean userRepBean = new UserCNRReportBean();
				userRepBean.setUserId((Integer)entity[0]);
				userRepBean.setEmpId((String)entity[5]);
				userRepBean.setEmpName((String) entity[1] +" "+ (String) entity[2]);
				userRepBean.setEmpRole(reportQueryParam.getType());
				userRepBean.setCnrCount(((BigInteger)entity[4]).intValue());
				userRepBean.setFromDate(reportQueryParam.getFromDate());
				userRepBean.setToDate(reportQueryParam.getToDate());
				userCNRReportList.add(userRepBean);
			}
			return userCNRReportList;
		}
		return null;
	}
	
	public List<UserCNRReportBean> getUserCNRChartDetails(UserCNRReportBean reportQueryParam) throws Exception {
		/**
		 *  Modified toDate value to that day 12 PM night time.
		 *  And fromDate value to that day 12 AM start time.
		 */
		final String toDate = reportQueryParam.getToDate() + " 23:59:59:00";
		final String fromDate = reportQueryParam.getFromDate() +" 00:00:01:00";
		
		
		if( reportQueryParam.getEmpRole().equals("coder") ) {
			final List<Object[]> dataEntities = reportDao.getCoderCNRChartDetailsByDate(fromDate, toDate, reportQueryParam.getUserId());
			if(dataEntities != null) {
				final List<UserCNRReportBean> userCNRReportList = new ArrayList<UserCNRReportBean>();
				
				for(Object[] entity : dataEntities) {
					UserCNRReportBean userRepBean = new UserCNRReportBean();
					System.out.println("CLASS :  " +entity[0].getClass());
					System.out.println( "" +getEmployeeId(entity[0]));
					
						userRepBean.setEmpId(getEmployeeId(entity[0]));
					
					userRepBean.setClientName((String) entity[1]);
					userRepBean.setChartType((String)entity[2]);
					userRepBean.setCnrCount(((BigInteger)entity[5]).intValue());
					userRepBean.setFromDate(DateUtil.fomatDate(reportQueryParam.getFromDate()));
					userRepBean.setToDate(DateUtil.fomatDate(reportQueryParam.getToDate()));
					userCNRReportList.add(userRepBean);
				}
				return userCNRReportList;
			}
		} else if (reportQueryParam.getEmpRole().equals("qa") ) {
			final List<Object[]>dataEntities = reportDao.getQACNRChartDetailsByDate(fromDate, toDate, reportQueryParam.getUserId());
			if(dataEntities != null) {
				final List<UserCNRReportBean> userCNRReportList = new ArrayList<UserCNRReportBean>();
				
				for(Object[] entity : dataEntities) {
					UserCNRReportBean userRepBean = new UserCNRReportBean();
					userRepBean.setEmpId(getEmployeeId(entity[0]));
					userRepBean.setClientName((String) entity[1]);
					userRepBean.setChartType((String)entity[2]);
					userRepBean.setCompletedCount(((BigInteger)entity[3]).intValue());
					userRepBean.setCnrCount(((BigInteger)entity[4]).intValue());
					userRepBean.setFromDate(DateUtil.fomatDate(reportQueryParam.getFromDate()));
					userRepBean.setToDate(DateUtil.fomatDate(reportQueryParam.getToDate()));
					userCNRReportList.add(userRepBean);
				}
				return userCNRReportList;
			}
		}
		return null;
	}
	
	private String getEmployeeId(Object empId) {
		String employeeId = "";
		if( empId instanceof Integer)
			employeeId = ((Integer)empId).toString();
		else
			employeeId =  (String)empId;
		return employeeId;
	}
	
	/**
	 * 
	 * @param clientId
	 * @param startDate
	 * @param toDate
	 * @return TRUE/FALSE
	 * @throws Exception
	 */
	public boolean generateCodersQualityReports(ReportQueryParam reportQueryParam, final String excelFileName) throws Exception {
		// Modified End date value to that day night time.
		final String toDate = reportQueryParam.getToDate() + " 23:59:59";
		final String fromDate = reportQueryParam.getFromDate();

		final String dirpath = REPORT_FILES_LOCATION + "quality_reports/";
		final List<Object[]> entities = reportDao.getUsersQualityReportDataByDateRange(fromDate, toDate);
		
		if(entities != null && entities.size() > 0) {
			logger.debug("=====> Generating CODER QUALITY reports.");
			
			String fileName = dirpath + excelFileName;
			reportQueryParam.setRelativePath(fileName);
			return coderTrackingUtility.generateCoderQualityReport(entities, dirpath, fileName);
		
		} else {
			return false;
		}
	}
	
	public Integer[] parseStringToIntegerArray(String str) {
    	final String[] values = str.split(",");
    	final Integer[] ids = new Integer[values.length];
    	for(int i=0; i<values.length;i++){
    		try {
    			ids[i] = Integer.parseInt(values[i]); 
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Not a number: " + str + " at index " + i, e);
            }
    	}
    	return ids;
	}
	
	@Override
	public boolean generateSupervisorReport(ReportQueryParam reportQuery) throws Exception {
		// Modified End date value to that day 12 PM night time.
		// From date value to that day 12 AM start time.
		final String toDate = reportQuery.getToDate() + " 23:59:59:00";
		final String fromDate = reportQuery.getFromDate() +" 00:00:01:00";
		
		final List<ReportBean> reportBeanList = datasourceService.getReportBeanList(fromDate, toDate, 
				reportQuery.getQueryClient(), reportQuery.getChartType());
		String csvFileName = "";
		if(reportBeanList != null && reportBeanList.size() > 0) {
			final String dirpath = REPORT_FILES_LOCATION + "supervisor_report/";
				//construct csv file
				csvFileName = dirpath + "supervisor_report.csv";
				reportUtility.createFileDirectory(dirpath, csvFileName);
				clientwiseReportUtility.generateSupervisorReportCSVFiles(reportBeanList, csvFileName);
				logger.debug(" Generated CSV for invoice report !! @ the location : " + csvFileName);
				reportQuery.setRelativePath(csvFileName);
				return true;
		}
		return false;
	}
	
	@Override
	public boolean generateMiscellaneousReport(final ReportQueryParam queryParam, 
			final String excelFileName) throws Exception {
		final String fromDate = queryParam.getFromDate() +" 00:00:00:01";
		final String toDate = queryParam.getToDate() + " 23:59:59:01";
		final String dirpath = REPORT_FILES_LOCATION + "miscellaneous_reports/";
		final List<Object[]> entities = reportDao.getMiscellaneousReportDataByDateRange(fromDate, toDate);
		if(entities != null && entities.size() > 0) {
			logger.debug("=====> Generating MISC REPORT ....");
			String fileName = dirpath + excelFileName;
			queryParam.setRelativePath(fileName);
			return reportUtility.generateMiscellaneousReport(entities, dirpath, fileName);
		} 
		return false;
	}
	
}
