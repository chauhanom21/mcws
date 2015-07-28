package com.eclat.mcws.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.dto.WorkItemHistory;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.comparator.ReviewWorklistComparator;

@Component
public class WorklistUtilService {
	
	@Log
	private  Logger logger; 
	
	@Autowired
	private  CoderDao coderDao;
	
	@Autowired
	private  ChartSpecilizationDao chartSplDao;

	@Autowired
	private  WorkListDao workListDao;
	
	
	public TaskDetails convertWorkItemToTaskDetails(WorkListItem item, final int qaUserId, 
			final boolean fetchTaskDetails, final Map<Integer, Coders> coderMap, 
			final Map<Integer, ChartSpecilization> chartSplMap) throws Exception {
		TaskDetails taskDetail = new TaskDetails();
		if (item != null) {
			taskDetail.setId(item.getId());
			taskDetail.setEm(new Double(item.getEffortMetric()).toString() + "EM");
			taskDetail.setTat(item.getTat());
			taskDetail.setLos(item.getLos());
			taskDetail.setAccountNumber(item.getAccountNumber());			
			taskDetail.setMrNumber(item.getMrNumber());
			taskDetail.setStatus(item.getStatus());
			taskDetail.setSpanClass(ChartStatus.getStatusSpanClass(item.getStatus()));
			if (item.getClientDetails() != null) {
				taskDetail.setClient(item.getClientDetails().getName());
			}
		
			ChartSpecilization chartSpl = chartSplMap.get(Integer.parseInt(item.getChartSpl()));
			taskDetail.setChartType(chartSpl.getChartType());
			taskDetail.setSpecilization(chartSpl.getChartSpelization());
				
			taskDetail.setCoderId(item.getCoderId());	
			taskDetail.setUserRole(CommonOperations.findRoleOfUser(coderMap.get(item.getCoderId())));
			taskDetail.setPatientName(item.getPatientName());
			if (item.getAdmittedDate() != null) {
				taskDetail.setAdmitDate(DateUtil.df.format(item.getAdmittedDate()));
			}
			
			if (item.getDischargedDate() != null) {
				taskDetail.setDischargeDate(DateUtil.df.format(item.getDischargedDate()));
			}			
			
			Set<ReviewWorklist> revieworkList = item.getReviewWorkLists();
			boolean codingDataSet = false;
			if(revieworkList != null) {
				final List<String> codingDataDetails = new ArrayList<String>();
				for( ReviewWorklist reviewWork : revieworkList) { 
					if(reviewWork.getCodingDetails() != null) {
						codingDataDetails.add(reviewWork.getCodingDetails());
					}
				}
				//Add work item history from review work list
				populateTaskWorklistHistory(revieworkList, taskDetail, coderMap);
				
				//Add last coding item details from review work list
				populateLastCodingChartDetails(revieworkList, codingDataSet, fetchTaskDetails, taskDetail, qaUserId);
			}
			
			if ( qaUserId > 0 ) {
				final Coders coderUser = coderMap.get(item.getCoderId());
				taskDetail.setCoderName(coderUser.getFirstname() + " "+coderUser.getLastname());
				
				final Coders coder = coderMap.get(qaUserId);
				if(coder.getLocalQa() || coder.getRemoteQa()) {
					taskDetail.setCoderId(item.getCoderId());	
					taskDetail.setUserRole(CommonOperations.getUserReviewedRole(coderUser, false));
				}
			} 
			
		} 
		
		return taskDetail;				
	}
	
	private void populateLastCodingChartDetails(Set<ReviewWorklist> revieworkList, 
			boolean codingDataSet,  boolean fetchTaskDetails, 
			TaskDetails taskDetail, int qaUserId) {
		
		List<ReviewWorklist> reviewLists = new ArrayList<ReviewWorklist>(revieworkList);
		Collections.sort(reviewLists, new ReviewWorklistComparator());
		for ( ReviewWorklist rw : reviewLists) { 
			
			if(fetchTaskDetails && ((rw.getIsTempData() != null && rw.getIsTempData()) || null != rw.getComment()) && !codingDataSet) {
				taskDetail.setCodingData(rw.getCodingDetails());
				codingDataSet = true;
			}
			if(rw.getStatus().equals(ChartStatus.Completed.getStatus())) {
				taskDetail.setComments(rw.getComment());
				final Map<String, Object> codingData = CommonOperations.parseJsonData(rw.getCodingDetails());
				if(codingData != null) {
					final String drg = (String)codingData.get("drg");
					if(drg != null)
						taskDetail.setDrg(drg);
					else
						taskDetail.setDrg("");
					
					final String insurance = (String)codingData.get("insurance");
					if(insurance != null)
						taskDetail.setInsurance(insurance);
					else
						taskDetail.setInsurance("");
				}
			}
			if(qaUserId == rw.getUserReviewd()) {
				if(rw != null) {
					taskDetail.setTotalWorkTime(DateUtil.convertMinutesToMilliseconds
							(rw.getTotalTimeTaken().longValue()));
					
					if(!"RemoteQA".equalsIgnoreCase(rw.getUserRole()))
						taskDetail.setTaskAssignedTime(DateUtil.convertToTimeZone(new java.util.Date(rw.getCreatedDate().getTime()), 
								Calendar.getInstance().getTimeZone().getID(), "IST") + " IST");
					else	
						taskDetail.setTaskAssignedTime(DateUtil.df2.format(new Date(rw.getCreatedDate().getTime())) + " EDT");
				}
			}
		}
	}
	
