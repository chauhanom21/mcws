package com.eclat.mcws.service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.rest.TaskResponse;

@Component
public class CoderWorkistributionService {

	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sesionFactory;
	
	@Autowired
	private CoderDao coderDao;
	
	@Autowired
	private WorkDistributionService workDistributionService;
	
	@Autowired
	private WorklistUtilService worklistUtilService;
	
	@Autowired
	private AppStartupComponent appStartupComponent;

	Map<Integer, Coders> codersMap;
	Map<Integer, ChartSpecilization> chartSplMap;

	@PostConstruct
	public void init() {
		codersMap = appStartupComponent.getCodersMap();
		chartSplMap = appStartupComponent.getChartSplObjMap();
	}

	
	public TaskDetails fetchWorkList(ChartRestQueryDetails restQuery, TaskResponse response) throws Exception {
		// Get Coder Object ==>keep in session after login
		Coders coder = coderDao.find((int) restQuery.getUserId());

		logger.info("======== fetch work for coder " + coder.getUserId());
		TaskDetails result = null;
		// Before getting the Item check coders load
		if (isCoderAvailable(coder, restQuery.isCoderTask())) {
			synchronized (this) {
				final WorkListItem workItem = fetchCoderWorkItem(restQuery, coder);
				if (workItem != null) {
					result = worklistUtilService.convertWorkItemToTask(workItem, codersMap, chartSplMap);
					return result;
				}
				response.setMessage("No work items available, Please contact Supervisor/Admin.");
				return null;
			}
		}
		response.setMessage("You have completed the load of the day, Please contact Supervisor/Admin.");
		return null;

	}
	
	private WorkListItem fetchCoderWorkItem(ChartRestQueryDetails restQueryDetails, Coders coder) throws Exception {
		WorkListItem workItem = workDistributionService.getCoderWorkItem(coder, restQueryDetails, true, null);
		WorkListItem updatedWorkItem = null;
		while (workItem != null) {
			updatedWorkItem = updateWorkItem(workItem, restQueryDetails.isCoderTask(), coder);
			if (updatedWorkItem == null)
				workItem = workDistributionService.getCoderWorkItem(coder, restQueryDetails, true, null);
			else
				break;
		}
		return updatedWorkItem;
	}

	/**
	 * Return true if Coder not yet reached max limit, otherwise false
	 * 
	 * @param coder
	 * @param isCoder
	 * @return
	 */
	private boolean isCoderAvailable(Coders coder, Boolean isCoder) {
		if(isCoder){
			if (coder.getCompleteLoad() < coder.getCoderDailyWorkLoad()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (coder.getCompleteLoad() < coder.getCoderMaxWorkLoad()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	

	private WorkListItem updateWorkItem(WorkListItem workItem, boolean coderTask, Coders coder) throws Exception {
		WorkListItem updatedItem = null;
		Session session = null;
		Transaction txn = null;
		try {
			session = sesionFactory.openSession();
			txn = session.beginTransaction();

			WorkListItem worklist = (WorkListItem) session.get(WorkListItem.class, workItem.getId());

			worklist.setCoderId(coder.getUserId());
			String preStatus = null;
			if (worklist.getStatus() != null) {
				preStatus = worklist.getStatus();
			} else {
				preStatus = ChartStatus.NotStarted.getStatus();
			}

			ReviewWorklist reviewWorklist = null;

			if (coderTask) {
				worklist.setStatus(ChartStatus.CoderAssigned.getStatus());
				reviewWorklist = constructReviewWorklist(worklist, coder.getUserId(),
						ChartStatus.CoderAssigned.getStatus(), preStatus, coderTask);
			} else {
				if (coder.getRemoteQa()) {
					worklist.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
					reviewWorklist = constructReviewWorklist(worklist, coder.getUserId(),
							ChartStatus.GlobalQAAssigned.getStatus(), preStatus, coderTask);
				} else {
					worklist.setStatus(ChartStatus.LocalQAAssigned.getStatus());
					reviewWorklist = constructReviewWorklist(worklist, coder.getUserId(),
							ChartStatus.LocalQAAssigned.getStatus(), preStatus, coderTask);
				}
			}
			//session.saveOrUpdate(reviewWorklist);
			Set<ReviewWorklist> reviewWorkLists = new HashSet<>();
			reviewWorkLists.add(reviewWorklist);
			worklist.setReviewWorkLists(reviewWorkLists);
			worklist.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

			session.saveOrUpdate(worklist);
			txn.commit();
			updatedItem = worklist;
		} catch (final Exception e) {
			txn.rollback();
			logger.error(" EXP updateWorkItem : ", e);
		} finally {
			if (session != null)
				session.close();
		}
		return updatedItem;
	}

	private ReviewWorklist constructReviewWorklist(final WorkListItem workItem, final int coderId, final String status,
			final String previousStatus, final boolean isCoderTask) {
		init();
		final ReviewWorklist reviewWorklist = new ReviewWorklist();
		reviewWorklist.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		reviewWorklist.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		reviewWorklist.setStatus(status);
		reviewWorklist.setPreviousStatus(previousStatus);
		reviewWorklist.setUserReviewd(coderId);
		reviewWorklist.setIsTempData(false);

		/*
		 * This set the user role on review work list. We added this for user
		 * who is coder and QA. To know the activity done by user as a coder or
		 * QA we need this status.
		 */
		reviewWorklist.setUserRole(CommonOperations.getUserReviewedRole(codersMap.get(coderId), isCoderTask));

		reviewWorklist.setTotalTimeTaken(getCoderTotalWorkTimeInMins(reviewWorklist));

		reviewWorklist.setWorkListItem(workItem);
		return reviewWorklist;
	}

	private Integer getCoderTotalWorkTimeInMins(final ReviewWorklist reviewWorklist) {
		Long millis = 0L;
		Integer spent_mins = reviewWorklist.getTotalTimeTaken();
		if (spent_mins != null) {

			if (reviewWorklist.getWorkStartTime() != null)
				millis = System.currentTimeMillis() - reviewWorklist.getWorkStartTime().getTime();
			else
				millis = System.currentTimeMillis() - reviewWorklist.getCreatedDate().getTime();

			return (int) (millis / (60 * 1000) + 1 + spent_mins);
		} else
			return 0;
	}


}
