package com.eclat.mcws.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.AuditParamEntity;
import com.eclat.mcws.dto.ChartDetail;
import com.eclat.mcws.dto.CoderOrQaDetail;
import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.DashboardBean;
import com.eclat.mcws.dto.NameValuePair;
import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartAuditStatus;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.dao.AuditLogDao;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.SupervisorDao;
import com.eclat.mcws.persistence.dao.UserDAO;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.AuditLog;
import com.eclat.mcws.persistence.entity.AuditLogDetails;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.persistence.entity.UsersClient;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.utility.ClientwiseReportUtility;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.DecimalFormatUtils;

@Service
public class SupervisorServiceImpl implements SupervisorService {

	private static final int TWENTY_THIRD = 23;
	private static final int FIFTY_NINE = 59;
	private static final int ZERO = 0;
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int ONE_HUNDRED = 100;
	private List<Integer> rQAIds = null;

	@Log
	private Logger logger;

	@Autowired
	private CoderDao coderDao;

	@Autowired
	private UserDAO userDao;

	@Autowired
	private SupervisorDao supervisorDao;

	@Autowired
	private WorkListDao workListDao;

	@Autowired
	private CommonUtilService commonUtilService;

	@Autowired
	private ClientwiseReportUtility clientwiseReportUtility;

	@Autowired
	private ClientDetailsDao clientDetailDao;

	@Autowired
	private AuditLogDao auditLogDao;

	@Autowired
	private AppStartupComponent appStartupComponent;

	Map<Integer, Coders> codersMap;
	Map<Integer, ChartSpecilization> chartSplMap;

	@PostConstruct
	public void init() {
		codersMap = appStartupComponent.getCodersMap();
		chartSplMap = appStartupComponent.getChartSplObjMap();
	}

	// quartz job
	public void executeResetCompletedLoadForAll(String userType) throws Exception {

		supervisorDao.executeResetCompletedLoadForAll(userType);
	}

	public List<CodersQADto> getAllCoderAndQADetails() throws Exception {
		final List<CodersQADto> codersAndQA = new ArrayList<CodersQADto>();
		final List<Users> users = userDao.getAllUsers();
		if (users != null && users.size() > 0) {

			for (Users user : users) {
				CodersQADto cq = new CodersQADto();
				final Coders coder = coderDao.getCoderQaDetailByUserId(user.getId());
				if (coder != null) {
					cq.setId(user.getId());
					cq.setName(user.getFirstname());
					cq.setRole(CommonOperations.findRoleOfUser(coder));
					cq.setLocation(user.getLocation());
					cq.setJobLevel(coder.getJobLevel());
					cq.setActualLoad(findActualWorkLoad(coder));
					cq.setDailyLoad(findDailyWorkLoad(coder));

					if (user.getIsAvailable()) {
						cq.setAvailable("Yes");
					} else {
						cq.setAvailable("No");
					}

					// If coder does not work on any chart on current day
					// Then properties are not setting.
					List<ReviewWorklist> reviewWorkItems = workListDao.getUsersPresentDayWorkItemsById(coder
							.getUserId());
					if (reviewWorkItems != null && reviewWorkItems.size() > 0) {
						cq.setCompleteLoad(coder.getCompleteLoad() + "EM");
						cq.setCompletedCharts(getCountOfCompletedCharts(reviewWorkItems));
						cq.setCnrCharts(getCountOfCNRCharts(reviewWorkItems));
						cq.setInCompleteCharts(getCountOfINCompletedCharts(reviewWorkItems));
						cq.setMiscCharts(getCountOfMISCCharts(reviewWorkItems));
						cq.setTotalCharts(getTotalWorklistCountOfCoder(reviewWorkItems));
					}
					codersAndQA.add(cq);
				} else {
					logger.warn("=====> NO Coder data found for userId :: " + user.getId());
				}
			}
		}
		return codersAndQA;
	}

	public CoderOrQaDetail getCoderOrQaDetailByIdAndDate(final int userId, final String fromDate, final String toDate)
			throws Exception {
		init();

		final CoderOrQaDetail coderqaDetail = new CoderOrQaDetail();
		final Users user = userDao.getUserById(userId);
		if (user != null) {
			coderqaDetail.setUserId(user.getId());
			coderqaDetail.setUserName(user.getFirstname());
			final Coders coderQA = codersMap.get(userId);
			if (coderQA != null) {
				coderqaDetail.setRole(CommonOperations.findRoleOfUser(coderQA));
				coderqaDetail.setExperience(coderQA.getJobLevel());
			} else {
				logger.warn(" No Coder details found for userId :: " + userId);
			}
			final List<UsersClient> userClients = new ArrayList<UsersClient>(coderQA.getUserClients());
			if (userClients != null && userClients.size() > 0) {
				final String clientName = getPrimaryClientOfCoderQA(userClients);
				coderqaDetail.setPrimaryClient(clientName);
				final Set<UserCharts> userCharts = coderQA.getUserCharts();
				if (userCharts != null && userCharts.size() > 0) {
					final String primaryChart = getPrimaryChartOfCoderQA(new ArrayList(userCharts));
					coderqaDetail.setPrimaryChart(primaryChart);
				} else {
					logger.warn(" NO Chart Details Found for User :: " + userId);
				}
			} else {
				logger.warn(" NO Client Details Found for User :: " + userId);
			}

			final Timestamp fromDateTime = DateUtil.convertStringValueToDateFormat(fromDate);
			final Timestamp startDate = DateUtil.constructFromDateValue(fromDateTime.getTime());
			final Timestamp endDateTime = DateUtil.convertStringValueToDateFormat(toDate);
			final Timestamp endDate = DateUtil.constructEndDateValue(endDateTime.getTime());
			final List<ChartDetail> chartDetails = constructChartDetailsInfo(user, startDate, endDate);
			coderqaDetail.setChartDetails(chartDetails);
		}
		return coderqaDetail;
	}