	private void populateTaskWorklistHistory(final Set<ReviewWorklist> reviewWorklists, 
			TaskDetails taskDetail, Map<Integer, Coders> coderMap) {
		if(reviewWorklists != null && reviewWorklists.size() > 0) {
			final List<WorkItemHistory> workitemHistory = new ArrayList<WorkItemHistory>();
			for(ReviewWorklist reviewWorklist : reviewWorklists) {
				if(reviewWorklist.getCodingDetails() != null && reviewWorklist.getCodingDetails().length() > 0) {
					final WorkItemHistory workHistory = new WorkItemHistory();
					workHistory.setCodingData(reviewWorklist.getCodingDetails());
					final Coders coder = coderMap.get(reviewWorklist.getUserReviewd());
					workHistory.setCoderName(coder.getFirstname() + " " +coder.getLastname());
					workHistory.setStatus(reviewWorklist.getStatus());
					workHistory.setComments(reviewWorklist.getComment());
					workitemHistory.add(workHistory);
				}
			}
			taskDetail.setWorkitemHistory(workitemHistory);
		}
	}
	
	
	public TaskDetails convertWorkItemToTask(WorkListItem item,
			final Map<Integer, Coders> codersMap, 
			final Map<Integer, ChartSpecilization> chartSplMap) throws Exception {
		TaskDetails taskDetail = new TaskDetails();
		if (item != null) {
			taskDetail.setId(item.getId());
			taskDetail.setEm(item.getEffortMetric() + "EM");
			taskDetail.setLos(CommonOperations.checkForNull(item.getLos()));			
			taskDetail.setTat( CommonOperations.checkForNull(item.getTat()) );
			taskDetail.setStatus(item.getStatus());
			taskDetail.setSpanClass(ChartStatus.getStatusSpanClass(item.getStatus()));
			if (item.getClientDetails() != null) {
				taskDetail.setClient(item.getClientDetails().getName());
			}
			if (item.getChartSpl() != null) {
				//taskDetail.setChartType(item.getChartSpl().split("-")[0]);
				try {
					ChartSpecilization chartSpl = chartSplMap.get(Integer.parseInt(item.getChartSpl()));
					taskDetail.setChartType(chartSpl.getChartType());
					taskDetail.setSpecilization(chartSpl.getChartSpelization());
				} catch(NumberFormatException | NullPointerException e) {
					logger.error("ChartSpl is Null for===>"+item.getChartSpl());
					taskDetail.setChartType("");
					taskDetail.setSpecilization("");
				}					
			}
			taskDetail.setUpdateDateMiliis(item.getUpdatedDate().getTime());
			if (item.getAdmittedDate()!=null) {
				taskDetail.setAdmitDate(DateUtil.df.format(item.getAdmittedDate()));
			}
			
			if (item.getDischargedDate()!=null) {
				taskDetail.setDischargeDate(DateUtil.df.format(item.getDischargedDate()));
			}
			taskDetail.setAccountNumber(item.getAccountNumber());
			taskDetail.setMrNumber(item.getMrNumber());
			final Integer coderId = item.getCoderId();
			if ( coderId != null ) {
				taskDetail.setCoderId(item.getCoderId());	
				taskDetail.setUserRole(CommonOperations.findRoleOfUser(codersMap.get(item.getCoderId())));
				final Coders coder = codersMap.get(coderId);
				
				final ReviewWorklist reviewWorkList = item.getWorklistPreviousCodingDetail(coderId);
				if(coder.getLocalQa() || coder.getRemoteQa()) {
					if(reviewWorkList != null) {
						if(null != reviewWorkList.getComment())
							taskDetail.setCodingData(reviewWorkList.getCodingDetails());
					}
				}
				

				if(reviewWorkList != null) {
					final Coders user = codersMap.get(reviewWorkList.getUserReviewd());
					taskDetail.setCoderName(user.getFirstname() + " "+user.getLastname());
				} else {
					taskDetail.setCoderName(coder.getFirstname()+ " "+coder.getLastname());
				}
				
				/**
				 * At time of fetch, Review Work List not updated. 
				 * To get updated Entity data from session.
				 */
				WorkListItem updatedItem = workListDao.find(item.getId());
				final ReviewWorklist reviewWork = updatedItem.getReviewWorkListsByUser(coderId);
				if( null != reviewWork) {
					taskDetail.setTotalWorkTime(DateUtil.convertMinutesToMilliseconds
							(reviewWork.getTotalTimeTaken().longValue()));
					//Added while setting start_work_time in revWorklist
					taskDetail.setReviewWorkListId(reviewWork.getId());
					//Adding previous status in mytasks screen
					taskDetail.setPreviousStatus(reviewWork.getPreviousStatus());
					if(!"RemoteQA".equalsIgnoreCase(reviewWork.getUserRole()))
						taskDetail.setTaskAssignedTime(DateUtil.convertToTimeZone(new java.util.Date(reviewWork.getCreatedDate().getTime()), 
								Calendar.getInstance().getTimeZone().getID(), "IST") + " IST");
					else	
						taskDetail.setTaskAssignedTime(DateUtil.df2.format(new Date(reviewWork.getCreatedDate().getTime())) + " EDT");
				}
			}
			
		} else {
			throw new Exception("Invalid Item");
		}
		return taskDetail;				
	}
}
