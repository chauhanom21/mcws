package com.eclat.mcws.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.dto.GradingSheetDetails;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartAuditStatus;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.dao.AuditLogDao;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.GradingSheetDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.AuditLogDetails;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.GradingSheet;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;
import com.eclat.mcws.persistence.entity.WeightageMaster;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.comparator.ChartComparator;
import com.eclat.mcws.util.comparator.ClientComparator;
import com.eclat.mcws.util.rest.Response;

@Service
public class AuditDataServiceImpl implements AuditDataService {

	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int TWENTY_THREE = 23;
	private static final int FIFTY_NINE = 59;

	@Log
	private Logger logger;

	@Autowired
	private WorkListDao workListDao;

	@Autowired
	private AuditLogDao auditLogDao;

	@Autowired
	private WorklistUtilService worklistUtilService;

	@Autowired
	private AppStartupComponent appStartupComponent;

	@Autowired
	private GradingSheetDao gradingSheetDao;

	@Autowired
	private ClientDetailsDao clientDetailDao;

	Map<Integer, Coders> codersMap;
	Map<Integer, ChartSpecilization> chartSplMap;

	@PostConstruct
	public void init() {
		codersMap = appStartupComponent.getCodersMap();
		chartSplMap = appStartupComponent.getChartSplObjMap();
	}

	public TaskDetails fetchTaskToAudit(ChartRestQueryDetails restQueryDetails, Response<TaskDetails> response)
			throws Exception {
		init();
		final Integer auditorId = restQueryDetails.getUserId();
		Coders coder = codersMap.get(auditorId);
		if (isCoderAvailable(coder)) {
			final List<UsersClient> useclients = new ArrayList<UsersClient>(coder.getUserClients());
			synchronized (this) {

				// Sort the array based on the list
				Collections.sort(useclients, new ClientComparator());

				for (UsersClient usrClient : useclients) {
					final List<AuditLogDetails> auditLogDetails = auditLogDao.getAuditLogDetailsByClientId(
							usrClient.getClientId(), coder);
					if (auditLogDetails != null) {
						Set<UserCharts> chartSpecs = coder.getUserCharts();
						// Sort the Charts based on the priority
						List<UserCharts> chartSpecsList = new ArrayList<UserCharts>(chartSpecs);
						Collections.sort(chartSpecsList, new ChartComparator());

						final AuditLogDetails auditLogDetail = searchAuditItem(auditLogDetails, chartSpecsList);
						if (auditLogDetail != null) {
							auditLogDetail.setStatus(ChartAuditStatus.INPROGRESS.toString());
							WorkListItem workItem = workListDao.find(auditLogDetail.getWorklistId());
							final String auditUserStatus = getAuditStatusByCoder(coder);
							// updating All else roll back
							auditLogDao.updateWorklistAndAuditLogDetails(auditLogDetail, workItem, auditorId,
									auditUserStatus);

							return worklistUtilService.convertWorkItemToTaskDetails(workItem, auditorId, false,
									codersMap, chartSplMap);

						}
						response.setMessage("No more work items are available to Audit based on your profile details! "
								+ "Please contact supervisor/admin or moved to Coding Task to complete your today workload !!!");
					}
				}

			}
			return null;
		}
		response.setMessage("No more work items to Audit! You have completed your today's workload. Contact supervisor/admin !!!");
		return null;
	}

	private String getAuditStatusByCoder(final Coders coder) {
		String auditStatus = "";
		final String coderRole = CommonOperations.findRoleOfUser(coder);
		if (UserRoles.LocalQA.toString().equals(coderRole))
			auditStatus = ChartStatus.LocalAudit.getStatus();
		else if (UserRoles.RemoteQA.toString().equals(coderRole))
			auditStatus = ChartStatus.GlobalAudit.getStatus();
		return auditStatus;
	}

