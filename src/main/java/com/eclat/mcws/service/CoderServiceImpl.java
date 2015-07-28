package com.eclat.mcws.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.CodingChartData;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.UserDAO;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.persistence.entity.UsersClient;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.comparator.RevWorkUpdateDateASCComparator;
import com.eclat.mcws.util.comparator.SortTaskDetailsUpdateDateDescComparator;
import com.eclat.mcws.util.comparator.WorklistIdDescComparator;

@Service
public class CoderServiceImpl implements CoderService {
	private static final int ZERO = 0;
	private static final int ONE = 1;

	@Log
	private Logger logger;

	@Autowired
	private UserDAO userDao;

	@Autowired
	private CoderDao coderDao;

	@Autowired
	private ChartSpecilizationDao chartSplDao;

	@Autowired
	private WorkListDao workListDao;

	@Autowired
	ClientDetailsDao clientDetailDao;

	@Autowired
	private WorklistUtilService worklistUtilService;

	@Autowired
	private AppStartupComponent appStartupComponent;

	@Autowired
	private WorkDistributionService workDistributionService;

	Map<Integer, Coders> codersMap;
	Map<Integer, ChartSpecilization> chartSplMap;

	@PostConstruct
	public void init() {
		codersMap = appStartupComponent.getCodersMap();
		chartSplMap = appStartupComponent.getChartSplObjMap();
	}

	@Override
	public List<TaskDetails> fetchAllWorkLists(int coderId, String date) throws Exception {
		List<WorkListItem> workItems = null;
		workItems = workListDao.getAllWorksByCoder(coderId);
		logger.info("========Clients Size for codeer=====>" + workItems.size());
		return convertWorkListToTask(workItems);
	}

	@Override
	public List<TaskDetails> getActiveCharts(int coderId) throws Exception {
		List<WorkListItem> workItems = null;
		workItems = workListDao.getActiveWorkListByCoder(coderId);
		logger.info("========Clients Size for codeer=====>" + workItems.size());
		return convertWorkListToTask(workItems);
	}

	@Override
	public List<TaskDetails> getCompletedCharts(int coderId, String date) throws Exception {
		final Timestamp fromDateTime = DateUtil.convertStringValueToDateFormat(date);
		final Timestamp startDate = DateUtil.constructFromDateValue(fromDateTime.getTime());
		final Timestamp endDate = DateUtil.constructEndDateValue(null);

		final List<WorkListItem> workListItems = workListDao.getCompletedWorksByCoder(coderId, startDate, endDate);
		logger.debug("========> Completed WorkList Items  : " + workListItems.size() + "  for Coder: " + coderId);
		return convertWorkListToTaskDetails(workListItems, coderId);
	}

	@Override
	public List<TaskDetails> getCompletedChartsByDateRange(int coderId, String fromDate, String toDate)
			throws Exception {

		final Timestamp fromDateTime = DateUtil.convertStringValueToDateFormat(fromDate);
		final Timestamp startDate = DateUtil.constructFromDateValue(fromDateTime.getTime());
		final Timestamp toDateTime = DateUtil.convertStringValueToDateFormat(toDate);
		final Timestamp endDate = DateUtil.constructEndDateValue(toDateTime.getTime());

		final List<WorkListItem> workListItems = workListDao.getCompletedWorksByCoder(coderId, startDate, endDate);
		logger.debug("========> Completed WorkList Items  : " + workListItems.size() + "  for Coder: " + coderId);
		return convertWorkListToTaskDetails(workListItems, coderId);
	}

	private List<TaskDetails> convertWorkListToTaskDetails(List<WorkListItem> workListItems, Integer userId)
			throws Exception {
		final List<TaskDetails> taskDetails = new ArrayList<>();
		for (WorkListItem worklistItem : workListItems) {
			taskDetails.add(constructUserAllTaskDetails(worklistItem, userId));
		}
		return taskDetails;
	}

