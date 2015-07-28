package com.eclat.mcws.admin.resource;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.service.ExcelFileHandlingService;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ErrorType;
import com.eclat.mcws.util.rest.ResponseBuilder;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Log
	private Logger logger;
	
	@Autowired
	private ExcelFileHandlingService fileHandlingService;
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody Boolean generateBulkGrantReport(
			MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		String fileName = file.getOriginalFilename();
		logger.info("Received new File: " + fileName + "; Size: "
				+ file.getSize() + " bytes...");

		try {
			// Read from this stream for CSV parsing operation...
			InputStream stream = file.getInputStream();			
			return true;
		} catch (Exception e) {
			logger.error("Error while parsing the CSV file...", e);
			return false;
		}
	}
	
	@RequestMapping(value = "/lastupdate", method = RequestMethod.POST)
	public @ResponseBody Boolean uploadExcel(
			final HttpServletRequest request, final HttpServletResponse response) {
		if (request instanceof MultipartHttpServletRequest) {
	        // process the uploaded file
			System.out.println("multipart request");
	    }
	    else {
	        // other logic
	    	System.out.println("normal http request");
	    }

		try {
			// Read from this stream for CSV parsing operation...
			//InputStream stream = file.getInputStream();			
			return true;
		} catch (Exception e) {
			logger.error("Error while parsing the CSV file...", e);
			return false;
		}
	}
	
	@RequestMapping(value="/newDocument", headers = "'Content-Type': 'multipart/form-data'", method = RequestMethod.POST)
    public void UploadFile(MultipartHttpServletRequest request, HttpServletResponse response) {

        Iterator<String> itr=request.getFileNames();

        MultipartFile file=request.getFile(itr.next());

        String fileName=file.getOriginalFilename();
        System.out.println(fileName);
    }
	
	@RequestMapping(value="/allclients", method=RequestMethod.GET)
	public @ResponseBody Response<List<ClientDetails>> getAllclients() {
		String message = "";
		try {			
			List<ClientDetails> clientList = fileHandlingService.getClients();
			logger.info("clientList : " + clientList);
			return ResponseBuilder.buildSuccessResponse(clientList, "Got the clientList DATA");
		} catch (Exception e) {
			logger.error("Error while getting clientList data...", e);
			message = "Error while getting clientList data...";
		} 
		return ResponseBuilder.buildErrorResponse(ErrorType.ERROR,"9999999999","Error while getting clientList data...");
	}

	@RequestMapping("/dashboard")
	public String getAdminDashboard() {
		return "app/admin/views/admin_dashboard";
	}

	@RequestMapping("/user_config")
	public String getUserConfig() {
		return "app/admin/views/confightml/user_config";
	}
	
	@RequestMapping("/client_config")
	public String getAuditTasks() {
		return "app/admin/views/confightml/client_config";
	}
	
	@RequestMapping("/charttype_chartspl_config")
	public String getClientChartType() {
		return "app/admin/views/confightml/charttype_chartspl_config";
	}
}