	/**
	 * Return true if Coder is not Yet Exhausted otherwise false
	 * 
	 * @param coder
	 * @return
	 */
	private boolean isCoderAvailable(Coders coder) {
		if (coder.getCompleteLoad() < coder.getCoderMaxWorkLoad()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<TaskDetails> getAuditInProgressCharts(Integer coderId) throws Exception {
		final List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
		List<WorkListItem> workItems = workListDao.getAuditInProgressItemsByCoder(coderId.intValue());
		if (workItems.size() > 0) {
			for (WorkListItem workItem : workItems) {
				taskDetails.add(worklistUtilService.convertWorkItemToTaskDetails(workItem, coderId, false, codersMap,
						chartSplMap));
			}

		}

		return taskDetails;
	}

	public List<TaskDetails> getGlobalQaAuditItemsByUpdateDateRange(final String startDate, final String endDate,
			final Integer coderId) throws Exception {

		Calendar cal = null;
		if (startDate != null && startDate.length() > 0) {
			cal = CommonOperations.convertStringValueToDateFormat(startDate);
		} else {
			cal = new GregorianCalendar();
		}
		cal.set(Calendar.HOUR_OF_DAY, ZERO);
		cal.set(Calendar.MINUTE, ZERO);
		cal.set(Calendar.SECOND, ONE);
		Timestamp fromDate = new Timestamp(cal.getTimeInMillis());

		if (endDate != null && endDate.length() > 0) {
			cal = CommonOperations.convertStringValueToDateFormat(endDate);
		} else {
			cal = new GregorianCalendar();
		}
		cal.set(Calendar.HOUR_OF_DAY, TWENTY_THREE);
		cal.set(Calendar.MINUTE, FIFTY_NINE);
		cal.set(Calendar.SECOND, FIFTY_NINE);

		Timestamp toDate = new Timestamp(cal.getTimeInMillis());

		List<WorkListItem> worklistItems = workListDao.getAuditItemsForRemoteQAByUpdateDateRange(fromDate, toDate);

		List<WorkListItem> inProgAuditWorklist = workListDao.getAuditInProgressItemsByCoder(coderId);
		if (inProgAuditWorklist != null && inProgAuditWorklist.size() > 0) {
			worklistItems.addAll(inProgAuditWorklist);
		}

		if (worklistItems != null && worklistItems.size() > 0) {
			final List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
			for (WorkListItem workItem : worklistItems) {
				taskDetails.add(worklistUtilService.convertWorkItemToTaskDetails(workItem, coderId, false, codersMap,
						chartSplMap));
			}
			return taskDetails;
		}
		return null;
	}

	public void assignedAuditChartToUser(final Integer userId, final Long taskId) throws Exception {
		init();
		String auditStatus = "";
		Coders coder = codersMap.get(userId);
		final String userRole = CommonOperations.findRoleOfUser(coder);
		if (UserRoles.LocalQA.toString().equals(userRole)) {
			auditStatus = ChartStatus.LocalAudit.getStatus();
		} else if (UserRoles.RemoteQA.toString().equals(userRole)) {
			auditStatus = ChartStatus.GlobalAudit.getStatus();
		}
		final ReviewWorklist reviewWorkList = workListDao.getReviewWorklistByUserIdAndWorklistId(userId, taskId);
		if (reviewWorkList != null
				&& (reviewWorkList.getStatus().equals(ChartStatus.LocalAudit.getStatus()) || reviewWorkList.getStatus()
						.equals(ChartStatus.GlobalAudit.getStatus()))) {
			// Skip Updating task
		} else {
			final WorkListItem workItem = workListDao.find(taskId);
			if (workItem != null) {
				final String preStatus = workItem.getStatus();
				workItem.setCoderId(userId);
				workItem.setStatus(auditStatus);
				workItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

				// Insert record on review worklist
				saveReviewWorklist(workItem, userId, auditStatus, preStatus, false);

				workListDao.update(workItem);
			}
		}
	}

	/**
	 * 
	 * @param auditLogDetails
	 * @param chartList
	 * @return AuditItem matched with chart specialization, Chart Specialization
	 *         data are sorted by ascending order of priority
	 */
	private AuditLogDetails searchAuditItem(final List<AuditLogDetails> auditLogDetails, List<UserCharts> chartList) {
		for (AuditLogDetails auditItem : auditLogDetails) {
			for (UserCharts chart : chartList) {
				logger.debug(" ----> AuditItem spl_ID : " + auditItem.getChartSpeId());
				logger.debug("User Chart spl_ID : " + chart.getChartSpecializationId());
				if (auditItem.getChartSpeId().intValue() == chart.getChartSpecializationId()) {
					return auditItem;
				}
			}
		}
		// If nothing means return null
		return null;
	}

	@Override
	public Map<String, Double> getAllWeightageMasterDataByChartType(final String chartType) throws Exception {
		final List<WeightageMaster> weightageData = gradingSheetDao.getAllWeightageMasterDataByChartType(chartType);
		final Map<String, Double> weightages = new HashMap<String, Double>();
		if (weightageData != null) {
			for (WeightageMaster weightage : weightageData) {
				weightages.put(weightage.getColumnName(), weightage.getWaightage());
			}
		}

		return weightages;
	}

	@Override
	public GradingSheetDetails getAuditedChartDetails(final long chartId, final int userId) {
		final WorkListItem worklistItem = workListDao.find(chartId);
		if (worklistItem != null) {
			final GradingSheetDetails qaGradingDetails = new GradingSheetDetails();
			qaGradingDetails.setWorklistId(new Long(worklistItem.getId()));
			qaGradingDetails.setAccountNumber(worklistItem.getAccountNumber());
			qaGradingDetails.setMrNumber(worklistItem.getMrNumber());
			final ClientDetails clientDetail = worklistItem.getClientDetails();
			qaGradingDetails.setClientId(clientDetail.getId());
			qaGradingDetails.setClient(clientDetail.getName());

			final ReviewWorklist revWorklist = workListDao.getReviewWorklistByWorklistIdAndStatus(worklistItem.getId(),
					ChartStatus.Completed.getStatus());

			qaGradingDetails.setUserCompleted(revWorklist.getUserReviewd());
			if (worklistItem.getChartSpl() != null) {
				final ChartSpecilization chartSpec = chartSplMap.get(Integer.parseInt(worklistItem.getChartSpl()));
				qaGradingDetails.setChartSpecId(chartSpec.getId());
				qaGradingDetails.setSpecilization(chartSpec.getChartSpelization());
				qaGradingDetails.setChartType(chartSpec.getChartType());
			}

			try {
				if (!isAuditChartAssignedToUser(userId, chartId))
					assignedAuditChartToUser(userId, chartId);
			} catch (Exception e) {
				logger.error("Exception on Audit chart assignment: ", e);
			}
			return qaGradingDetails;
		}
		return null;
	}

	@Override
	public boolean saveAuditChart(final GradingSheetDetails gradingSheetDetails) throws Exception {
		init();
		final GradingSheet gradingSheet = new GradingSheet();
		gradingSheet.setWorklistId(gradingSheetDetails.getWorklistId());
		gradingSheet.setClientId(gradingSheetDetails.getClientId());
		gradingSheet.setChartSpecializationId(gradingSheetDetails.getChartSpecId());
		gradingSheet.setDrg(gradingSheetDetails.getDrg());
		gradingSheet.setUserAudited(gradingSheetDetails.getUserAudited());
		gradingSheet.setUserCompleted(gradingSheetDetails.getUserCompleted());
		if (gradingSheetDetails.getTotalAccuracy() != null) {
			gradingSheet.setAccuracy(gradingSheetDetails.getTotalAccuracy());
		} else {
			gradingSheet.setAccuracy(0.0);
		}

		if (gradingSheetDetails.getGradingsheet() != null
				&& gradingSheetDetails.getGradingsheet().toJSONString().length() > 0) {
			gradingSheet.setGradingSheetData(gradingSheetDetails.getGradingsheet().toJSONString());
		}

		Coders coder = codersMap.get(gradingSheetDetails.getUserAudited());
		logger.info("CREATED gradingSheet : " + gradingSheet);
		return auditLogDao.saveAuditDataAndUpdateAuditLogAndWorklistAndUserWorkload(gradingSheet, coder);

	}

	private void saveReviewWorklist(final WorkListItem workItem, final int coderId, final String status,
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

		reviewWorklist.setWorkListItem(workItem);
		workListDao.saveOrUpdateReviewWorklist(reviewWorklist);
	}

	private boolean isAuditChartAssignedToUser(final Integer userId, final Long taskId) {
		final WorkListItem workItem = workListDao.find(taskId);
		if (workItem.getStatus() != null
				&& (ChartStatus.LocalAudit.getStatus().equals(workItem.getStatus()) || ChartStatus.GlobalAudit
						.getStatus().equals(workItem.getStatus()))
				&& workItem.getCoderId().intValue() != userId.intValue()) {
			return true;
		}
		return false;
	}
}
