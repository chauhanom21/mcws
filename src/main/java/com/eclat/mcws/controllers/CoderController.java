package com.eclat.mcws.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.CodingChartData;
import com.eclat.mcws.dto.GradingSheetDetails;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.ChartTypes;
import com.eclat.mcws.service.AuditDataService;
import com.eclat.mcws.service.CoderService;
import com.eclat.mcws.service.CommonUtilService;
import com.eclat.mcws.service.CoderWorkistributionService;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.JsonConstructor;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;
import com.eclat.mcws.util.rest.TaskResponse;

@Controller
@RequestMapping("/coder")
public class CoderController {

	@Log
	private Logger logger;

	@Autowired
	private CoderService coderService;

	@Autowired
	private CoderWorkistributionService workDistService;

	@Autowired
	private AuditDataService auditDataService;

	@Autowired
	private CommonUtilService commonUtilService;

	@Autowired
	private JsonConstructor jsonConstructor;

	@RequestMapping(value = "/fetchchart", method = RequestMethod.POST)
	@ResponseBody
	public TaskResponse<TaskDetails> fetchWorkListItem(@RequestBody ChartRestQueryDetails restQueryDetails) {
		TaskResponse<TaskDetails> response = new TaskResponse<TaskDetails>(ResponseStatus.OK);
		TaskDetails item;
		logger.debug("Query Parameters==>" + restQueryDetails);
		try {
			item = workDistService.fetchWorkList(restQueryDetails, response);

			populateCoderFetchTaskButtonOption(restQueryDetails.getUserId(), response);

			if (item != null) {
				response.setEnableTaskOption(false);
				response.setPayLoad(item);
			}
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
			response.setEnableTaskOption(true);
			populateCoderFetchTaskButtonOption(restQueryDetails.getUserId(), response);
		}

		return response;
	}

	private void populateCoderFetchTaskButtonOption(final Integer userId, TaskResponse<TaskDetails> response) {

		if (coderService.isCoder(userId)) {
			response.setEnableCoderTaskOption(true);
		} else {
			response.setEnableCoderTaskOption(false);
		}
	}