	private List<ChartDetail> constructChartDetailsInfo(final Users user, final Timestamp startDate,
			final Timestamp endDate) {

		init();
		final List<ChartDetail> chartDetails = new ArrayList<ChartDetail>();
		final Coders coder = codersMap.get(user.getId());
		List<ReviewWorklist> reviewWorklistItems = workListDao.getReviewWorklistsByUserIdAndDateRange(
				coder.getUserId(), startDate, endDate);
		if (reviewWorklistItems != null && reviewWorklistItems.size() > 0) {
			for (ReviewWorklist reviewWorklist : reviewWorklistItems) {
				WorkListItem workList = reviewWorklist.getWorkListItem();
				ChartDetail cd = new ChartDetail();
				logger.info(" CLIENT DETAIL :: " + workList.getClientDetails());
				cd.setClient(workList.getClientDetails().getName());
				if (null != workList.getAccountNumber())
					cd.setAccount(workList.getAccountNumber());
				else
					cd.setAccount(workList.getMrNumber());
				if (workList.getAuditDate() != null) {
					cd.setAdmitDate(DateUtil.df.format(workList.getAuditDate()));
				} else {
					cd.setAdmitDate(null);
				}
				if (workList.getDischargedDate() != null) {
					cd.setDischargeDate(DateUtil.df.format(workList.getDischargedDate()));
				} else {
					cd.setDischargeDate(null);
				}
				cd.setStatus(reviewWorklist.getStatus());
				cd.setTotalTime(calculateChartCodingTime(reviewWorklist));
				cd.setLos(workList.getLos());
				cd.setTat(workList.getTat());
				cd.setReceivedDate(DateUtil.df.format(workList.getUploadedDate()));
				cd.setUpdateDate(DateUtil.df.format(new Date(workList.getUpdatedDate().getTime())));
				if (workList.getChartSpl() != null) {
					try {
						final int chartSpecId = Integer.parseInt(workList.getChartSpl());
						final ChartSpecilization chartSpec = chartSplMap.get(chartSpecId);
						cd.setChartType(chartSpec.getChartType());
						cd.setChartSpecialization(chartSpec.getChartSpelization());
					} catch (final Exception e) {
						logger.warn("=====> Wrong value Added for Chart specialization on worklist table:: "
								+ workList.getChartSpl());
					}
				}
				chartDetails.add(cd);
			}
		} else {
			logger.warn("=====> NO Work List Items found for coder/QA userId:: " + coder.getUserId());
		}
		return chartDetails;
	}

	public List<TaskDetails> getAllTaskDetails(final String date) throws Exception {
		init();
		final List<TaskDetails> taskDetails = new ArrayList<TaskDetails>();

		final List<WorkListItem> workListItems = getWorkListItemsBySelectedDate(date);
		if (workListItems != null && workListItems.size() > 0) {
			for (WorkListItem workListItem : workListItems) {
				final TaskDetails taskDetail = new TaskDetails();
				taskDetail.setId(workListItem.getId());
				taskDetail.setAccountNumber(workListItem.getAccountNumber());
				taskDetail.setMrNumber(workListItem.getMrNumber());
				taskDetail.setEm(new Double(workListItem.getEffortMetric()).toString() + "EM");
				taskDetail.setTat(workListItem.getTat());
				taskDetail.setLos(workListItem.getLos());
				if (workListItem.getStatus() != null)
					taskDetail.setStatus(workListItem.getStatus());
				else
					taskDetail.setStatus(ChartStatus.NotStarted.getStatus());

				taskDetail.setReceivedDate(DateUtil.df.format(workListItem.getUploadedDate()));
				taskDetail.setUpdateDate(DateUtil.df.format(new Date(workListItem.getUpdatedDate().getTime())));
				taskDetail.setSpanClass(ChartStatus.getStatusSpanClass(taskDetail.getStatus()));

				if (workListItem.getClientDetails() != null) {
					taskDetail.setClientId(workListItem.getClientDetails().getId());
					taskDetail.setClient(workListItem.getClientDetails().getName());
				}

				if (workListItem.getChartSpl() != null) {
					final ChartSpecilization chartSpec = chartSplMap.get(Integer.parseInt(workListItem.getChartSpl()));
					taskDetail.setChartSpecId(chartSpec.getId());
					taskDetail.setSpecilization(chartSpec.getChartSpelization());
					taskDetail.setChartType(chartSpec.getChartType());
				}

				final Set<ReviewWorklist> reviewWorkLists = workListItem.getReviewWorkLists();
				if (reviewWorkLists != null && reviewWorkLists.size() > 0) {

					for (ReviewWorklist reviewWorkList : reviewWorkLists) {
						final int coderId = reviewWorkList.getUserReviewd();
						final Coders coderqa = codersMap.get(coderId);
						final String reviewedUserRole = reviewWorkList.getUserRole();
						addCoderInfoToTaskDetail(coderqa, reviewedUserRole, taskDetail);
						taskDetail.setReviewWorkListId(reviewWorkList.getId());
					}
				}
				// setting the remaining tat to taskDetail
				Long millis = System.currentTimeMillis() - workListItem.getUploadedDate().getTime();
				taskDetail.setRemainingTat(DateUtil.convertMinsToHHMM(workListItem.getTat() * 60
						- DateUtil.convertMillsToMins(millis)));
				taskDetails.add(taskDetail);
			}
		}
		return taskDetails;
	}

