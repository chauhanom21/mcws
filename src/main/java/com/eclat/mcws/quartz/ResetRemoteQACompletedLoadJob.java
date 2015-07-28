package com.eclat.mcws.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ResetRemoteQACompletedLoadJob  extends QuartzJobBean {
	
	private ResetRemoteQACompletedLoadTask resetRemoteQACompletedLoadTask;

	public void setResetRemoteQACompletedLoadTask(ResetRemoteQACompletedLoadTask resetRemoteQACompletedLoadTask) {
		this.resetRemoteQACompletedLoadTask = resetRemoteQACompletedLoadTask;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		resetRemoteQACompletedLoadTask.runTask();

	}

}
	
