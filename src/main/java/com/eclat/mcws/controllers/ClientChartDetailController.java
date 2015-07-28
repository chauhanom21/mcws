package com.eclat.mcws.controllers;

import java.util.List;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.ChartTypes;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.service.CoderService;
import com.eclat.mcws.service.CommonUtilService;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/client/detail")
public class ClientChartDetailController {

	@Log
	private Logger logger;

	@Autowired
	private CoderService coderService;
	
	@Autowired
	private CommonUtilService commonUtilService;
	
	/**
	 * 
	 * @return List of all client's name
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public Response<JSONArray> getAllClients() {
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertStringDataToJsonArray(coderService.getAllClientsName()));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
	
	/**
	 * 
	 * @return List of all client's detail
	 */
	@RequestMapping(value = "/allClientsDetail", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<ClientDetails>> allClientsDetail() {
		Response<List<ClientDetails>> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(commonUtilService.getAllClientDetails());
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
	
	/**
	 * 
	 * @return List of all Client's Id
	 */
	@RequestMapping(value = "/allClientIds", method = RequestMethod.GET)
	@ResponseBody
	public Response<JSONArray> getAllClientIds() {
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		try {
			response.setPayLoad(CommonOperations.convertIdsToJsonArray(coderService.getAllClientIds()));
			response.setStatus(ResponseStatus.OK);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
	
	/**
	 * 
	 * @return List of all chart type
	 */
	@RequestMapping(value = "/allChartTypes", method = RequestMethod.GET)
	@ResponseBody
	public Response<JSONArray> getAllChartTypes() {
		
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		
		try {
			response.setPayLoad(CommonOperations.convertList2Json(ChartTypes.values()));
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
	
	/**
	 * 
	 * @return List of all chart status
	 */
	@RequestMapping(value = "/allChartStatus", method = RequestMethod.GET)
	@ResponseBody
	public Response<JSONArray> getAllChartStatus() {
		
		Response<JSONArray> response = ResponseBuilder.buildOkResponse();
		
		try {
			response.setPayLoad(CommonOperations.convertList2Json(ChartStatus.values()));
		} catch (Exception e) {
			logger.error("Exception occurred while fetching ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage(e.getMessage());
		}
		return response;
	}
}