	private TaskDetails constructUserAllTaskDetails(WorkListItem item, final Integer userId) throws Exception {
		init();
		final TaskDetails taskDetail = new TaskDetails();
		if (item != null) {
			taskDetail.setId(item.getId());
			taskDetail.setEm(item.getEffortMetric() + "EM");
			taskDetail.setLos(CommonOperations.checkForNull(item.getLos()));
			taskDetail.setTat(CommonOperations.checkForNull(item.getTat()));
			taskDetail.setStatus(item.getStatus());
			taskDetail.setReceivedDate(DateUtil.df.format(item.getUploadedDate()));
			taskDetail.setSpanClass(ChartStatus.getStatusSpanClass(item.getStatus()));

			if (item.getClientDetails() != null) {
				taskDetail.setClient(item.getClientDetails().getName());
			}

			if (item.getChartSpl() != null) {
				try {
					ChartSpecilization chartSpl = chartSplMap.get(Integer.parseInt(item.getChartSpl()));
					taskDetail.setChartType(chartSpl.getChartType());
					taskDetail.setSpecilization(chartSpl.getChartSpelization());
				} catch (NumberFormatException | NullPointerException e) {
					logger.error("ChartSpl is Null for===>" + item.getChartSpl());
					taskDetail.setChartType("");
					taskDetail.setSpecilization("");
				}
			}

			if (item.getAdmittedDate() != null) {
				taskDetail.setAdmitDate(DateUtil.df.format(item.getAdmittedDate()));
			}

			if (item.getDischargedDate() != null) {
				taskDetail.setDischargeDate(DateUtil.df.format(item.getDischargedDate()));
			}
			taskDetail.setAccountNumber(item.getAccountNumber());
			taskDetail.setMrNumber(item.getMrNumber());

			if (userId != null) {
				final Set<ReviewWorklist> reviewList = item.getReviewWorkLists();
				final List<ReviewWorklist> reviewedUserList = new ArrayList<ReviewWorklist>();
				if (reviewList.size() > 0) {
					final List<String> codingDataDetails = new ArrayList<String>();

					final ArrayList<ReviewWorklist> reviewWorkData = new ArrayList<>(reviewList);
					Collections.sort(reviewWorkData, new RevWorkUpdateDateASCComparator());

					for (ReviewWorklist reviewWork : reviewWorkData) {
						if (reviewWork.getUserReviewd() != userId.intValue()) {
							reviewedUserList.add(reviewWork);
						} else {
							taskDetail.setUserStatus(reviewWork.getStatus());
							taskDetail.setUpdateDate(DateUtil.df
									.format(new Date(reviewWork.getUpdatedDate().getTime())));
						}

						if (reviewWork.getCodingDetails() != null) {
							if ((reviewWork.getStatus().equals(ChartStatus.LocalAudited.getStatus()))
									|| (reviewWork.getStatus().equals(ChartStatus.GlobalAudited.getStatus()))
									|| (reviewWork.getStatus().equals(ChartStatus.LocalAudit.getStatus()))
									|| (reviewWork.getStatus().equals(ChartStatus.GlobalAudit.getStatus()))) {

								// Ignore
							} else {
								codingDataDetails.add(reviewWork.getCodingDetails());
							}
						}
					}

					taskDetail.setCodingHistory(codingDataDetails);
					setUserReviewedToTaskDetails(taskDetail, userId, reviewedUserList);

					for (ReviewWorklist reviewWorkItem : reviewList) {
						if (reviewWorkItem.getUserReviewd() == userId)
							taskDetail
									.setTotalTimeTaken(DateUtil.convertMinsToHHMM(reviewWorkItem.getTotalTimeTaken()));
					}
				}
			}
		} else {
			throw new Exception("Invalid Item");
		}
		return taskDetail;
	}

	/**
	 * 
	 * @param taskDetail
	 * @param userId
	 * @param reviewedUserList
	 * 
	 *            this method add the reviewed user details to the particular
	 *            chart, if the chart marked as CNR by coder/qa.
	 */

