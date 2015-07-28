package com.eclat.mcws.quartz;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.service.SupervisorService;

@Configurable
public class ResetCompletedLoadTask {
	
	@Autowired
	SupervisorService supervisorService;
	
	@Log
	private Logger logger;
	
	public void runTask() {
		try {
			System.out.println(" Entered in  ResetCompletedLoad QUARTZ JOB successfully !!! (Coders & LocalQA --> for equivalant IST = 00:00 )  @EST/EDT : " + new Date());
			supervisorService.executeResetCompletedLoadForAll("coderLocalQA");
			logger.debug(" Executed ResetCompletedLoad QUARTZ JOB successfully !!! (Coders & LocalQA --> for equivalant IST = 00:00 )  @EST/EDT :" + new Date());
		} catch (Exception e) {
			logger.error(" Unable to Execute ResetCompletedLoad QUARTZ JOB @ " + new Date());
			e.printStackTrace();
		}
	}
}
