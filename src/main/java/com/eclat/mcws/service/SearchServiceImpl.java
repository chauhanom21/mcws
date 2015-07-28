package com.eclat.mcws.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.comparator.RevWorkUpdateDateASCComparator;

@Service
public class SearchServiceImpl implements SearchService {

	@Log
	private Logger logger;
	
	@Autowired
	private WorkListDao worklistDao;
	
	@Autowired
	private ChartSpecilizationDao chartSplDao;
	
	@Override
	public List<TaskDetails> getTaskDetails(Long taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TaskDetails> searchTaskDetailsByAccountMRNumber(String queryParam) {
		final List<WorkListItem> workLists = worklistDao.getWorkListItemsByAccountNumber(queryParam);
		if (workLists != null && workLists.size() > 0) {
			return convertWorklistToTaskDetails(workLists);
		} else {
			final List<WorkListItem> workListItems = worklistDao.getWorkListItemsByMRNumber(queryParam);
			if (workListItems != null && workListItems.size() > 0) {
				return convertWorklistToTaskDetails(workListItems);
			}
		}
		return null;
	}

	
	private List<TaskDetails> convertWorklistToTaskDetails(final List<WorkListItem> workLists){
		final List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
		
		for ( WorkListItem item : workLists ) {
			TaskDetails task = new TaskDetails();
			task.setId(item.getId());
			task.setEm(item.getEffortMetric() + "EM");
			task.setLos(CommonOperations.checkForNull(item.getLos()));			
			task.setTat( CommonOperations.checkForNull(item.getTat()) );
			task.setStatus(item.getStatus());
			task.setReceivedDate(DateUtil.df.format(item.getUploadedDate()));
			task.setUpdateDate(DateUtil.df.format(new Date(item.getUpdatedDate().getTime())));
			task.setAccountNumber(item.getAccountNumber());
			task.setMrNumber(item.getMrNumber());
			
			if (item.getClientDetails() != null) {
				task.setClient(item.getClientDetails().getName());
			}
			
			if (item.getChartSpl() != null) {
				try {
					ChartSpecilization chartSpl = chartSplDao.find(Integer.parseInt(item.getChartSpl()));
					task.setChartType(chartSpl.getChartType());
					task.setSpecilization(chartSpl.getChartSpelization());
				} catch(Exception e) {
					logger.error("ChartSpl is Null for===>"+item.getChartSpl());
					task.setChartType("");
					task.setSpecilization("");
				}					
			}
			
			
			final Set<ReviewWorklist> reviewList = item.getReviewWorkLists();
			final List<String> codingDetails = new ArrayList<String>();
			if(reviewList.size() > 0) {
				
				final ArrayList<ReviewWorklist> reviewWorkData = new ArrayList<>(reviewList);
				Collections.sort(reviewWorkData, new RevWorkUpdateDateASCComparator());
				for(ReviewWorklist reviewWork : reviewWorkData) {
					if(reviewWork.getCodingDetails() != null) {
						if( (reviewWork.getStatus().equals(ChartStatus.LocalAudited.getStatus())) || 
								(reviewWork.getStatus().equals(ChartStatus.GlobalAudited.getStatus())) ||
								(reviewWork.getStatus().equals(ChartStatus.LocalAudit.getStatus())) ||
								(reviewWork.getStatus().equals(ChartStatus.GlobalAudit.getStatus()))) {
							
							//Ignore 
						} else {
							codingDetails.add(reviewWork.getCodingDetails());
						}
					}
				}
			}
			task.setCodingHistory(codingDetails);
			taskDetails.add(task);
		}
		
		return taskDetails;
	}
}