	private void setUserReviewedToTaskDetails(TaskDetails taskDetail, Integer userId,
			List<ReviewWorklist> reviewedUserList) {
		init();
		if (reviewedUserList.size() > 0) {
			final Coders coder = codersMap.get(userId);
			for (ReviewWorklist reviewItem : reviewedUserList) {
				final Coders reviewedUser = codersMap.get(reviewItem.getUserReviewd());

				if (taskDetail.getQaReviewed() == null || taskDetail.getQaReviewed().equals("N/A")) {
					if (coder.getCoder() && (reviewItem.getUserRole().equals(UserRoles.LocalQA.toString()))) {
						taskDetail.setQaReviewed(reviewedUser.getFirstname() + " " + reviewedUser.getLastname());
					} else if (coder.getLocalQa() && (reviewItem.getUserRole().equals(UserRoles.RemoteQA.toString()))) {
						taskDetail.setQaReviewed(reviewedUser.getFirstname() + " " + reviewedUser.getLastname());
					} else {
						taskDetail.setQaReviewed("N/A");
					}
				}
			}
		} else {
			taskDetail.setQaReviewed("N/A");
		}
	}

	@Override
	public List<TaskDetails> getInProgressCharts(int coderId) throws Exception {
		List<WorkListItem> workItems = workListDao.getInProgWorksByCoder(coderId);
		Collections.sort(workItems, new WorklistIdDescComparator());
		logger.debug("------> In Progress work items for coder(" + coderId + ") : " + workItems.size());
		return convertWorkListToTask(workItems);
	}

	public List<TaskDetails> convertWorkListToTask(List<WorkListItem> workListItems) throws Exception {
		List<TaskDetails> taskDetails = new ArrayList<>();
		for (WorkListItem item : workListItems) {
			taskDetails.add(worklistUtilService.convertWorkItemToTask(item, codersMap, chartSplMap));
		}
		Collections.sort(taskDetails, new SortTaskDetailsUpdateDateDescComparator());
		return taskDetails;
	}

	@Override
	public TaskDetails updateWorkList(long workListId, String status) throws Exception {
		WorkListItem item = workListDao.find(workListId);
		item.setStatus(status);
		item.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		item = workListDao.update(item);
		return worklistUtilService.convertWorkItemToTask(item, codersMap, chartSplMap);
	}

	@Override
	public List<CodersQADto> getAllCodersData(final Integer client, final Integer chartSpecId) {
		final List<CodersQADto> codersList = new ArrayList<CodersQADto>();
		final List<Integer> userIds = getUserIdsByClientAndChartSpl(client, chartSpecId);
		final List<Coders> coders = getCoderDetailssByUserIds(userIds);
		if (coders != null && coders.size() > 0) {
			for (Coders coder : coders) {
				if (coder.getIsAvailable() && coder.getCoder())
					codersList.add(new CodersQADto(coder.getUserId(), coder.getFirstname()));
			}
		}
		return codersList;
	}

	@Override
	public List<CodersQADto> getAllLocalQAData(final Integer client, final Integer chartSpecId) {
		final List<CodersQADto> localQAList = new ArrayList<CodersQADto>();
		final List<Integer> userIds = getUserIdsByClientAndChartSpl(client, chartSpecId);
		final List<Coders> coders = getCoderDetailssByUserIds(userIds);
		if (coders != null && coders.size() > 0) {
			for (Coders coder : coders) {
				if (coder.getIsAvailable() && coder.getLocalQa())
					localQAList.add(new CodersQADto(coder.getUserId(), coder.getFirstname()));
			}
		}
		return localQAList;
	}

	@Override
	public List<CodersQADto> getAllRemoteQAData(final Integer client, final Integer chartSpecId) {
		final List<CodersQADto> remoteQAList = new ArrayList<CodersQADto>();
		final List<Integer> userIds = getUserIdsByClientAndChartSpl(client, chartSpecId);
		final List<Coders> coders = getCoderDetailssByUserIds(userIds);
		if (coders != null && coders.size() > 0) {
			for (Coders coder : coders) {
				if (coder.getIsAvailable() && coder.getRemoteQa())
					remoteQAList.add(new CodersQADto(coder.getUserId(), coder.getFirstname()));
			}
		}
		return remoteQAList;
	}