	private void addCoderInfoToTaskDetail(final Coders coder, final String reviewedUserRole, TaskDetails taskDetail) {
		if (coder != null && reviewedUserRole != null) {
			if (reviewedUserRole.equals(UserRoles.Coder.toString()) && coder.getCoder()) {
				taskDetail.setCoderName(coder.getFirstname());
				taskDetail.setCoderId(coder.getUserId());
			} else if (reviewedUserRole.equals(UserRoles.LocalQA.toString()) && coder.getLocalQa()) {
				taskDetail.setLocalQAId(coder.getUserId());
				taskDetail.setLocalQAName(coder.getFirstname());
			} else if (reviewedUserRole.equals(UserRoles.RemoteQA.toString()) && (coder.getRemoteQa())) {
				taskDetail.setRemoteQAId(coder.getUserId());
				taskDetail.setRemoteQAName(coder.getFirstname());
			}
		}
	}

	private List<WorkListItem> getWorkListItemsBySelectedDate(final String date) {
		final Timestamp startDate = new Timestamp(DateUtil.convertStringValueToDate(date).getTime());
		final Timestamp endDate = DateUtil.constructEndDateValue(null);
		return workListDao.getAllNotCompletedWorkListItemsByDateRange(startDate, endDate);
	}

	private String calculateChartCodingTime(final ReviewWorklist reviewWorklist) {
		return DateUtil.convertMinsToHHMM(reviewWorklist.getTotalTimeTaken());
	}

	private String getPrimaryClientOfCoderQA(final List<UsersClient> userClients) {
		final int clientId = getPrimaryClientIdOfCoderQA(userClients);
		final ClientDetails clientDetail = clientDetailDao.getClientDetailById(clientId);
		if (clientDetail != null) {
			return clientDetail.getName();
		} else {
			logger.error("====> Client Detail does not exits for client ID:: " + clientId);
		}
		return null;
	}

	private int getPrimaryClientIdOfCoderQA(final List<UsersClient> userClients) {
		final int primaryClientId = 0;
		for (UsersClient userClient : userClients) {
			if (userClient.getPriority() == 1) {
				return userClient.getClientId();
			}
		}
		return primaryClientId;
	}

	private String getPrimaryChartOfCoderQA(final List<UserCharts> userCharts) {
		init();
		String primaryChart = null;
		final int chartSpecId = getChartSpecIdOfCoderQAPrimaryChart(userCharts);

		final ChartSpecilization chartSpec = chartSplMap.get(chartSpecId);

		if (chartSpec != null) {
			primaryChart = chartSpec.getChartType();
		} else {
			logger.warn(" NO Chart Specialization Details Found for Id :: " + chartSpecId);
		}

		return primaryChart;
	}

	private int getChartSpecIdOfCoderQAPrimaryChart(final List<UserCharts> userCharts) {

		final int primaryChartSpecId = 0;
		for (UserCharts userChart : userCharts) {
			if (userChart.getPriority() == 1) {
				return userChart.getChartSpecializationId();
			}
		}
		return primaryChartSpecId;
	}

	private int getTotalWorklistCountOfCoder(List<ReviewWorklist> reviewedItems) {
		return getCountOfCompletedCharts(reviewedItems) + getCountOfINCompletedCharts(reviewedItems)
				+ getCountOfCNRCharts(reviewedItems) + getCountOfMISCCharts(reviewedItems);
	}

	private int getCountOfCompletedCharts(List<ReviewWorklist> reviewedItems) {
		int completeChartCount = 0;
		for (ReviewWorklist reviewedItem : reviewedItems) {
			if (reviewedItem.getStatus() != null && reviewedItem.getStatus().equals(ChartStatus.Completed.getStatus())) {
				completeChartCount++;
			}
		}
		return completeChartCount;
	}

	private int getCountOfINCompletedCharts(List<ReviewWorklist> reviewedItems) {
		int inCompleteChartCount = 0;
		for (ReviewWorklist reviewedItem : reviewedItems) {
			if (reviewedItem.getStatus() != null && reviewedItem.getStatus().equals(ChartStatus.InComplete.getStatus())) {
				inCompleteChartCount++;
			}
		}
		return inCompleteChartCount;
	}

	private int getCountOfCNRCharts(List<ReviewWorklist> reviewedItems) {

		int cnrChartCount = 0;
		for (ReviewWorklist reviewedItem : reviewedItems) {
			if (reviewedItem.getStatus() != null
					&& (reviewedItem.getStatus().equals(ChartStatus.LocalCNR.getStatus()) || reviewedItem.getStatus()
							.equals(ChartStatus.GlobalCNR.getStatus()))) {
				cnrChartCount++;
			}
		}
		return cnrChartCount;
	}

	private int getCountOfMISCCharts(List<ReviewWorklist> reviewedItems) {
		int miscChartCount = 0;
		for (ReviewWorklist reviewedItem : reviewedItems) {
			if (reviewedItem.getStatus() != null && reviewedItem.getStatus().equals(ChartStatus.MISC.getStatus())) {
				miscChartCount++;
			}
		}
		return miscChartCount;
	}

	private String findActualWorkLoad(final Coders coder) {
		if (coder != null) {
			return coder.getCoderMaxWorkLoad() + "EM";
		}
		return null;
	}

	private String findDailyWorkLoad(final Coders coder) {
		if (coder != null) {
			return coder.getCoderDailyWorkLoad() + "EM";
		}
		return null;
	}

