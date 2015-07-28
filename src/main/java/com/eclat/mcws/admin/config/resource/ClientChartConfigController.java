package com.eclat.mcws.admin.config.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.config.dto.ChartSpeDetails;
import com.eclat.mcws.admin.config.service.ClientConfigService;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ChartType;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/config/client")
public class ClientChartConfigController {

	@Log
	private Logger logger;
	
	@Autowired
	private ClientConfigService clientConfigService;
	
	@RequestMapping(value = "/allChartSpeDetail", method = RequestMethod.GET )
	@ResponseBody
	public Response<List<ChartSpeDetails>>  getAllUserDetails() {
		Response<List<ChartSpeDetails>> response = ResponseBuilder.buildOkResponse();
		final List<ChartSpeDetails> chartSpeDetails = clientConfigService.getAllChartSpecializationDetails();
		if(chartSpeDetails.size() > 0) {
			response.setPayLoad(chartSpeDetails);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/loadUserNotMapChartSpeDetails/{userId}", method = RequestMethod.GET )
	@ResponseBody
	public Response<List<ChartSpeDetails>>  loadUserNotMapChartSpeDetails(@PathVariable("userId") Integer userId) {
		
		Response<List<ChartSpeDetails>> response = ResponseBuilder.buildOkResponse();
		final List<ChartSpeDetails> chartSpeDetails = clientConfigService.getUserNotMappedChartSpecializationDetails(userId);
		
		if(chartSpeDetails.size() > 0) {
			response.setPayLoad(chartSpeDetails);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/loadUserNotMapClients/{userId}", method = RequestMethod.GET )
	@ResponseBody
	public Response<List<ClientDetails>>  loadUserNotMapClients(@PathVariable("userId") Integer userId) {
		
		Response<List<ClientDetails>> response = ResponseBuilder.buildOkResponse();
		final List<ClientDetails> clientDetails = clientConfigService.loadUserNotMappedClientDetails(userId);
		
		if(clientDetails.size() > 0) {
			response.setPayLoad(clientDetails);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/allChartTypes", method = RequestMethod.GET )
	@ResponseBody
	public Response<List<ChartType>>  getAllChartTypes() {
		Response<List<ChartType>> response = ResponseBuilder.buildOkResponse();
		final List<ChartType> chartTypes = clientConfigService.getAllChartTypes();
		if(chartTypes.size() > 0) {
			response.setPayLoad(chartTypes);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/saveChartSpecialization", method = RequestMethod.POST )
	@ResponseBody
	public Response  saveChartSpecialization ( @RequestBody ChartSpecilization chartSpecilization) {
		
		System.out.println(" ===> " +chartSpecilization);
		Response response = ResponseBuilder.buildOkResponse();
		String chartType  = chartSpecilization.getChartType();
		String specialization  = chartSpecilization.getChartSpelization();
		
		try{		
			Boolean addedChartSpl = clientConfigService.saveChartSpecialization(chartType, specialization);
			if(addedChartSpl) {
				response.setStatus(ResponseStatus.OK);
			} else {
				response.setStatus(ResponseStatus.ERROR);
			}
		} catch(Exception ex){
			response.setStatus(ResponseStatus.ERROR);
			if(ex.getCause().getMessage().contains("Duplicate entry"))
				response.setMessage(" Duplicate entry ! This Chart-Specialization (" +chartType + "-" + specialization + ") is already exists !!");
			else
				response.setMessage(" Error, Unable to create this Chart-Specialization !!");
		} finally {
			
		}
		return response;
	}
}
