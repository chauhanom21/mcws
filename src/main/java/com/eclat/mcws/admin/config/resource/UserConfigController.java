package com.eclat.mcws.admin.config.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.config.dto.UserDetails;
import com.eclat.mcws.admin.config.service.UserConfigService;
import com.eclat.mcws.admin.dto.UserClientDto;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/config/user")
public class UserConfigController {

	@Log
	private Logger logger;
	
	@Autowired
	private UserConfigService userConfigService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST )
	@ResponseBody
	public Response<String> createUser(@RequestBody UserDetails userData) {
				
		userConfigService.saveOrUpdateUser(userData);
		Response<String> response = ResponseBuilder.buildOkResponse();
		response.setMessage("User Successfully Created !!!");
		return response;
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT )
	@ResponseBody
	public Response<String> updateUserDetails(@RequestBody UserDetails userData) {
		
		userConfigService.saveOrUpdateUser(userData);
		Response<String> response = ResponseBuilder.buildOkResponse();
		response.setMessage("User Details Successfully Updated !!!");
		return response;
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE )
	@ResponseBody
	public void deleteUser(@RequestBody String userData){
		
	}
	
	@RequestMapping(value = "/loadAllUsers", method = RequestMethod.GET )
	@ResponseBody
	public Response<List<UserDetails>>  getAllUserDetails() {
		Response<List<UserDetails>> response = ResponseBuilder.buildOkResponse();
		final List<UserDetails> userDetails = userConfigService.getAllUserDetails();
		if(userDetails.size() > 0) {
			response.setPayLoad(userDetails);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/loadUserDetails/{userId}/{loadClientDetail}", method = RequestMethod.GET )
	@ResponseBody
	public Response<UserDetails>  getUserDetailsById(@PathVariable("userId") Integer userId, @PathVariable("loadClientDetail") Boolean loadClientDetail) {
		logger.info(" ---------->userId : " +userId);
	
		final UserDetails userDetails = userConfigService.getUserDetailsById(userId, loadClientDetail);
		Response<UserDetails> response = ResponseBuilder.buildOkResponse();
		if(userDetails != null) {
			response.setPayLoad(userDetails);
		} else {
			response.setStatus(ResponseStatus.ERROR);
			response.setPayLoad(null);
		}
		return response;
	}
	
	@RequestMapping(value = "/saveUserClientDetails", method = RequestMethod.POST )
	@ResponseBody
	public Response<String> saveUserClientDetails(@RequestBody UserClientDto userClientDto) {
		Response<String> response = ResponseBuilder.buildOkResponse();
		try {
			if(userClientDto != null ) {
				List<Integer> clientIds = userClientDto.getClientIds();
				Integer userId = userClientDto.getUserId();
					userConfigService.saveOrUpdateUserClientDetails(userId, clientIds);
					response.setMessage("Successfully Updated User's Client Configuration details !!!");
			} else {
					response.setStatus(ResponseStatus.ERROR);
					response.setMessage("User/Client details not found on request !!!");
			}
		} catch( final Exception e ) {
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("Got Exception While Updating User's Client Configuration !!!");
		}
		return response;
	}
	
	@RequestMapping(value = "/saveUserChartSpecDetails", method = RequestMethod.POST )
	@ResponseBody
	public Response<String> saveUserChartSpecDetails(@RequestBody JSONObject requestData) {
		Response<String> response = ResponseBuilder.buildOkResponse();
		try {
			
			List<Integer> chartSpeIds = new ArrayList<Integer> ();
			Integer userId = iterateUserChartData(chartSpeIds, requestData);
			
			if(userId > 0 && chartSpeIds.size() > 0) {
				userConfigService.saveOrUpdateUserChartSpeDetails(userId, chartSpeIds);
				response.setMessage("Successfully Updated User's Chart Configuration details !!!");
			} else {
				response.setStatus(ResponseStatus.ERROR);
				response.setMessage("User/Chart details not found on request !!!");
			}
			
		} catch(final Exception e) {
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("Got Exception While Updating User's Client Configuration !!!");
		}
		return response;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param chartSpeIds
	 * @param requestParam
	 */
	public Integer iterateUserChartData(List<Integer> chartSpeIds, JSONObject requestParam){
		final Map<String, Object> map = CommonOperations.parseJsonData(requestParam.toJSONString());
		Integer userId  = 0;
		if(map.size() > 0) {
			String usrId = (String) map.get("userId");
			if(usrId != null) {
				userId  = Integer.parseInt(usrId);
			}
			ArrayList<Integer> chartSpeIdList = (ArrayList<Integer>) map.get("chartSpeIds");
			for(Integer chartId: chartSpeIdList) {
				chartSpeIds.add(chartId);
			}
		}
		return userId;
	}
}