	public boolean updateWorkListItem(final long worklistId, final int coderId, final int localQAId,
			final int remoteQAId, final double effortMetric, final String status) {
		try {
			final WorkListItem workList = workListDao.find(worklistId);
			Set<ReviewWorklist> reviewWorks = null;
			if (workList != null) {
				if (effortMetric > 0.0) {
					workList.setEffortMetric(effortMetric);
					workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
				}

				// If the task EM was not calculated when running setup coders
				// daily workload,
				// Adding that chart EM to coder's(assignee) daily workload.
				List<Coders> coders = null;
				if (!workList.getIsUsedForAutoEMProcess() && coderId > 0) {
					coders = getCodersTOUpdateDailyWorkload(workList, coderId);
				}

				// Adding Assigned status instead of CodingInProgress during
				// Manual task assignment/coder task fetch
				reviewWorks = constructReviewWorkListItems(coderId, localQAId, remoteQAId, workList, status);

				workList.setReviewWorkLists(reviewWorks);

				logger.info(" reviewWorks SIZE :: " + reviewWorks.size());

				return workListDao.updateManualAssignedWorkitem(workList, coders);
			} else {
				logger.warn("===> No Records Found for work list id : " + worklistId);
			}
		} catch (final Exception e) {
			logger.error(" Exception on Manual Assignment updateWorkListItem", e);
		}

		return false;
	}

	private List<Coders> getCodersTOUpdateDailyWorkload(final WorkListItem worklist, final Integer assigneeId) {
		final List<Coders> coders = new ArrayList<>();
		final String status = worklist.getStatus();

		if (status == null || status.equals(ChartStatus.NotStarted.getStatus())) {
			final Coders assignee = coderDao.find(assigneeId);
			if (assignee != null) {
				assignee.setCoderDailyWorkLoad(assignee.getCoderDailyWorkLoad() + worklist.getEffortMetric());
				coders.add(assignee);
			}

		} else if (status.equals(ChartStatus.CoderAssigned.getStatus())
				|| status.equals(ChartStatus.CodingInProgress.getStatus())
				|| status.equals(ChartStatus.InComplete.getStatus())) {

			final Coders assigned = coderDao.find(worklist.getCoderId());
			final Double dailyLoad = assigned.getCoderDailyWorkLoad() - worklist.getEffortMetric();
			if (dailyLoad < ZERO)
				assigned.setCoderDailyWorkLoad(0.00);
			else
				assigned.setCoderDailyWorkLoad(dailyLoad);

			coders.add(assigned);

			final Coders assignee = coderDao.find(assigneeId);
			assignee.setCoderDailyWorkLoad(assignee.getCoderDailyWorkLoad() + worklist.getEffortMetric());
			coders.add(assignee);
		}

		return coders;

	}