	private List<Integer> getUserIdsByClientAndChartSpl(final Integer client, final Integer chartSpecId) {
		final List<UsersClient> usersClients = coderDao.getAllUserClientsByClientId(client);
		final List<Integer> userIds = new ArrayList<Integer>();
		for (UsersClient userClient : usersClients) {
			userIds.add(userClient.getCoder().getUserId());
		}
		final List<UserCharts> userCharts = coderDao.getUsersChartByChartSpeIdAndUserIds(userIds, chartSpecId);
		if (userCharts != null && userCharts.size() > 0) {
			userIds.clear();
			for (UserCharts userChart : userCharts) {
				userIds.add(userChart.getCoder().getUserId());
			}
		}

		return userIds;
	}

	public List<String> getAllUsersName() {
		final List<String> usersName = new ArrayList<String>();
		final List<Users> users = userDao.findAll();
		if (users != null && users.size() > 0) {
			for (Users user : users) {
				usersName.add(user.getFirstname());
			}
		}

		return usersName;
	}

	public List<String> getAllClientsName() {
		final List<ClientDetails> clientDetails = clientDetailDao.findAll();
		final List<String> clientsName = new ArrayList<>();
		if (clientDetails != null && clientDetails.size() > 0) {
			for (ClientDetails clientDetail : clientDetails) {
				clientsName.add(clientDetail.getName());
			}
		}
		return clientsName;
	}

	public List<Integer> getAllClientIds() {
		final List<ClientDetails> clientDetails = clientDetailDao.findAll();
		final List<Integer> clientIds = new ArrayList<>();
		if (clientDetails != null && clientDetails.size() > 0) {
			for (ClientDetails clientDetail : clientDetails) {
				clientIds.add(clientDetail.getId());
			}
		}
		return clientIds;
	}

	@Override
	public TaskDetails getWorkListDetails(String queryString) throws Exception {
		init();
		final Map<String, Object> map = CommonOperations.parseJsonData(queryString);
		Long worklistId;
		if (map.get("taskId") instanceof Integer) {
			worklistId = new Long((Integer) map.get("taskId"));
		} else {
			worklistId = (Long) map.get("taskId");
		}

		WorkListItem item = workListDao.find(worklistId);

		int userId;
		if (map.get("userId") instanceof Integer) {
			userId = (Integer) map.get("userId");
		} else {
			userId = ((Long) map.get("userId")).intValue();
		}
		/**
		 * Always add work start time to current time whenever user opens the
		 * chart for coding.
		 */
		workListDao.updateWorkStartTime(worklistId, userId);
		return worklistUtilService.convertWorkItemToTaskDetails(item, userId, true, codersMap, chartSplMap);
	}

