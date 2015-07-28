package com.eclat.mcws.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ResetCompletedLoadJob  extends QuartzJobBean {
	
	private ResetCompletedLoadTask resetCompletedLoadTask;

	public void setResetCompletedLoadTask(ResetCompletedLoadTask resetCompletedLoadTask) {
		this.resetCompletedLoadTask = resetCompletedLoadTask;
	}
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		resetCompletedLoadTask.runTask();

	}

}
	