	private Set<ReviewWorklist> constructReviewWorkListItems(final int coderId, final int localQAId,
			final int remoteQAId, final WorkListItem workList, final String status) {
		init();
		boolean coderUpdated = false;
		boolean localQAUpdated = false;
		boolean remoteQAUpdated = false;
		final Set<ReviewWorklist> reviewWorklistItems = new HashSet<ReviewWorklist>();
		ReviewWorklist reviewWorklistItem = null;
		final Set<ReviewWorklist> reviewWorklists = workList.getReviewWorkLists();

		if (coderId > 0) {
			final ReviewWorklist reviewWorklistObj = workList.getReviewWorkListsByUser(coderId);

			if (reviewWorklistObj != null) {
				// Do not do anything
			} else {
				if (reviewWorklists != null && reviewWorklists.size() > 0) {
					for (ReviewWorklist reviewWorkItem : reviewWorklists) {
						Coders reviewCoder = reviewWorkItem.getCoders();
						if (reviewCoder.getCoder() && reviewCoder.getUserId() != coderId) {
							if (reviewWorkItem.getComment() != null) {
								reviewWorklistItem = new ReviewWorklist();

								// workList.setStatus(ChartStatus.CodingInProgress.getStatus());
								// Adding Assigned status instead of
								// CodingInProgress during Manual task
								// assignment/coder task fetch
								workList.setStatus(ChartStatus.CoderAssigned.getStatus());
								workList.setCoderId(coderId);
								workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setWorkListItem(workList);
								reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUserReviewd(coderId);
								reviewWorklistItem.setUserRole(CommonOperations.getUserReviewedRole(
										codersMap.get(coderId), true));
								reviewWorklistItem.setStatus(ChartStatus.CoderAssigned.getStatus());
								if (ChartStatus.LocalQAAssigned.getStatus().equalsIgnoreCase(status))
									reviewWorklistItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
								else
									reviewWorklistItem.setPreviousStatus(status);
								reviewWorklistItem.setTotalTimeTaken(0);
								reviewWorklistItem.setIsTempData(false);
								coderUpdated = true;
							} else {
								workList.setCoderId(coderId);
								workList.setStatus(ChartStatus.CoderAssigned.getStatus());
								workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								if (ChartStatus.LocalQAAssigned.getStatus().equalsIgnoreCase(status))
									reviewWorkItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
								else
									reviewWorkItem.setPreviousStatus(status);
								final Boolean istempData = reviewWorkItem.getIsTempData();
								if (istempData == null) {
									reviewWorkItem.setIsTempData(false);
								}
								reviewWorkItem.setWorkListItem(workList);
								reviewWorkItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorkItem.setUserReviewd(coderId);
								reviewWorkItem.setUserRole(CommonOperations.getUserReviewedRole(codersMap.get(coderId),
										true));
								reviewWorkItem.setTotalTimeTaken(0);

								reviewWorklistItem = reviewWorkItem;
								coderUpdated = true;
							}

						}
					}
				}
				if (!coderUpdated) {
					reviewWorklistItem = new ReviewWorklist();
					workList.setStatus(ChartStatus.CoderAssigned.getStatus());
					workList.setCoderId(coderId);
					workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setWorkListItem(workList);
					reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUserReviewd(coderId);
					reviewWorklistItem.setUserRole(CommonOperations.getUserReviewedRole(codersMap.get(coderId), true));
					reviewWorklistItem.setStatus(ChartStatus.CoderAssigned.getStatus());
					reviewWorklistItem.setPreviousStatus(status);
					reviewWorklistItem.setTotalTimeTaken(0);
					reviewWorklistItem.setIsTempData(false);
				}

				reviewWorklistItems.add(reviewWorklistItem);
			}
		}

		if (localQAId > 0) {
			final ReviewWorklist reviewWorklistObj = workList.getReviewWorkListsByUser(localQAId);
			if (reviewWorklistObj != null) {
				// Do Not Do Anything
			} else {
				if (reviewWorklists != null && reviewWorklists.size() > 0) {
					for (ReviewWorklist reviewWorkItem : reviewWorklists) {
						Coders reviewCoder = reviewWorkItem.getCoders();
						if (reviewCoder.getLocalQa()) {
							if (reviewWorkItem.getComment() != null) {
								workList.setStatus(ChartStatus.LocalQAAssigned.getStatus());
								workList.setCoderId(localQAId);
								workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem = new ReviewWorklist();
								reviewWorklistItem.setWorkListItem(workList);
								reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUserReviewd(localQAId);
								reviewWorklistItem.setUserRole(CommonOperations.getUserReviewedRole(
										codersMap.get(localQAId), false));
								reviewWorklistItem.setStatus(ChartStatus.LocalQAAssigned.getStatus());
								if (ChartStatus.LocalQAAssigned.getStatus().equalsIgnoreCase(status))
									reviewWorklistItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
								else
									reviewWorklistItem.setPreviousStatus(status);
								reviewWorklistItem.setTotalTimeTaken(0);
								reviewWorklistItem.setIsTempData(false);
								localQAUpdated = true;
							} else {
								final String userRole = reviewWorkItem.getUserRole();
								if (reviewCoder.getUserId() != localQAId
										&& userRole.equals(UserRoles.LocalQA.toString())) {
									workList.setStatus(ChartStatus.LocalQAAssigned.getStatus());
									workList.setCoderId(localQAId);
									workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
									reviewWorkItem.setWorkListItem(workList);
									reviewWorkItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
									reviewWorkItem.setUserReviewd(localQAId);
									reviewWorkItem.setUserRole(CommonOperations.getUserReviewedRole(
											codersMap.get(localQAId), false));
									reviewWorkItem.setStatus(ChartStatus.LocalQAAssigned.getStatus());
									if (ChartStatus.LocalQAAssigned.getStatus().equalsIgnoreCase(status))
										reviewWorkItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
									else
										reviewWorkItem.setPreviousStatus(status);
									final Boolean istempData = reviewWorkItem.getIsTempData();
									if (istempData == null) {
										reviewWorkItem.setIsTempData(false);
									}
									reviewWorkItem.setTotalTimeTaken(0);
									reviewWorklistItem = reviewWorkItem;
									localQAUpdated = true;
								}
							}
						}
					}
				}
				if (!localQAUpdated) {
					workList.setStatus(ChartStatus.LocalQAAssigned.getStatus());
					workList.setCoderId(localQAId);
					workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem = new ReviewWorklist();
					reviewWorklistItem.setWorkListItem(workList);
					reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUserReviewd(localQAId);
					reviewWorklistItem
							.setUserRole(CommonOperations.getUserReviewedRole(codersMap.get(localQAId), false));
					reviewWorklistItem.setStatus(ChartStatus.LocalQAAssigned.getStatus());
					reviewWorklistItem.setPreviousStatus(status);
					reviewWorklistItem.setTotalTimeTaken(0);
					reviewWorklistItem.setIsTempData(false);
				}
				reviewWorklistItems.add(reviewWorklistItem);
			}
		}

		if (remoteQAId > 0) {
			final ReviewWorklist reviewWorklistObj = workList.getReviewWorkListsByUser(remoteQAId);
			if (reviewWorklistObj != null) {
				// Do not Do Anything
			} else {
				if (reviewWorklists != null && reviewWorklists.size() > 0) {
					for (ReviewWorklist reviewWorkItem : reviewWorklists) {
						Coders reviewCoder = reviewWorkItem.getCoders();
						if (reviewCoder.getRemoteQa()) {
							if (reviewWorkItem.getComment() != null) {
								workList.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
								workList.setCoderId(remoteQAId);
								workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem = new ReviewWorklist();
								reviewWorklistItem.setWorkListItem(workList);
								reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
								reviewWorklistItem.setUserReviewd(remoteQAId);
								reviewWorklistItem.setUserRole(CommonOperations.getUserReviewedRole(
										codersMap.get(remoteQAId), false));
								reviewWorklistItem.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
								if (ChartStatus.GlobalQAAssigned.getStatus().equalsIgnoreCase(status))
									reviewWorklistItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
								else
									reviewWorklistItem.setPreviousStatus(status);
								reviewWorklistItem.setTotalTimeTaken(0);
								reviewWorklistItem.setIsTempData(false);
								remoteQAUpdated = true;
							} else {
								final String userRole = reviewWorkItem.getUserRole();
								if (reviewCoder.getUserId() != remoteQAId
										&& userRole.equals(UserRoles.RemoteQA.toString())) {
									workList.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
									workList.setCoderId(remoteQAId);
									workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
									reviewWorkItem.setWorkListItem(workList);
									reviewWorkItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
									reviewWorkItem.setUserReviewd(remoteQAId);
									reviewWorkItem.setUserRole(CommonOperations.getUserReviewedRole(
											codersMap.get(remoteQAId), false));
									// Added RemoteQAAssigned to review_work
									reviewWorkItem.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
									if (ChartStatus.GlobalQAAssigned.getStatus().equalsIgnoreCase(status))
										reviewWorkItem.setPreviousStatus(reviewWorkItem.getPreviousStatus());
									else
										reviewWorkItem.setPreviousStatus(status);
									final Boolean istempData = reviewWorkItem.getIsTempData();
									if (istempData == null) {
										reviewWorkItem.setIsTempData(false);
									}
									reviewWorkItem.setTotalTimeTaken(0);
									reviewWorklistItem = reviewWorkItem;
									remoteQAUpdated = true;
								}
							}

						}
					}
				}
				if (!remoteQAUpdated) {
					workList.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
					workList.setCoderId(remoteQAId);
					workList.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem = new ReviewWorklist();
					reviewWorklistItem.setWorkListItem(workList);
					reviewWorklistItem.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
					reviewWorklistItem.setUserReviewd(remoteQAId);
					reviewWorklistItem.setUserRole(CommonOperations.getUserReviewedRole(codersMap.get(remoteQAId),
							false));
					reviewWorklistItem.setStatus(ChartStatus.GlobalQAAssigned.getStatus());
					reviewWorklistItem.setPreviousStatus(status);
					reviewWorklistItem.setTotalTimeTaken(0);
					reviewWorklistItem.setIsTempData(false);
				}

				reviewWorklistItems.add(reviewWorklistItem);
			}
		}
		return reviewWorklistItems;
	}