	/**
	 * This method update the status of the task item in worklist table. And
	 * update the review_worklist table records based on user input on form. On
	 * the review_worklist table will update the status and comments. After that
	 * in the coding_chart table insert/update the complete data of the form as
	 * JSON String.
	 * 
	 * If any error/exception happened during the save/update review_worklist
	 * then It returns without updating the coding_chart table.
	 */
	public boolean saveOrUpdateCodingChartDetails(final CodingChartData codingChartData) throws Exception {
		boolean flag = false;
		String codingChartDetails = codingChartData.getCodingData().toJSONString();
		if (codingChartDetails != null && codingChartDetails.length() > 0) {
			final Map<String, Object> map = CommonOperations.parseJsonData(codingChartDetails);
			final String chartType = (String) map.get("chartType");

			Long worklistId;
			if (map.get("id") instanceof Integer) {
				worklistId = new Long((Integer) map.get("id"));
			} else {
				worklistId = (Long) map.get("id");
			}
			final Integer coderId = (Integer) map.get("coderId");
			final String client = (String) map.get("client");
			final String status = getFinalCompStatus(coderId, client, (String) map.get("status"));
			final String comments = (String) map.get("comments");

			logger.debug("===> worklistId : " + worklistId + " coderId : " + coderId + " client : " + client
					+ " status : " + status + " chartType : " + chartType + " comments : " + comments);

			final WorkListItem worklistItem = workListDao.find(worklistId);
			if (worklistItem != null) {
				// If user save the coding form then update the coding form data
				// into DB.
				if (codingChartData.isTempData()) {
					
					flag = updateReviewWorklist(worklistId, coderId, comments, status, true, codingChartDetails,
							codingChartData.getInsurance(), codingChartData.isTempData());

					// If User submits the Same chart multiple time, without
					// changing status.
					// Update only the chart detail don't update coder's completed Load
					// and return.
				} else if (!codingChartData.isTempData() && worklistItem.getStatus().equals(status.trim())) {
					
					flag = updateReviewWorklist(worklistId, coderId, comments, status, true,
							codingChartDetails, codingChartData.getInsurance(), codingChartData.isTempData());
				} else {
					//If normal form submit then save/update chart detail in DB
					// And update coder's completed Load
					final boolean updated = updateReviewWorklist(worklistId, coderId, comments, status, true,
							codingChartDetails, codingChartData.getInsurance(), codingChartData.isTempData());

					if (updated) {
						synchronized (worklistId) {
							updateCompletedWorkloadOfCoder(worklistId, coderId, status);
							flag = true;
						}
					}
				}
			}
		}
		return flag;
	}

	private String getFinalCompStatus(int coderId, String client, String status) {
		init();
		if (status != null && status.equals(ChartStatus.Completed.getStatus())) {
			Coders coder = codersMap.get(coderId);
			if ((!coder.getRemoteQa() && !coder.getLocalQa() && (coder.getNewCoder() || clientDetailDao
					.getClientByName(client).isNewClient()))) {
				return ChartStatus.CompletedNR.getStatus();
			}
		}
		return status;
	}

	/**
	 * This method is consists the logic of updating the value of completedLoad
	 * of the user on coders table. After completed the coding of each work
	 * item's the EM value of work item is added to completedLoad. If user is
	 * first time login for the day and finished the first work of the day then
	 * old value of the completedLoad, is removed and new value is added.
	 */
	private void updateCompletedWorkloadOfCoder(final Long worklistId, final int userId, final String status) {
		logger.info("********* Updating coder's EM workload *************");
		if (status != null
				&& (status.equals(ChartStatus.MISC.getStatus()) || status.equals(ChartStatus.InComplete.getStatus()))) {
			// do not decrease user worload, ignore
		} else {
			init();
			final WorkListItem worklistItem = workListDao.find(worklistId);
			Coders coder = coderDao.find(userId);
			// Coders coder = codersMap.get(userId);
			Date updateDate = null;
			logger.info(" Existing Coder's Completed Work Load : " + coder.getCompleteLoad());
			logger.info(" Current Chart WorkLoad : " + worklistItem.getEffortMetric());
			if (coder.getUpdatedDate() != null) {
				updateDate = new Date(coder.getUpdatedDate().getTime());
				Calendar cal = new GregorianCalendar();
				cal.set(Calendar.HOUR_OF_DAY, ZERO);
				cal.set(Calendar.MINUTE, ZERO);
				cal.set(Calendar.SECOND, ONE);
				if (updateDate.after(new Date(cal.getTimeInMillis()))) {
					final double emLoad = coder.getCompleteLoad() + worklistItem.getEffortMetric();
					coder.setCompleteLoad(emLoad);
				} else {
					coder.setCompleteLoad(worklistItem.getEffortMetric());
				}
			} else {
				coder.setCompleteLoad(worklistItem.getEffortMetric());
			}

			logger.info(" ********** Updated coder's Complete WorkLoad : " + coder.getCompleteLoad());
			coder.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			coderDao.save(coder);
		}
	}

