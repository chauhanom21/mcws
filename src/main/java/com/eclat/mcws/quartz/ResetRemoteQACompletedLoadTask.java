package com.eclat.mcws.quartz;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.service.SupervisorService;

@Configurable
public class ResetRemoteQACompletedLoadTask {
	
	@Autowired
	SupervisorService supervisorService;
	
	@Log
	private Logger logger;
	
	public void runTask() {
		try {
			System.out.println(" Entered in  ResetRemoteQACompletedLoad QUARTZ JOB successfully !!! (RemoteQA --> EST/EDT timezone  00:00 ) @ " + new Date());
			supervisorService.executeResetCompletedLoadForAll("remoteQA");
			logger.debug(" Executed ResetRemoteQACompletedLoad QUARTZ JOB successfully !!! (RemoteQA --> EST/EDT timezone) @ " + new Date());
		} catch (Exception e) {
			logger.error(" Unable to Execute ResetRemoteQACompletedLoad QUARTZ JOB @ " + new Date());
			e.printStackTrace();
		}
	}
}