	public JSONArray getSupervisorReportsData(final String startDate, final String toDate) throws Exception {
		// Modified End date value to that day night time.
		final String endDate = toDate + " 23:59:59";
		final List<Object[]> entities = workListDao.getWorklistDataForSupervisorReport(startDate, endDate);
		final JSONArray jsonArray = new JSONArray();
		for (Object[] entity : entities) {
			constructJsonArray(entity, jsonArray);
		}
		return jsonArray;
	}

	private JSONArray constructJsonArray(Object[] entity, JSONArray jsonArray) {
		JSONObject jsonObj = new JSONObject();
		final Map<String, Object> values = new HashMap<String, Object>();
		int count = 0;
		for (int i = 0; i < entity.length; i++) {
			if (i == 0) {
				values.put("client", entity[0]);
			}
			if (i == 1) {
				values.put("chartType", entity[1]);
			}
			if (i == 2) {
				values.put("inComplete", entity[2]);
				count = count + ((BigInteger) entity[2]).intValue();
			}
			if (i == 3) {
				values.put("completed", entity[3]);
				count = count + ((BigInteger) entity[3]).intValue();
			}
			if (i == 4) {
				values.put("misc", entity[4]);
				count = count + ((BigInteger) entity[4]).intValue();
			}
			if (i == 5) {
				values.put("CoderAssigned", entity[5]);
				count = count + ((BigInteger) entity[5]).intValue();
			}
			if (i == 6) {
				values.put("LocalQAAssigned", entity[6]);
				count = count + ((BigInteger) entity[6]).intValue();
			}
			if (i == 7) {
				values.put("GlobalQAAssigned", entity[7]);
				count = count + ((BigInteger) entity[7]).intValue();
			}
			if (i == 8) {
				values.put("CodingInProgress", entity[8]);
				count = count + ((BigInteger) entity[8]).intValue();
			}
			if (i == 9) {
				values.put("LocalQAInProgress", entity[9]);
				count = count + ((BigInteger) entity[9]).intValue();
			}
			if (i == 10) {
				values.put("GlobalQAInProgress", entity[10]);
				count = count + ((BigInteger) entity[10]).intValue();
			}
			if (i == 11) {
				values.put("LocalCNR", entity[11]);
				count = count + ((BigInteger) entity[11]).intValue();
			}
			if (i == 12) {
				values.put("GlobalCNR", entity[12]);
				count = count + ((BigInteger) entity[12]).intValue();
			}
			if (i == 13) {
				values.put("LocalAudit", entity[13]);
				count = count + ((BigInteger) entity[13]).intValue();
			}
			if (i == 14) {
				values.put("GlobalAudit", entity[14]);
				count = count + ((BigInteger) entity[14]).intValue();
			}
			if (i == 15) {
				values.put("Audited", entity[15]);
				count = count + ((BigInteger) entity[15]).intValue();
			}
			if (i == 16) {
				values.put("open", entity[16]);
				count = count + ((BigInteger) entity[16]).intValue();
			}
		}
		values.put("count", count);
		jsonObj.putAll(values);
		jsonArray.add(jsonObj);
		return jsonArray;
	}

	private Boolean filterByRemoteQA(WorkListItem item) {

		if (rQAIds == null) {
			rQAIds = coderDao.getAllRemoteQAIds();
		}

		for (Integer qaID : rQAIds) {
			if (qaID.equals(item.getCoderId()))
				return false;
		}
		return true;
	}

	public boolean createAuditData(final AuditParamEntity auditParam, String message) throws Exception {
		final Timestamp fromDate = convertStringValueToDateFormat(auditParam.getFromDate());
		final Timestamp toDate = convertStringValueToDateFormat(auditParam.getToDate());

		List<WorkListItem> worklistItems = workListDao.getCompletedWorklistToAuditByUpdateDateRange(fromDate, toDate);

		Map<ClientDetails, List<WorkListItem>> temp = new HashMap<ClientDetails, List<WorkListItem>>();

		for (WorkListItem workListItem : worklistItems) {
			List<WorkListItem> list = temp.get(workListItem.getClientDetails());
			if (list == null) {
				list = new ArrayList<WorkListItem>();
				temp.put(workListItem.getClientDetails(), list);
			}

			if (filterByRemoteQA(workListItem))
				list.add(workListItem);
		}

		final Set<AuditLogDetails> auditLogDetails = new HashSet<AuditLogDetails>();
		constructAuditLogDetails(temp, auditLogDetails);
		if (auditLogDetails.size() > 0) {
			return saveAuditLogData(auditLogDetails, auditParam);
		} else {
			message = "No work item found to audit !!!";
			return false;
		}
	}