	private boolean updateReviewWorklist(long worklistId, int userId, String comment, String status,
			boolean updateChartStatus, String codingDetails, String insurance, Boolean isTempData) throws Exception {
		final ReviewWorklist reviewWorklist = workListDao.getReviewWorklistByUserIdAndWorklistId(userId, worklistId);
		if (reviewWorklist != null) {
			if (status != null && status.trim().length() > 0) {
				reviewWorklist.setStatus(status);
			} else {
				status = reviewWorklist.getStatus();
			}

			reviewWorklist.setComment(comment);
			reviewWorklist.setCodingDetails(codingDetails);
			reviewWorklist.setIsTempData(isTempData);
			reviewWorklist.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			// This Updates the status on worklist table
			if (updateChartStatus) {
				reviewWorklist.getWorkListItem().setStatus(status);
				if (insurance != null) {
					reviewWorklist.getWorkListItem().setInsurance(insurance);
				}
			}

			reviewWorklist.getWorkListItem().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			workListDao.saveOrUpdateReviewWorklist(reviewWorklist);

			return true;
		} else {
			logger.info("##### No Records found in review_worklist table for worklist Id :: " + worklistId);
			return false;
		}
	}

	@Override
	public boolean isCoder(int coder) {
		init();
		if (codersMap.get(coder).getCoder() != null && codersMap.get(coder).getCoder())
			return true;
		else
			return false;
	}

	@Override
	public void updateWorkStartTimeAndTaskStatus(final Long worklistId, final Integer userId, final String workType)
			throws Exception {
		final WorkListItem workList = workListDao.find(worklistId);
		ReviewWorklist reviewWork = workList.getReviewWorkListsByUser(userId);
		if (reviewWork != null) {
			final Integer preWorkTime = reviewWork.getTotalTimeTaken();
			final Long totalWorkTime = (System.currentTimeMillis() - reviewWork.getWorkStartTime().getTime());

			reviewWork.setTotalTimeTaken((DateUtil.convertMillsToMins(totalWorkTime) + preWorkTime + 1));
			updateStatusToAssigned(workList, reviewWork, workType);
		}

		workListDao.updateWorkStartTime(worklistId, userId);
	}

	public void saveOrUpdateNotes(final String notes, final Integer userId) throws Exception {
		Coders coder = coderDao.find(userId);
		if (coder != null) {
			coder.setNotes(notes);
			coderDao.save(coder);
		}
	}

	// Calling find method to get updated coders entity object.
	// After updating Notes value.
	public String loadUserNotesValue(final Integer userId) throws Exception {
		final Coders coder = coderDao.find(userId);
		return coder.getNotes();
	}

	public Boolean updateStatusToInProgress(Long taskId) throws Exception {

		final WorkListItem workList = workListDao.find(taskId);
		Set<ReviewWorklist> reviewWorks = null;
		reviewWorks = workList.getReviewWorkLists();
		if (reviewWorks != null && reviewWorks.size() > 0) {
			for (ReviewWorklist reviewWork : reviewWorks) {
				if (reviewWork.getStatus().equalsIgnoreCase(ChartStatus.CoderAssigned.getStatus())) {
					reviewWork.setStatus(ChartStatus.CodingInProgress.getStatus());
					workList.setStatus(ChartStatus.CodingInProgress.getStatus());
					reviewWork.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
					// reviewWork.setPreviousStatus(ChartStatus.CoderAssigned.getStatus());
				} else if (reviewWork.getStatus().equalsIgnoreCase(ChartStatus.LocalQAAssigned.getStatus())) {
					reviewWork.setStatus(ChartStatus.LocalQAInProgress.getStatus());
					workList.setStatus(ChartStatus.LocalQAInProgress.getStatus());
					reviewWork.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
					// reviewWork.setPreviousStatus(ChartStatus.QAAssigned.getStatus());
				} else if (reviewWork.getStatus().equalsIgnoreCase(ChartStatus.GlobalQAAssigned.getStatus())) {
					reviewWork.setStatus(ChartStatus.GlobalQAInProgress.getStatus());
					workList.setStatus(ChartStatus.GlobalQAInProgress.getStatus());
					reviewWork.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
					// reviewWork.setPreviousStatus(ChartStatus.RemoteQAAssigned.getStatus());
				}
			}
		}
		workList.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
		return workListDao.updateWorkListItems(workList);
	}

