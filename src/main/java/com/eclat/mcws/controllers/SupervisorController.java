package com.eclat.mcws.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.AuditParamEntity;
import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.CoderOrQaDetail;
import com.eclat.mcws.dto.DashboardBean;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.service.DownloadService;
import com.eclat.mcws.service.SupervisorService;
import com.eclat.mcws.service.UserService;
import com.eclat.mcws.service.CoderDailyWorkloadSetupService;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/supervisor")
public class SupervisorController {

	@Log
	private Logger logger;

	@Autowired
	private SupervisorService supervisorService;

	@Autowired
	private DownloadService downloadService;

	@Autowired
	private ChartSpecilizationDao chartSpelDao;

	@Autowired
	private CoderDailyWorkloadSetupService workloadSetupService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/allCodersAndQA", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllUsersDetail() {
		Response<List<CodersQADto>> response = ResponseBuilder.buildOkResponse();
		List<CodersQADto> coderOrQaList = null;
		try {
			coderOrQaList = supervisorService.getAllCoderAndQADetails();
			if (coderOrQaList != null) {
				response.setPayLoad(coderOrQaList);
			} else {
				response.setStatus(ResponseStatus.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(ResponseStatus.ERROR);
			logger.error(" Got Exception :: ", e);
		}
		return response;
	}

	@RequestMapping(value = "/coderqa", method = RequestMethod.GET)
	public @ResponseBody
	Response<CoderOrQaDetail> getCoderOrQaById(@RequestParam(value = "coderOrQaId", required = true) Integer id,
			@RequestParam(value = "fromDate") String fromDate, @RequestParam(value = "toDate") String toDate) {

		Response<CoderOrQaDetail> response = ResponseBuilder.buildOkResponse();
		CoderOrQaDetail coderQaDetail = null;
		try {
			coderQaDetail = supervisorService.getCoderOrQaDetailByIdAndDate(id, fromDate, toDate);
			if (coderQaDetail != null) {
				response.setPayLoad(coderQaDetail);
			} else {
				response.setStatus(ResponseStatus.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(ResponseStatus.ERROR);
			logger.error(" Got Exception :: ", e);
		}
		return response;
	}

	@RequestMapping(value = "/allTaskDetails", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<TaskDetails>> getAllUserTaskDetails(@RequestParam(value = "date") String date) {
		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		try {
			List<TaskDetails> taskDetailsList = supervisorService.getAllTaskDetails(date);
			if (taskDetailsList != null && taskDetailsList.size() > 0) {
				response.setPayLoad(taskDetailsList);
			} else {
				response.setStatus(ResponseStatus.DATA_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(ResponseStatus.ERROR);
			logger.error(" Got Exception :: ", e);
		}
		return response;
	}

	@RequestMapping(value = "/updateTask", method = RequestMethod.POST)
	public @ResponseBody
	Response<String> updateTask(@RequestBody TaskDetails task) {
		Response<String> apiResponse = new Response<String>();
		if (task != null) {
			final long taskId = task.getId();
			int coderId = 0;
			int localQAId = 0;
			int remoteQAId = 0;
			String status = null;
			double effortMetric = 0.0;

			if (task.getCoderId() != null)
				coderId = task.getCoderId();

			if (task.getLocalQAId() != null)
				localQAId = task.getLocalQAId();

			if (task.getRemoteQAId() != null)
				remoteQAId = task.getRemoteQAId();

			if (task.getStatus() != null)
				status = task.getStatus();

			if (task.getEm() != null) {
				String em = task.getEm();
				if (em.contains("EM"))
					effortMetric = Double.parseDouble(em.substring(0, em.indexOf("EM")));
				else
					effortMetric = Double.parseDouble(em);
			}

			final boolean isUpdated = supervisorService.updateWorkListItem(taskId, coderId, localQAId, remoteQAId,
					effortMetric, status);

			if (isUpdated) {
				apiResponse.setStatus(ResponseStatus.OK);
				apiResponse.setMessage("Success");
			} else {
				apiResponse.setStatus(ResponseStatus.ERROR);
				apiResponse.setMessage("Update Failed!");
			}
			return apiResponse;
		}

		apiResponse.setStatus(ResponseStatus.ERROR);
		apiResponse.setMessage("Update Failed!");
		return apiResponse;
	}

	@RequestMapping(value = "/assignChartToCoder", method = RequestMethod.POST)
	public @ResponseBody
	Response<String> assignChartToCoder(@RequestBody ChartRestQueryDetails queryDetails) {
		Response<String> apiResponse = new Response<String>();

		final boolean isUpdated = supervisorService.assignWorkListItemToCoder(queryDetails.getTaskIds(),
				queryDetails.getCoderId(), queryDetails.getStatus());
		if (isUpdated) {
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("Success");
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Update Failed!");
		}
		return apiResponse;
	}

	@RequestMapping(value = "/getSupervisorReportsData", method = RequestMethod.GET)
	public @ResponseBody
	Response<JSONArray> getSupervisorReportsData(@RequestParam String fromDate, @RequestParam String toDate) {

		JSONArray reports = null;
		try {
			reports = supervisorService.getSupervisorReportsData(fromDate, toDate);
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}

		Response<JSONArray> apiResponse = new Response<JSONArray>();
		if (reports != null) {
			apiResponse.setPayLoad(reports);
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("success");
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
		}

		return apiResponse;
	}

	@RequestMapping(value = "/createAudit", method = RequestMethod.POST)
	public @ResponseBody
	Response<JSONArray> createAudit(@RequestBody AuditParamEntity auditParam) {

		boolean isCreated = false;
		String message = "Do not have sufficient work items to audit.";
		Response<JSONArray> apiResponse = new Response<JSONArray>();
		try {
			final boolean isValid = validateDate(auditParam.getFromDate(), auditParam.getToDate());
			if (isValid) {
				isCreated = supervisorService.createAuditData(auditParam, message);
				if (isCreated) {
					apiResponse.setStatus(ResponseStatus.OK);
					apiResponse.setMessage("Auditing data successfully created !!!");
				} else {
					apiResponse.setStatus(ResponseStatus.ERROR);
					apiResponse.setMessage(message);
				}
			} else {
				apiResponse.setStatus(ResponseStatus.VALIDATION_ERROR);
				apiResponse.setStatusCode(412);
			}
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Got error while fetching audit data !!!");
			return apiResponse;
		}

		return apiResponse;
	}

	@RequestMapping(value = "/getDashboardData", method = RequestMethod.GET)
	public @ResponseBody
	Response<DashboardBean> getDashboardDistributionData() {

		Response<DashboardBean> apiResponse = new Response<DashboardBean>();
		DashboardBean dataBean = null;
		try {
			dataBean = supervisorService.getSupervisorDashboardData();
		} catch (Exception e) {
			logger.error(" Got Exception :: ", e);
		}

		if (dataBean != null) {
			apiResponse.setPayLoad(dataBean);
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("success");
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
		}

		return apiResponse;
	}

	private boolean validateDate(final String fromDate, final String toDate) {
		final Calendar from_date = CommonOperations.convertStringValueToDateFormat(fromDate);
		final Calendar to_date = CommonOperations.convertStringValueToDateFormat(toDate);
		boolean isValid = true;
		if (to_date.compareTo(from_date) < 0)
			isValid = false;

		return isValid;
	}

	@RequestMapping(value = "/getAllUniqueChartSpecialization", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<ChartSpecilization>> getChartSpecialization() {
		Response<List<ChartSpecilization>> response = new Response<List<ChartSpecilization>>();
		final List<ChartSpecilization> chartSpls = chartSpelDao.getAllUniqueChartSpecialization();
		response.setPayLoad(chartSpls);
		response.setStatus(ResponseStatus.OK);
		return response;
	}

	@RequestMapping(value = "/getChartTypeSpecialization/{chartType}", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<ChartSpecilization>> getChartTypeSpecialization(@PathVariable String chartType) {
		Response<List<ChartSpecilization>> response = new Response<List<ChartSpecilization>>();
		final List<ChartSpecilization> chartSpls = new ArrayList<>();
		for (ChartSpecilization chartSpl : chartSpelDao.getAllUniqueChartSpecialization()) {
			if (chartSpl.getChartType().equals(chartType))
				chartSpls.add(chartSpl);
		}
		response.setPayLoad(chartSpls);
		response.setStatus(ResponseStatus.OK);
		return response;
	}

	@RequestMapping(value = "/deleteWorklist", method = RequestMethod.POST)
	public @ResponseBody
	Response<String> deleteWorklist(@RequestBody ChartRestQueryDetails queryDetails) {
		Response<String> apiResponse = new Response<String>();
		final boolean isDeleted = supervisorService.deleteWorklistItems(queryDetails.getTaskIds());
		if (isDeleted) {
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("Success");
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Deleted Failed!");
		}
		return apiResponse;
	}

	@RequestMapping(value = "/updateUserAvailability", method = RequestMethod.POST)
	public @ResponseBody
	Response<String> updateUserAvailability(@RequestBody CodersQADto coderQA) {
		Response<String> response = new Response<String>();
		final Boolean isUpdated = userService.updateUserAvailability(coderQA);
		if (isUpdated) {
			response.setStatus(ResponseStatus.OK);
			response.setMessage("Success");
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("Deleted Failed!");
		}
		return response;
	}

	@RequestMapping(value = "/setupDailyCodersWorkload", method = RequestMethod.GET)
	public @ResponseBody
	Response<String> setupDailyCodersWorkload() {
		Response<String> apiResponse = new Response<String>();
		
		//Reset coder daily work load before setup coder'd daily work load.
		final Boolean isReset = workloadSetupService.resetDailyCodersWorkload();
		if (isReset) {
			final Boolean isAssign = workloadSetupService.setupCoderDailyWorkload();
			if (isAssign) {
				apiResponse.setStatus(ResponseStatus.OK);
				apiResponse.setMessage("Success");
			} else {
				apiResponse.setStatus(ResponseStatus.ERROR);
				apiResponse.setMessage("Deleted Failed!");
			}
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Deleted Failed!");
		}
		return apiResponse;
	}
	
	@RequestMapping(value = "/resetDailyCodersWorkload", method = RequestMethod.GET)
	public @ResponseBody
	Response<String> resetDailyCodersWorkload() {
		Response<String> apiResponse = new Response<String>();
		final Boolean isReset = workloadSetupService.resetDailyCodersWorkload();
		if (isReset) {
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("Success");
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Deleted Failed!");
		}
		return apiResponse;
	}

	
	@RequestMapping("/newupload")
	public String uploadWorklist() {
		return "app/admin/views/upload";
	}

	@RequestMapping("/monitorUsers")
	public String monitorUsers() {
		return "app/supervisor/views/monitorUsers";
	}

	@RequestMapping("/monitorSelectedUser")
	public String monitorSelectedUser() {
		return "app/supervisor/views/monitorSelectedUser";
	}

	@RequestMapping("/monitorTask")
	public String monitorTasks() {
		return "app/supervisor/views/monitorTask";
	}

	@RequestMapping("/setupAuditing")
	public String setupAuditing() {
		return "app/supervisor/views/setup_audit";
	}

	@RequestMapping("/dashboard")
	public String viewDashboard() {
		return "app/supervisor/views/dashboard";
	}

	@RequestMapping("/deleteWorklistData")
	public String deleteWorklistData() {
		return "app/supervisor/views/delete_worklist_data";
	}
	
	
	@Autowired
	private WorkListDao worklistDao;
	
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody
	Response<String> resetWorklistData() {
		Response<String> apiResponse = new Response<String>();
		try {
			final long worklistId = 18554L;

			final WorkListItem workItem = worklistDao.find(worklistId);

			System.out.println(" ID : " + workItem.getId());
			System.out.println(" EM Value : " + workItem.getEffortMetric());
			
			workItem.setStatus("Assigned");
			workItem.setCoderId(1035);
			
			worklistDao.normalUpdate(workItem);
			
			
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("Success");
			
		} catch (final Exception e) {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Failed!");
		}
		
		
		return apiResponse;
	}
	
	
	
	@RequestMapping(value = "/test1", method = RequestMethod.GET)
	public @ResponseBody
	Response<String> resetWorklistData1() {
		Response<String> apiResponse = new Response<String>();
		try {
			final long worklistId = 18554L;

			final WorkListItem workItem = worklistDao.find(worklistId);

			System.out.println(" ID : " + workItem.getId());
			System.out.println(" EM Value : " + workItem.getEffortMetric());
			
			workItem.setStatus("Assigned Coder");
			workItem.setCoderId(1036);
			
			worklistDao.normalUpdate(workItem);
			
			apiResponse.setStatus(ResponseStatus.OK);
			apiResponse.setMessage("Success");
			
		} catch (final Exception e) {
			apiResponse.setStatus(ResponseStatus.ERROR);
			apiResponse.setMessage("Failed!");
		}
		
		
		return apiResponse;
	}
}