	/**
	 * 
	 * @param temp
	 * @param auditLogDetails
	 * 
	 *            This method calculate the audit data of each client with
	 *            respect to audit percentage configuration. And add the data
	 *            into auditLogDetails collection.
	 */
	private void constructAuditLogDetails(Map<ClientDetails, List<WorkListItem>> temp,
			Set<AuditLogDetails> auditLogDetails) throws Exception {
		final List<ClientDetails> clientDetails = clientDetailDao.findAll();
		final List<WorkListItem> usersWorkListItems = new ArrayList<WorkListItem>();
		for (ClientDetails clientDetail : clientDetails) {
			for (Map.Entry<ClientDetails, List<WorkListItem>> entry : temp.entrySet()) {
				if (entry.getKey().getId() == clientDetail.getId()) {
					List<WorkListItem> worklistItemsEntry = entry.getValue();
					if (worklistItemsEntry != null) {
						usersWorkListItems.addAll(constructCoderAuditDetails(worklistItemsEntry, clientDetail));
					}
				}
			}
		}
		for (WorkListItem item : usersWorkListItems) {
			AuditLogDetails auditLogDetail = new AuditLogDetails();

			auditLogDetail.setWorklistId(item.getId());
			auditLogDetail.setStatus(ChartAuditStatus.OPEN.toString());
			auditLogDetail.setAuditClientId(item.getClientDetails().getId());
			auditLogDetail.setChartSpeId(Integer.parseInt(item.getChartSpl()));
			auditLogDetail.setAuditCoderId(item.getCoderId());

			auditLogDetails.add(auditLogDetail);
		}
		logger.info(" --> Total work items loaded for AuditLogDetails  : " + auditLogDetails.size());
	}

	private List<WorkListItem> constructCoderAuditDetails(List<WorkListItem> worklistItems, ClientDetails clientDetails)
			throws Exception {
		List<UsersClient> userClients = clientDetailDao.getUsersClientByClientId(clientDetails.getId());
		if (userClients == null || userClients.isEmpty()) {
			return null;
		}

		final double clientAuditChartCount = clientDetails.getAuditPercentage()
				* ((double) worklistItems.size() / ONE_HUNDRED);

		logger.info(" --> Audit chart count is  : " + clientAuditChartCount + " for client(" + clientDetails.getName()
				+ ") : " + clientDetails.getId());
		final List<WorkListItem> usersWorkListItems = new ArrayList<WorkListItem>();
		for (UsersClient userClient : userClients) {
			List<WorkListItem> list = new ArrayList<>();
			;

			for (WorkListItem item : worklistItems) {
				if (userClient.getCoder().getUserId() == item.getCoderId().intValue()) {
					list.add(item);
				}
			}

			if (list.isEmpty() || userClient.getAuditPercentage() <= ZERO) {
				/*
				 * If there are no work items for this coder, ignore this coder
				 * & continue with next one. If Audit percentage is ZERO for
				 * coder/qa, ignore and continue next one.
				 */
				continue;
			}

			double size = clientAuditChartCount * ((double) userClient.getAuditPercentage() / ONE_HUNDRED);

			if (size <= TWO) {
				final WorkListItem workItem = getWorkitemByInsurancePriority(list);
				/**
				 * The Medicare/Medicaid chart first priority for auditing then
				 * get other charts randomly.
				 */
				if (workItem != null) {
					usersWorkListItems.add(workItem);
					list.remove(workItem);
				} else {
					int index = (int) (Math.random() * (size - 1));
					WorkListItem workListItem = list.get(index);
					usersWorkListItems.add(workListItem);
				}
			} else {
				for (int i = 0; i < size; i++) {
					if (list.size() > 0) {
						final WorkListItem workItem = getWorkitemByInsurancePriority(list);
						/**
						 * The Medicare/Medicaid chart first priority for
						 * auditing then get other charts randomly.
						 */
						if (workItem != null) {
							usersWorkListItems.add(workItem);
							list.remove(workItem);
						} else {
							int index = (int) (Math.random() * (size - 1));
							if (index < list.size()) {
								WorkListItem workListItem = list.get(index);
								usersWorkListItems.add(workListItem);
								list.remove(index);
							} else {
								WorkListItem workListItem = list.get(ZERO);
								usersWorkListItems.add(workListItem);
								list.remove(ZERO);
							}
						}
					}

				}
			}
		}
		logger.debug("--> Details of Audit Data For client : " + clientDetails.getId());
		logger.debug("----> Total Client list = {}; Selected Size = {}; Percentage: {}", new Object[] {
				new Double(worklistItems.size()), new Double(usersWorkListItems.size()),
				new Double(((new Double(usersWorkListItems.size()) / new Double(worklistItems.size())) * 100)) });

		return usersWorkListItems;
	}

	private boolean saveAuditLogData(final Set<AuditLogDetails> auditLogDetails, final AuditParamEntity auditParam)
			throws Exception {
		final AuditLog auditLog = new AuditLog();
		auditLog.setTitle(auditParam.getTitle());
		auditLog.setAuditCriteria(auditParam.toString());
		auditLog.setOwner(auditParam.getUserId());
		auditLog.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		return auditLogDao.saveAuditLogDetails(auditLog, auditLogDetails);
	}

	private Timestamp convertStringValueToDateFormat(final String date) {
		Calendar cal = new GregorianCalendar();
		if (date != null && date.length() > 0) {
			final String[] values = date.split("/");
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(values[1]));
			cal.set(Calendar.MONTH, Integer.parseInt(values[0]) - ONE);
			cal.set(Calendar.YEAR, Integer.parseInt(values[2]));
		} else {
			cal.add(Calendar.DATE, -ONE);
		}

