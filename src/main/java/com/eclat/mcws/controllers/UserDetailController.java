package com.eclat.mcws.controllers;

import java.util.List;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.enums.UserLocations;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.service.CoderService;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/user/detail")
public class UserDetailController {

	@Log
	private Logger logger;

	@Autowired
	private CoderService coderService;

	@RequestMapping(value = "/coders", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllCoders() {
		Response<List<CodersQADto>> response = ResponseBuilder.buildOkResponse();
		
//		final List<CodersQADto> codersList = coderService.getAllCodersData(clientId, chartSpecId);
//		
//		if (codersList != null) {
//			response.setPayLoad(codersList);
//			response.setStatus(ResponseStatus.OK);
//		} else {
//			response.setStatus(ResponseStatus.ERROR);
//		}

		return response;
	}
	
	@RequestMapping(value = "/coders/{clientId}/{chartSpecId}", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllCodersByClientAndSpec(@PathVariable("clientId") Integer clientId,
			@PathVariable("chartSpecId") Integer chartSpecId) {
		final List<CodersQADto> codersList = coderService.getAllCodersData(clientId, chartSpecId);
		Response<List<CodersQADto>> response = ResponseBuilder.buildOkResponse();
		if (codersList != null) {
			response.setPayLoad(codersList);
			response.setStatus(ResponseStatus.OK);
		} else {
			response.setStatus(ResponseStatus.ERROR);
		}

		return response;
	}
	
	@RequestMapping(value = "/coders/{clientName}/{chartType}/{chartSpls}/{status}", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllCodersByClientChartTypeAndSpl(@PathVariable("clientName") String clientName,
			@PathVariable("chartType") String chartType, @PathVariable("chartSpls") String chartSpls,
			@PathVariable("status") String status) {
		final List<CodersQADto> codersList = coderService.
				getAllCodersORLocalQAByClientChartTypeAndChartSpl(clientName, chartType, chartSpls, status);
		Response<List<CodersQADto>> response = ResponseBuilder.buildOkResponse();
		if (codersList != null) {
			response.setPayLoad(codersList);
			response.setStatus(ResponseStatus.OK);
		} else {
			response.setStatus(ResponseStatus.ERROR);
		}

		return response;
	}

	@RequestMapping(value = "/localQA/{clientId}/{chartSpecId}", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllLocalQAByClientAndChartSpec(@PathVariable("clientId") Integer clientId, 
			@PathVariable("chartSpecId") Integer chartSpecId) {
		final List<CodersQADto> localQAList = coderService.getAllLocalQAData(clientId, chartSpecId);
		Response<List<CodersQADto>> apiResponse = ResponseBuilder.buildOkResponse();
		if (localQAList != null) {
			apiResponse.setPayLoad(localQAList);
			apiResponse.setStatus(ResponseStatus.OK);
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
		}

		return apiResponse;
	}

	@RequestMapping(value = "/remoteQA/{clientId}/{chartSpecId}", method = RequestMethod.GET)
	public @ResponseBody
	Response<List<CodersQADto>> getAllRemoteQAByClientAndChartSpec(@PathVariable("clientId") Integer clientId,
			@PathVariable("chartSpecId") Integer chartSpecId) {
		final List<CodersQADto> remoteQAList = coderService.getAllRemoteQAData(clientId, chartSpecId);
		Response<List<CodersQADto>> apiResponse = ResponseBuilder.buildOkResponse();
		if (remoteQAList != null) {
			apiResponse.setPayLoad(remoteQAList);
			apiResponse.setStatus(ResponseStatus.OK);
		} else {
			apiResponse.setStatus(ResponseStatus.ERROR);
		}

		return apiResponse;
	}

	@RequestMapping(value = "/allUsersName", method = RequestMethod.GET)
	public @ResponseBody
	Response<JSONArray> getAllUsersName() {
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertStringDataToJsonArray(coderService.getAllUsersName()));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	@RequestMapping(value = "/allUsersRole", method = RequestMethod.GET)
	public @ResponseBody
	Response<JSONArray> getAllUsersRole() {
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertUserRolesToJson(UserRoles.values()));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}

		return response;
	}
	
	@RequestMapping(value = "/allLocations", method = RequestMethod.GET)
	public @ResponseBody
	Response<JSONArray> getAllLocations() {
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertUserLocationToJson(UserLocations.getValues()));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}

		return response;
	}
	
}