	public void updateStatusToAssigned(WorkListItem workList, ReviewWorklist reviewWork, String workType) {
		try {
			if (workType.equalsIgnoreCase("coding")) {
				if ((reviewWork.getStatus()).equalsIgnoreCase(ChartStatus.CodingInProgress.getStatus())) {
					reviewWork.setStatus(ChartStatus.CoderAssigned.getStatus());
					workList.setStatus(ChartStatus.CoderAssigned.getStatus());
				} else if (reviewWork.getStatus().equalsIgnoreCase(ChartStatus.LocalQAInProgress.getStatus())) {
					reviewWork.setStatus(ChartStatus.LocalQAAssigned.getStatus());
					workList.setStatus(ChartStatus.LocalQAAssigned.getStatus());
				} else if (reviewWork.getStatus().equalsIgnoreCase(ChartStatus.GlobalQAInProgress.getStatus())) {
					reviewWork.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
					workList.setStatus(ChartStatus.GlobalQAAssigned.getStatus());

				}
			}
			// else if(workType.equalsIgnoreCase("globalAudit")){
			// reviewWork.setStatus(ChartStatus.Completed.getStatus());
			// workList.setStatus(ChartStatus.Completed.getStatus());
			// reviewWork.setUpdatedDate(new
			// java.sql.Timestamp(System.currentTimeMillis()));
			// }
			reviewWork.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
			workList.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
			workListDao.update(workList);
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		}
	}

	public List<CodersQADto> getAllCodersORLocalQAByClientChartTypeAndChartSpl(final String clientName,
			final String chartType, final String chartSpl, final String status) {
		final List<CodersQADto> codersList = new ArrayList<CodersQADto>();
		if (ChartStatus.NotStarted.getStatus().equals(status) || ChartStatus.LocalCNR.getStatus().equals(status)) {
			final ChartSpecilization chartSplization = chartSplDao.getChartSpecilizationByChartTypeAndSpecialization(
					chartType, chartSpl);
			if (chartSplization != null) {
				final Integer chartSpecId = chartSplization.getId();
				final ClientDetails client = clientDetailDao.getClientByName(clientName);
				if (client != null) {
					final List<Integer> userIds = getUserIdsByClientAndChartSpl(client.getId(), chartSpecId);
					final List<Coders> coders = getCoderDetailssByUserIds(userIds);
					if (coders != null && coders.size() > 0) {
						if (ChartStatus.NotStarted.getStatus().equals(status)) {
							for (Coders coder : coders) {
								if (coder.getIsAvailable() && coder.getCoder())
									codersList.add(new CodersQADto(coder.getUserId(), coder.getFirstname()));
							}
						}
						if (ChartStatus.LocalCNR.getStatus().equals(status)) {
							for (Coders coder : coders) {
								if (coder.getIsAvailable() && coder.getLocalQa())
									codersList.add(new CodersQADto(coder.getUserId(), coder.getFirstname()));
							}
						}
					}
				}

			}
		}
		return codersList;
	}

	private List<Coders> getCoderDetailssByUserIds(final List<Integer> userIds) {
		init();
		final List<Coders> coders = new ArrayList<Coders>();
		for (Map.Entry<Integer, Coders> entry : codersMap.entrySet()) {
			for (Integer id : userIds) {
				if (entry.getKey().equals(id))
					coders.add(entry.getValue());
			}
		}
		return coders;
	}

}