		return new Timestamp(cal.getTimeInMillis());
	}

	private WorkListItem getWorkitemByInsurancePriority(List<WorkListItem> itemList) {
		if (itemList.size() > 0) {
			for (WorkListItem item : itemList) {
				if (item.getInsurance() != null && item.getInsurance().trim().length() > 0) {
					if (item.getInsurance().equals("Medicare") || item.getInsurance().equals("Medicaid")) {
						return item;
					}
				}
			}
		}
		return null;
	}

	@Override
	public DashboardBean getSupervisorDashboardData() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, ZERO);
		cal.set(Calendar.MINUTE, ZERO);
		cal.set(Calendar.SECOND, ONE);
		final Date startDate = new Date(cal.getTimeInMillis());

		cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
		cal.set(Calendar.MINUTE, FIFTY_NINE);
		cal.set(Calendar.SECOND, FIFTY_NINE);
		final Date endDate = new Date(cal.getTimeInMillis());

		double completed = 0.0;
		double pending = 0.0;
		double inProgress = 0.0;
		final List<WorkListItem> worklistItems = workListDao.getAllWorkListItemsRangeByUplodedDate(startDate, endDate);
		if (worklistItems != null && worklistItems.size() > 0) {
			for (WorkListItem item : worklistItems) {
				if (item.getStatus() == null) {
					pending = pending + item.getEffortMetric();
				} else if (item.getStatus().equals(ChartStatus.Completed.getStatus())
						|| item.getStatus().equals(ChartStatus.LocalAudited.getStatus())
						|| item.getStatus().equals(ChartStatus.GlobalAudited.getStatus())
						|| item.getStatus().equals(ChartStatus.LocalAudit.getStatus())
						|| item.getStatus().equals(ChartStatus.GlobalAudit.getStatus())
						|| item.getStatus().equals(ChartStatus.MISC.getStatus())) {
					completed = completed + item.getEffortMetric();
				} else
					inProgress = inProgress + item.getEffortMetric();
			}

			return getDashboardBean(completed, pending, inProgress);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private DashboardBean getDashboardBean(final Double completed, final Double pending, final Double inProgress) {
		final DashboardBean dbBean = new DashboardBean();
		dbBean.setTotalEM(Double.parseDouble(DecimalFormatUtils.dfNum.format((completed + pending + inProgress))));
		BigDecimal result = coderDao.getTotalCodersEMCount();
		dbBean.setTotalCodersEM(result.doubleValue());

		NameValuePair p1 = new NameValuePair();
		p1.setName("Completed");
		p1.setValue(DecimalFormatUtils.dfNum.format(completed));
		NameValuePair p2 = new NameValuePair();
		p2.setName("In Progress");
		p2.setValue(DecimalFormatUtils.dfNum.format(inProgress));
		NameValuePair p3 = new NameValuePair();
		p3.setName("Pending");
		p3.setValue(DecimalFormatUtils.dfNum.format(pending));

		final List<NameValuePair> workValues = new ArrayList<NameValuePair>();
		workValues.add(p1);
		workValues.add(p2);
		workValues.add(p3);

		JSONArray workArray = new JSONArray();
		for (NameValuePair nv : workValues) {
			JSONObject obj = new JSONObject();
			JSONArray statusAray = new JSONArray();
			JSONObject statusObj = new JSONObject();
			statusObj.put("v", nv.getName());
			JSONObject valueObj = new JSONObject();
			valueObj.put("v", Double.parseDouble(nv.getValue()));
			statusAray.add(statusObj);
			statusAray.add(valueObj);
			obj.put("c", statusAray);
			workArray.add(obj);
		}

		dbBean.setWorkArray(workArray);

		NameValuePair cp3 = new NameValuePair();
		cp3.setName("Pending");
		double coderPending = result.doubleValue() - (completed + inProgress);
		cp3.setValue(DecimalFormatUtils.dfNum.format(coderPending));

		final List<NameValuePair> capacityValues = new ArrayList<NameValuePair>();
		capacityValues.add(p1);
		capacityValues.add(p2);
		capacityValues.add(cp3);

		JSONArray capacityArray = new JSONArray();
		for (NameValuePair nv : capacityValues) {
			JSONObject obj = new JSONObject();
			JSONArray statusAray = new JSONArray();
			JSONObject statusObj = new JSONObject();
			statusObj.put("v", nv.getName());
			JSONObject valueObj = new JSONObject();
			valueObj.put("v", Double.parseDouble(nv.getValue()));
			statusAray.add(statusObj);
			statusAray.add(valueObj);
			obj.put("c", statusAray);
			capacityArray.add(obj);
		}
		dbBean.setCapacityArray(capacityArray);
		return dbBean;
	}

	public boolean assignWorkListItemToCoder(final List<Long> taskId, final Integer coderId, final String status) {
		final List<WorkListItem> workListItems = workListDao.getWorkListByIds(taskId);
		Set<ReviewWorklist> reviewWorks = null;
		if (workListItems != null) {
			for (WorkListItem workList : workListItems) {
				// Adding Assigned status instead of CodingInProgress during
				// Manual task assignment.
				if (ChartStatus.NotStarted.getStatus().equals(status))
					reviewWorks = constructReviewWorkListItems(coderId, 0, 0, workList,
							ChartStatus.NotStarted.getStatus());
				else if (ChartStatus.LocalCNR.getStatus().equals(status))
					reviewWorks = constructReviewWorkListItems(0, coderId, 0, workList,
							ChartStatus.LocalCNR.getStatus());

				workList.setReviewWorkLists(reviewWorks);
				workListDao.updateWorkListItems(workList);
			}
			return true;
		} else {
			logger.warn("===> No Records Found for work list ids : " + taskId.toString());
		}
		return false;
	}

	public boolean deleteWorklistItems(final List<Long> ids) {
		return workListDao.deleteWorklistItems(ids);
	}
}
