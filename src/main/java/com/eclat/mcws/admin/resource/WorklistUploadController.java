package com.eclat.mcws.admin.resource;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.eclat.mcws.admin.service.ExcelFileHandlingService;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ErrorType;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/worklist")
public class WorklistUploadController {
	
	@Log
	private Logger logger;
	
	@Autowired
	private ExcelFileHandlingService fileHandler;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody Response<List<WorkListItem>> loadBulkEmployees(
			MultipartHttpServletRequest request, 
			HttpServletResponse response
			) {
		
		Iterator<String> itr = request.getFileNames();
		
		logger.info("Received worklist upload request for client: "+ request.getParameter("myClient"));
		
		logger.info("Received In Complete request: "+ Boolean.parseBoolean(request.getParameter("isIncompleteList")));		
		
		MultipartFile file = request.getFile(itr.next());
		
		String clientName = request.getParameter("myClient");
		
		String isIncomplete = request.getParameter("isIncompleteList");
		
		Boolean isIncompleteList = Boolean.parseBoolean(request.getParameter("isIncompleteList"));
		
		String fileName = file.getOriginalFilename();
		
		Response<List<WorkListItem>> responseRef = ResponseBuilder.buildOkResponse();
		
		if (fileName!=null) {
			
			logger.info("Received new File: " + fileName + " ; Size: " + file.getSize() + " bytes...");
			
			String[] fileNameParts = fileName.split("\\.");
			
			if ("xls".equalsIgnoreCase(fileNameParts[fileNameParts.length-1])) {
				//updated condition
				try {
					// Read from this stream ...
					InputStream stream = file.getInputStream();	
					
					//List<WorkListItem> items = fileHandler.getWorkListItems(stream, clientName);
					
					responseRef = fileHandler.getWorkListItems(stream, clientName, isIncompleteList, responseRef);
					
					if(null == responseRef.getPayLoad() && !isIncompleteList)
						return ResponseBuilder.buildErrorResponse(ErrorType.ERROR, "", responseRef.getMessage());
								//" Uploaded Invalid File for selected Client " + "\n" +
								//" --OR--" + "\n" + " You may Uploaded Incomplete Worklist data instead of Normal worklist");
					
					if(null == responseRef.getPayLoad() && isIncompleteList)
						return ResponseBuilder.buildErrorResponse(ErrorType.ERROR, "", responseRef.getMessage());
								//"Please Upload valid Excel file with IncompleteList Client Data. You Uploaded Regular Worklist instead of Incomplete List");
										
					//responseRef.setPayLoad(items);
					
					//save the list
					if(!isIncompleteList){
						if(null != responseRef.getPayLoad()) {
							fileHandler.saveAllListItems(responseRef.getPayLoad());
							responseRef.setMessage("Charts ("+responseRef.getPayLoad().size()+") are imported successfully for Client: " + clientName);
						}
					}
					else{
						Boolean flag = fileHandler.updateIncompleteWorkItems(responseRef.getPayLoad());
						if(flag)
							responseRef.setMessage("Incomplete worklist Charts ("+responseRef.getPayLoad().size()+") are updated successfully for Client: " + clientName);
						else 
							responseRef.setMessage("Unable to update the Incomplete worklist Charts for Client: " + clientName);
					}
										
					logger.info("Total Items from the Excel sheet :: "+responseRef.getPayLoad().size());

					return responseRef;
				} catch (Exception e) {
					logger.error("Error while parsing the uploaded excel file...", e);
					return ResponseBuilder.buildErrorResponse(ErrorType.ERROR, "",
							"Error while reading the Excel. [" + e.getMessage() + " ]");
				}
			} else {
				logger.info("Invalid file submitted");
				return ResponseBuilder.buildErrorResponse(ErrorType.ERROR, "",
						"Invalid File Choosen. Please Upload Excel File of 2003 version only");
			}
		}		
		logger.info("Final Return");
		return responseRef;
		
	}	
	
	@RequestMapping("/getClients")
	public String getClients() {
		return "employee/layout";
	}

	@RequestMapping("/loadEmployees")
	public String getEmployeePartialPage() {
		return "employee/layout";
	}

}
