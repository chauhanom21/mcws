package com.eclat.mcws.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.service.SearchService;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/search")
public class SearchController {

	@Log
	private Logger logger;
	
	@Autowired
	private SearchService searchService;
	
	@RequestMapping(value = "/charts", method = RequestMethod.GET)
	@ResponseBody
	public Response<List<TaskDetails>> searchTaskDetail(@RequestParam String searchQuery){
		final Response<List<TaskDetails>> response = ResponseBuilder.buildOkResponse();
		try {
			final List<TaskDetails> items = searchService.searchTaskDetailsByAccountMRNumber(searchQuery);
			response.setPayLoad(items);
		} catch(final Exception e) {
			logger.error("Exception while Search: ", e);
			response.setPayLoad(null);
			response.setStatus(ResponseStatus.ERROR);
		}
		
		return response;
	}
}