	@RequestMapping(value = "/fetchchartdetails", method = RequestMethod.POST)
	@ResponseBody
	public Response<TaskDetails> fetchChartDetails(@RequestBody String queryString) {

		Response<TaskDetails> response = ResponseBuilder.buildOkResponse();
		TaskDetails item;
		try {
			item = coderService.getWorkListDetails(queryString);
			jsonConstructor.constructColumnValCount(item);
			response.setPayLoad(item);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;

	}

	@RequestMapping(value = "/fetchActiveCharts", method = RequestMethod.POST)
	@ResponseBody
	public TaskResponse<List<TaskDetails>> fetchActiveWorkList(@RequestBody String coderId) {
		TaskResponse<List<TaskDetails>> response = new TaskResponse<List<TaskDetails>>(ResponseStatus.OK);
		List<TaskDetails> items = new ArrayList<>();
		try {
			items = coderService.getInProgressCharts((Integer.parseInt(coderId)));
			if (CommonOperations.checkListBasedOnStatus(items)) {
				response.setEnableTaskOption(true);
			} else {
				response.setEnableTaskOption(false);
			}

			boolean isCoder = coderService.isCoder((Integer.parseInt(coderId)));
			logger.debug("=========User is Coder?==========" + isCoder);
			if (isCoder) {
				response.setEnableCoderTaskOption(true);
			} else {
				response.setEnableCoderTaskOption(false);
			}
			response.setPayLoad(items);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * Json query contains userId and date
	 * 
	 * @param jsonQuery
	 * @return
	 */
	@RequestMapping(value = "/coderWorkedItems", method = RequestMethod.POST)
	@ResponseBody
	public Response<List<TaskDetails>> getCoderWorkedItems(@RequestBody String jsonQuery) {
		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		String fromdate = "";
		String toDate = "";
		List<TaskDetails> items = new ArrayList<>();
		try {
			// Parse the Json
			Map<String, Object> map = CommonOperations.parseJsonData(jsonQuery);
			Integer userId = (Integer) map.get("userId");
			fromdate = (String) map.get("fromDate");
			toDate = (String) map.get("toDate");

			logger.info("****  date : " + fromdate + " toDate : " + toDate);
			items = coderService.getCompletedChartsByDateRange(userId, fromdate, toDate);
			response.setPayLoad(items);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getallcharts", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<TaskDetails>> getAllWorkListItem(@RequestParam("coderId") String coderId,
			@RequestParam("date") String date) {

		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		List<TaskDetails> items = new ArrayList<>();
		try {
			items = coderService.fetchAllWorkLists((Integer.parseInt(coderId)), date);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		response.setPayLoad(items);
		return response;

	}

	@RequestMapping(value = "/getcompcharts", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<TaskDetails>> getCompWorkListItem(@RequestParam("coderId") String coderId,
			@RequestParam("date") String date) {

		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		List<TaskDetails> items = new ArrayList<>();
		try {
			items = coderService.getCompletedCharts((Integer.parseInt(coderId)), date);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		response.setPayLoad(items);
		return response;

	}

	@RequestMapping(value = "/updateChart", method = RequestMethod.POST)
	public @ResponseBody
	Response<TaskDetails> updateTask(@RequestBody String json) {

		final Map<String, Object> map = CommonOperations.parseJsonData(json);
		int workListId = (int) map.get("id");
		Object status = map.get("status");// Here status is a sub map with text
											// as the key to the value
		Response<TaskDetails> response = ResponseBuilder.buildOkResponse();
		if (workListId > 0 && status != null) {
			try {
				String taskStatus = "";
				if (status instanceof Map) {
					taskStatus = (String) ((Map) status).get("text");
				}
				TaskDetails item = coderService.updateWorkList(workListId, taskStatus);
			} catch (Exception e) {
				logger.error("Exception occurred while fetching ", e);
				response.setStatus(ResponseStatus.ERROR);
				response.setMessage(e.getMessage());
			}
		} else {
			response.setStatus(ResponseStatus.WARNING);
			response.setMessage("Invalid ID");
		}
		return response;

	}

	@RequestMapping(value = "/saveCodingChartData", method = RequestMethod.POST)
	@ResponseBody
	public Response<TaskDetails> saveCodingChartData(@RequestBody CodingChartData codingChartDetails) {

		Response<TaskDetails> response = ResponseBuilder.buildOkResponse();
		try {
			logger.debug("==> Coding Form Submit data ::: " + codingChartDetails + "CURRENT STATUS : "
					+ codingChartDetails.getCurrentStatus());
			final boolean success = coderService.saveOrUpdateCodingChartDetails(codingChartDetails);
			if (success) {
				response.setStatus(ResponseStatus.OK);
			} else {
				response.setStatus(ResponseStatus.VALIDATION_ERROR);
			}

		} catch (Exception e) {
			logger.error("Exception occurred while submiting IpChart ::s ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;

	}

	// adding for status filter
	@RequestMapping(value = "/states", method = RequestMethod.GET)
	@ResponseBody
	public Response<JSONArray> getAllStates() {
		logger.debug("<========Inside all states Types ===>");
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertList2Json(ChartStatus.values()));
		} catch (Exception e) {
			logger.error("Exception occurred while fetching states ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/fetchAuditChart", method = RequestMethod.POST)
	@ResponseBody
	public Response<TaskDetails> fetchAuditWorkItem(@RequestBody ChartRestQueryDetails restQueryDetails) {
		Response<TaskDetails> response = ResponseBuilder.buildOkResponse();
		TaskDetails item;
		logger.debug("Query Parameters to get Audit Task Details==>" + restQueryDetails);
		try {
			item = auditDataService.fetchTaskToAudit(restQueryDetails, response);
			if (item != null) {
				response.setStatus(ResponseStatus.OK);
				response.setPayLoad(item);
				response.setMessage(" Succefully fetched audit work item !!!");
			}
		} catch (Exception e) {
			logger.error("Exception occurred while loading Audit Task ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(" Got error while fetching audit work item !!!");
		}

		return response;
	}

	@RequestMapping(value = "/fetchActiveAuditCharts", method = RequestMethod.POST)
	@ResponseBody
	public Response<List<TaskDetails>> fetchActiveAuditWorks(@RequestBody Integer userId) {
		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		List<TaskDetails> items = new ArrayList<>();
		try {
			items = auditDataService.getAuditInProgressCharts(userId);
			if (items.size() > 0) {
				response.setEnableTaskOption(false);
			}
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("Error while fetching active audit tasks!");
		}
		response.setPayLoad(items);
		return response;
	}

	@RequestMapping(value = "/loadAuditChartDetails", method = RequestMethod.GET)
	@ResponseBody
	public Response<GradingSheetDetails> loadAuditChartDetails(@RequestParam Long taskId, @RequestParam Integer userId) {
		Response<GradingSheetDetails> response = ResponseBuilder.buildOkResponse();
		GradingSheetDetails details;
		try {
			details = auditDataService.getAuditedChartDetails(taskId, userId);
			response.setStatus(ResponseStatus.OK);
			response.setPayLoad(details);
		} catch (Exception e) {
			logger.error("Exception occurred while Getting QAGrading data ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/fetchGlobalQAAuditItems", method = RequestMethod.POST)
	@ResponseBody
	public Response<List<TaskDetails>> fetchGlobalQAAuditItems(@RequestBody ChartRestQueryDetails requestParam) {
		Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		List<TaskDetails> items = null;
		try {
			final boolean isValid = validateDate(requestParam.getStartDate(), requestParam.getEndDate());
			if (isValid) {
				items = auditDataService.getGlobalQaAuditItemsByUpdateDateRange(requestParam.getStartDate(),
						requestParam.getEndDate(), requestParam.getUserId());
				if (items != null) {
					response.setStatus(ResponseStatus.OK);
					response.setPayLoad(items);
					response.setMessage(" Succefully fetched daily audit work item !!!");
				} else {
					response.setMessage("No items are available to Audit ! ");
				}
			} else {
				response.setMessage("From date should be less then To date ! ");
				response.setStatus(ResponseStatus.VALIDATION_ERROR);
				response.setStatusCode(412);
			}
		} catch (Exception e) {
			logger.error("Exception occurred while loading daily Audit Task ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(" Got error while fetching daily audit work item !!!");
		}

		return response;
	}

	@RequestMapping(value = "/getWeightageMasterDetails", method = RequestMethod.GET)
	@ResponseBody
	public Response<Map<String, Double>> loadGradingWeightageByChartType(@RequestParam("chartType") String chartType) {
		Response<Map<String, Double>> response = ResponseBuilder.buildOkResponse();
		Map<String, Double> details;
		try {
			details = auditDataService.getAllWeightageMasterDataByChartType(chartType);
			response.setStatus(ResponseStatus.OK);
			response.setPayLoad(details);
		} catch (Exception e) {
			logger.error("Exception occurred while Getting Weightage data ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/saveAuditChart", method = RequestMethod.POST)
	@ResponseBody
	public Response<JSONObject> saveAuditChart(@RequestBody GradingSheetDetails gradingSheetDetail) {
		Response<JSONObject> response = ResponseBuilder.buildOkResponse();
		try {
			final boolean success = auditDataService.saveAuditChart(gradingSheetDetail);
			if (success) {
				response.setStatus(ResponseStatus.OK);
				response.setMessage("Grading Sheet Successfully Saved.");
			}
		} catch (Exception e) {
			logger.error("Exception occurred :: ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/getCodersDetailByClient", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<CodersQADto>> getCodersDetailByClient(@RequestParam Integer clientId,
			@RequestParam Integer auditorId) {
		Response<List<CodersQADto>> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(commonUtilService.getAllCodersDetailByClient(clientId, auditorId));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@RequestMapping(value = "/updateWorkStartTime", method = RequestMethod.POST)
	@ResponseBody
	public Response<String> updateWorkStartTime(@RequestBody ChartRestQueryDetails requestParam) {
		Response<String> response = ResponseBuilder.buildOkResponse();
		try {
			coderService.updateWorkStartTimeAndTaskStatus(requestParam.getTaskId(), requestParam.getUserId(),
					requestParam.getWorkType());
		} catch (final Exception e) {
			logger.error("Exception Updating wotk time: ", e);
			response.setStatus(ResponseStatus.ERROR);
		}
		return response;
	}

	@RequestMapping(value = "/saveOrUpdateNotes/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Response<String> saveOrUpdateNotes(@RequestBody String notes, @PathVariable("userId") Integer userId) {
		Response<String> response = ResponseBuilder.buildOkResponse();
		try {
			coderService.saveOrUpdateNotes(notes, userId);
		} catch (final Exception e) {
			logger.error("Exception Updating wotk time: ", e);
			response.setStatus(ResponseStatus.ERROR);
		}

		return response;
	}

	@RequestMapping(value = "/loadUserNotesValue/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public Response<String> loadUserNotesValue(@PathVariable("userId") Integer userId) {
		Response<String> response = ResponseBuilder.buildOkResponse();
		try {
			final String notes = coderService.loadUserNotesValue(userId);
			response.setPayLoad(notes);
		} catch (final Exception e) {
			logger.error("Exception Updating wotk time: ", e);
			response.setStatus(ResponseStatus.ERROR);
		}

		return response;
	}

	@RequestMapping(value = "/updateStatusToInProgress/{taskId}", method = RequestMethod.POST)
	@ResponseBody
	public Response<String> updateStatusToInProgress(@PathVariable("taskId") Long taskId) {

		Response<String> response = ResponseBuilder.buildOkResponse();

		try {
			coderService.updateStatusToInProgress(taskId);
		} catch (final Exception e) {
			logger.error("Exception Updating wotk time: ", e);
			response.setStatus(ResponseStatus.ERROR);
		}
		return response;
	}

	private boolean validateDate(final String fromDate, final String toDate) {
		final Calendar from_date = CommonOperations.convertStringValueToDateFormat(fromDate);
		final Calendar to_date = CommonOperations.convertStringValueToDateFormat(toDate);
		boolean isValid = true;
		if (to_date.compareTo(from_date) < 0)
			isValid = false;

		return isValid;
	}

	@RequestMapping("/mytasks")
	public String getMytasks() {
		return "app/coder/views/mytasks";
	}

	@RequestMapping("/alltasks")
	public String getAlltasks() {
		return "app/coder/views/alltasks";
	}

	@RequestMapping("/audit")
	public String getAuditTasks() {
		return "app/coder/views/audit";
	}

	@RequestMapping("/coding_chart/{type}")
	public String getCodingChart(@PathVariable("type") String type) {
		String chartType = "";
		if (type.equalsIgnoreCase(ChartTypes.IP.toString())) {
			chartType = "ip";
		} else if (type.equalsIgnoreCase(ChartTypes.GE.toString())) {
			chartType = "ge";
		} else if (type.equalsIgnoreCase(ChartTypes.EKG.toString())
				|| type.equalsIgnoreCase(ChartTypes.EKG_ECG.toString())) {
			chartType = "ekg";
		} else if (type.equalsIgnoreCase(ChartTypes.OP.toString()) || type.equalsIgnoreCase(ChartTypes.ER.toString())
				|| type.equalsIgnoreCase(ChartTypes.ANC.toString()) || type.equalsIgnoreCase(ChartTypes.M2.toString())
				|| type.equalsIgnoreCase(ChartTypes.MDA.toString())) {
			chartType = "common";
		}
		return "app/coder/views/codingtemplate/" + chartType + "codingtemplate";
	}

	@RequestMapping("/audit_chart/{type}")
	public String getAuditCodingChart(@PathVariable("type") String type) {
		String chartType = "";
		if (type.equalsIgnoreCase(ChartTypes.IP.toString())) {
			chartType = "ip";
		} else if (type.equalsIgnoreCase(ChartTypes.GE.toString())) {
			chartType = "ge";
		} else if (type.equalsIgnoreCase(ChartTypes.EKG.toString())
				|| type.equalsIgnoreCase(ChartTypes.EKG_ECG.toString())) {
			chartType = "ekg";
		} else if (type.equalsIgnoreCase(ChartTypes.OP.toString()) || type.equalsIgnoreCase(ChartTypes.ER.toString())
				|| type.equalsIgnoreCase(ChartTypes.ANC.toString()) || type.equalsIgnoreCase(ChartTypes.M2.toString())
				|| type.equalsIgnoreCase(ChartTypes.MDA.toString())) {

			chartType = "common";
		}
		return "app/coder/views/audittemplate/" + chartType + "audittemplate";
	}

	@RequestMapping("/notes")
	public String getUserNotesPage() {
		return "app/coder/views/notes";
	}

	@RequestMapping("/search")
	public String getUserSearchPage() {
		return "app/search/views/search_task";
	}

	public static void main(String[] args) {

	}
}
