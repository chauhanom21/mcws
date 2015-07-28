package com.eclat.mcws.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.report.bean.ReportQueryParam;
import com.eclat.mcws.report.bean.UserCNRReportBean;
import com.eclat.mcws.report.service.DownloadService;
import com.eclat.mcws.report.service.ReportService;
import com.eclat.mcws.service.SupervisorService;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;

@Controller
@RequestMapping("/report")
public class ReportController {

	@Log
	private Logger logger;

	@Autowired
	private DownloadService downloadService;
	
	@Autowired
	private SupervisorService supervisorService;
	
	@Autowired
	private ReportService reportService;

	@RequestMapping(value="/download")
	public void download(@RequestParam String type, @RequestParam String fromDate, @RequestParam String toDate, 
			@RequestParam String client, @RequestParam String chartType, HttpServletResponse response) {
		
		logger.debug("*** Request Param:  : type = "+type + " fromDate = "+fromDate +" toDate = "+toDate +
				"client : "+client + "  chartType : "+chartType);
		
		ReportQueryParam requestParam = constructReportQueryParam(fromDate, toDate, null, null, client, chartType);
		downloadService.downloadSupervisorReport(response, requestParam);
		//downloadService.download(type, response, fromDate, toDate, client, chartType);
	}
	
	/** Creating map for storing some <key,val> pairs to represent the Report Response
	* map properties are 
	* 1. "isDataAvailable" - true/false
	* 2. "csvFilePath" - "generated csv file/files location"
	* 3. "downloadFilePath" - "path of the downloadable file - excel/csv/zip"
	* 4. "reportData" - List
	*/
	/**
	 * 
	 * @param queryClient
	 * @param fromDate
	 * @param toDate
	 * @param response
	 */
	@RequestMapping(value = "/clientConsolidationReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<Object> getClientConsolidationReportData(@RequestParam String clients,
			@RequestParam String fromDate,
			@RequestParam String toDate,  HttpServletResponse response) {
		logger.debug("queryClient :  " + clients + " &&" + fromDate + " && " + toDate);
		Response<Object> responseJS = new Response<Object>();
		try {
			final String generatedFile = reportService.generatetClientConsolidationReport(clients, fromDate, toDate);
			if(generatedFile != null) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
				downloadService.downloadClientwiseConsolidationReport(response, generatedFile);
			} else {
				responseJS.setMessage("No Data is Available for the report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}
		return responseJS;
	}
	
	/**
	 * 
	 * @param queryClient
	 * @param fromDate
	 * @param toDate
	 * @param response
	 */
	@RequestMapping(value = "/dailyProductivityReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<Object> getDailyProductivityReportData(@RequestParam String reportType,
			@RequestParam String toDate,  HttpServletResponse response) {
		
		logger.debug("reportType {}, todate {} ", new Object[]{reportType, toDate});
		
		ReportQueryParam requestParam = constructReportQueryParam(null, toDate, null, reportType, null, null);
		Response<Object> responseJS = new Response<Object>();
		try {
				if (reportType.equalsIgnoreCase("coder")) {
					/*
					 * It generate the daily productivity report of coder.
					 */
					Map<String,Object> responseMap = downloadService.downloadDailyProductivityReport(response, requestParam);
					
					if(responseMap.containsKey("isDataAvailable") && Boolean.parseBoolean(responseMap.get("isDataAvailable").toString())) {
						responseJS.setMessage("Data Available for the report !!");
						responseJS.setStatus(ResponseStatus.OK);
						responseJS.setPayLoad(responseMap.get("reportData"));
						downloadService.downloadClientwiseConsolidationReport(response, responseMap.get("downloadFilePath").toString());
					} else {
						responseJS.setMessage("No Data is Available for the report !!");
						responseJS.setStatus(ResponseStatus.ERROR);
					}			
					
				} else if (reportType.equalsIgnoreCase("localqa")){
					/*
					 * It generate the daily productivity report of LocalQA.
					 */
					downloadService.downloadLocalQADailyProductivityReport(response, requestParam);
					if(requestParam.getIsDataAvailable()){
						responseJS.setMessage("Data Available for the report !!");
						responseJS.setStatus(ResponseStatus.OK);						
					} else  {
						responseJS.setMessage("No Data is Available for the report !!");
						responseJS.setStatus(ResponseStatus.ERROR);
					}
						
				}
				
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}
		return responseJS;
	}
	
	/**
	 * 
	 * @param queryClient
	 * @param fromDate
	 * @param toDate
	 * @param response
	 * 
	 * It's download the client invoice reports. Download all data for selected client
	 * By between selected date range. 
	 */
	@RequestMapping(value = "/clientInvoiceReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<Object> getInvoiceReportByClientAndDate(@RequestParam String fromDate, @RequestParam String toDate, 
			@RequestParam Integer clientId, HttpServletResponse response, HttpServletRequest request) {
		
		ReportQueryParam requestParam = constructReportQueryParam(fromDate, toDate, clientId, null, null, null);
		Response<Object> responseJS = new Response<Object>();
		try {
			/**
			 * This service gets the data from DB, generate all reports file based on client and keep all
			 * flies into one directory and download all the reports in single zip file.
			 */
			Map<String,Object> responseMap = downloadService.downloadInvoiceReport(request, response, requestParam);
			if(responseMap.containsKey("isDataAvailable") && Boolean.parseBoolean(responseMap.get("isDataAvailable").toString())) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
			} else {
				responseJS.setMessage("No Data is Available for the report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch (Exception e) {
			logger.error(" Got Exception loading invoice report:: ", e);
		}
		return responseJS;
	}
	

	/**
	 * getFilewiseTATReportData
	 * @param fromDate
	 * @param toDate
	 * @param response
	 */
	@RequestMapping(value = "/filewiseTATReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<Object> getFilewiseTATReportData( @RequestParam String fromDate,
			@RequestParam String toDate,  HttpServletResponse response) {
		
		logger.debug("fromDate :  " +  fromDate + " && toDate : " + toDate);
		Response<Object> responseJS = new Response<Object>();
		Boolean isDataAvailable = false;
		String downloadFilePath = null;
		try {
			downloadFilePath = reportService.generateFilewiseTATReportData( fromDate, toDate );
			if(downloadFilePath != null) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
				downloadService.downloadFilewiseTATReport(response, downloadFilePath);
			} else {
				responseJS.setMessage("No Data is Available for the report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}
		return responseJS;
	}
	
	/**
	 * getLocalQATrackingReportData
	 * @param fromDate
	 * @param toDate
	 * @param response
	 */
	@RequestMapping(value = "/userTrackingReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<Object> getUserTypeTrackingReportData( @RequestParam String fromDate,
			@RequestParam String toDate, @RequestParam String userType, HttpServletResponse response) {
		
		logger.debug("fromDate :  " +  fromDate + " && toDate : " + toDate + " && userType : " + userType);
		Map<String,Object> responseMap = null;
		Response<Object> responseJS = new Response<Object>();
		responseMap = new HashMap<String,Object>();
		try {
			// put 'userType' in the responseMap
			responseMap.put("userType", userType);
			responseMap = reportService.generateUserTypeTrackingReportData( fromDate, toDate, responseMap);
			if(responseMap.containsKey("isDataAvailable") && Boolean.parseBoolean(responseMap.get("isDataAvailable").toString())) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
				responseJS.setPayLoad(responseMap.get("reportData"));
				downloadService.downloadUserTypeTrackingReport(response, responseMap.get("downloadFilePath").toString(), userType);
			} else {
				responseJS.setMessage("No Data is Available for the report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}
		return responseJS;
	}
	
	@RequestMapping(value = "/userCNRChartsCount", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<UserCNRReportBean>> getUserCNRChartsCount(@RequestParam String userType,
			@RequestParam String fromDate, @RequestParam String toDate,  HttpServletResponse response) {
		
		ReportQueryParam requestParam = constructReportQueryParam(fromDate, toDate, null, userType, null, null);
		Response<List<UserCNRReportBean>> responseJS = new Response<List<UserCNRReportBean>>();
		try {
					
			List<UserCNRReportBean> cnrReportsBean = reportService.getUserCNRChartsCount(requestParam);
			
			if(cnrReportsBean != null) {
				responseJS.setStatus(ResponseStatus.OK);
				responseJS.setStatusCode(200);
				responseJS.setPayLoad(cnrReportsBean);
			} else {
				responseJS.setStatus(ResponseStatus.ERROR);
				responseJS.setStatusCode(501);
			}			
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
			responseJS.setStatus(ResponseStatus.ERROR);
			responseJS.setStatusCode(501);
		}
		return responseJS;
	}
	
	@RequestMapping(value = "/userCNRReportDetails", method = RequestMethod.POST)
	public @ResponseBody
	Response<List<UserCNRReportBean>> getUserCNRReportDetails(@RequestBody UserCNRReportBean requestParam,
			HttpServletResponse response) {
		
		Response<List<UserCNRReportBean>> responseJS = new Response<List<UserCNRReportBean>>();
		try {
			List<UserCNRReportBean> cnrReportsBean = reportService.getUserCNRChartDetails(requestParam);
			
			if(cnrReportsBean != null) {
				responseJS.setStatus(ResponseStatus.OK);
				responseJS.setStatusCode(200);
				responseJS.setPayLoad(cnrReportsBean);
			} else {
				responseJS.setStatus(ResponseStatus.ERROR);
				responseJS.setStatusCode(501);
			}			
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
			responseJS.setStatus(ResponseStatus.ERROR);
			responseJS.setStatusCode(501);
		}
		return responseJS;
	}
	
	@RequestMapping(value = "/coderwiseQualityReport", method = RequestMethod.GET)
	public @ResponseBody
	Response<String> coderQualityReport(@RequestParam String fromDate, @RequestParam String toDate,  
			@RequestParam String userType, HttpServletResponse response) {
		
		Response<String> responseJS = new Response<String>();
		boolean isDataAvailable = false;
		try {
			
			ReportQueryParam requestParam = constructReportQueryParam(fromDate, toDate, null, userType, null, null);
			/**
			 * This service gets the audited chart data from DB, 
			 * And Generates reports and return true.
			 * If data does not exits then return false.
			 */
			isDataAvailable = downloadService.downloadCodersQualityReports(response, requestParam);
			
			if(isDataAvailable) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
				responseJS.setStatusCode(200);
			} else {
				responseJS.setMessage("No Data is Available for quality report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch(final Exception e){
			logger.error("Got Exception on Coder Quality Report: ", e);
			responseJS.setMessage("Error occurred during initialization of report details!!");
			responseJS.setStatus(ResponseStatus.ERROR);
		}
		return responseJS;
	}
	
	@RequestMapping(value = "/miscReport", method=RequestMethod.GET)
	public @ResponseBody
	Response<String> getMiscReportData(@RequestParam String fromDate, 
			@RequestParam String toDate, HttpServletResponse response){
		Response<String> responseJS = new Response<String>();
		boolean isDataAvailable = false;
		try {
			ReportQueryParam requestParam = constructReportQueryParam(fromDate, toDate, null, null, null, null);
			isDataAvailable = downloadService.downloadMiscellaneousReport(response, requestParam);
			if(isDataAvailable) {
				responseJS.setMessage("Data Available for the report !!");
				responseJS.setStatus(ResponseStatus.OK);
				responseJS.setStatusCode(200);
			} else {
				responseJS.setMessage("No Data is Available for Miscellaneous report !!");
				responseJS.setStatus(ResponseStatus.ERROR);
			}
		} catch (final Exception e){
			logger.error(" Exception while miscReport :: ", e);
		}
		return responseJS;
	}
	
	private ReportQueryParam constructReportQueryParam(final String fromDate,
			final String toDate, final Integer clientId, String type, String queryClient, String chartType) {
		final ReportQueryParam requestParam = new ReportQueryParam();
		requestParam.setClientId(clientId);
		requestParam.setFromDate(fromDate);
		requestParam.setToDate(toDate);
		requestParam.setType(type);
		requestParam.setQueryClient(queryClient);
		requestParam.setChartType(chartType);
		return requestParam;
	}
}
