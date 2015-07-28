package com.eclat.mcws.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.ChartRestQueryDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.ChartTypes;
import com.eclat.mcws.enums.CoderJobLevel;
import com.eclat.mcws.persistence.dao.UserClientChartDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.utility.DateUtil;
import com.eclat.mcws.util.comparator.ChartComparator;
import com.eclat.mcws.util.comparator.ClientComparator;
import com.eclat.mcws.util.comparator.WorkListComparator;
import com.eclat.mcws.util.comparator.WorklistDisDateComparator;
import com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker;

@Component
public class WorkDistributionService {

	private static final int ZERO = 0;

	@Log
	private Logger logger;

	@Autowired
	private WorkListDao workListDao;

	@Autowired
	private UserClientChartDao userClientChartDao;

	@Autowired
	private AppStartupComponent appStartupComponent;

	public WorkListItem getCoderWorkItem(final Coders coder, final ChartRestQueryDetails restQueryDetails,
			final Boolean isUsedForAutoEMProcess, Session parentSession) throws Exception {

		List<UsersClient> clients = new ArrayList<UsersClient>(coder.getUserClients());
		Set<UserCharts> charts = coder.getUserCharts();
		WorkListItem workItem = null;
		// Sort the array based on the list
		Collections.sort(clients, new ClientComparator());

		logger.info("---> Configured Clients for coder(" + coder.getUserId() + ") : " + clients);

		List<String> chartSplList = constructChartSplList(charts);
		for (UsersClient userClient : clients) {
			logger.info("----> Fetching item for User_Client : " + userClient.getClientId());

			List<WorkListItem> items = null;

			// Fetch item from the Worklist table with the Client ID
			if (coder.getLocalQa() && !restQueryDetails.isCoderTask()) {

				items = workListDao.fetchCoderWorkItem(userClient.getClientId(), coder, restQueryDetails.isCoderTask(),
						chartSplList, ChartStatus.LocalCNR.getStatus(), isUsedForAutoEMProcess, parentSession);

				if (items == null || items.size() <= 0) {

					items = workListDao.fetchCoderWorkItem(userClient.getClientId(), coder,
							restQueryDetails.isCoderTask(), chartSplList, ChartStatus.CompletedNR.getStatus(),
							isUsedForAutoEMProcess, parentSession);
				}
			} else {

				items = workListDao.fetchCoderWorkItem(userClient.getClientId(), coder, restQueryDetails.isCoderTask(),
						chartSplList, null, isUsedForAutoEMProcess, parentSession);
			}

			if (items != null) {
				// Implementing the rem TAT for work items
				constructRemTatWorkItems(items);
				workItem = getActiveWorkItem(items, charts, coder);
				if (workItem != null)
					return workItem;
			}

		}

		return workItem;
	}

	private List<Integer> getIPChartSpecilizationIds() {
		final List<Integer> speList = new ArrayList<>();
		final Map<String, Integer> chartSplMap = appStartupComponent.getChartSplMap();
		for (java.util.Map.Entry<String, Integer> entry : chartSplMap.entrySet()) {
			String chartType = getChartType(entry.getKey());
			if (chartType.equals(ChartTypes.IP.getChartType())) {
				speList.add(entry.getValue());
			}
		}
		return speList;
	}

	private String getChartType(String chartSpl) {
		int index = chartSpl.indexOf("-");
		return chartSpl.substring(0, index);
	}

	private WorkListItem getActiveWorkItem(List<WorkListItem> items, Set<UserCharts> charts, final Coders coder) {
		WorkListItem workItem = null;
		if (items != null && items.size() > 0) {

			// Sort the User Charts based on the priority
			List<UserCharts> chartList = new ArrayList<UserCharts>(charts);
			Collections.sort(chartList, new ChartComparator());
			
			final List<WorkListItem> splSortedItems = sortUserWorklistItemsBySpecialization(items, chartList);
			
			
			for(WorkListItem item : splSortedItems) {
				logger.debug(" SPL : " +item.getChartSpl());
			}
			// Sort User Charts based on TAT...
			Collections.sort(splSortedItems, new WorkListComparator());

			logger.info(" *** Total Worklist Item AFTER REM TAT SORT for CODER (" + coder.getUserId() + ") : "
					+ splSortedItems.size() + splSortedItems.get(0).getAccountNumber());

			

			// Fetch the Item From the List using the Charts priority
			workItem = getSortedItem(splSortedItems, chartList, coder);
		}
		return workItem;
	}

	/**
	 * Sorting the workItems based on User Chart Specialization priority.
	 * 
	 * @param items
	 * @param userChartSpl
	 * @return
	 */
	private List<WorkListItem> sortUserWorklistItemsBySpecialization(List<WorkListItem> items,
			List<UserCharts> userChartSpl) {
		List<WorkListItem> splSortedList = null;
		Map<Integer, List<WorkListItem>> map = new HashMap<Integer, List<WorkListItem>>();
		Integer currentSplId = 0;
		if (items.size() > 0) {
			List<WorkListItem> sortedItems = null;
			logger.debug(" ***** START COMPARING Worklist Items and USER CHART SPECIALIZATIONS *****");
			for (UserCharts chart : userChartSpl) {
				
				currentSplId = chart.getChartSpecializationId();
				
				if ( (sortedItems =  map.get(currentSplId) ) == null) {
					sortedItems =  new ArrayList<WorkListItem>();
				}
				
				for (WorkListItem leastItem : items) {
					logger.debug(" -----> WORKLIST_ITEM Speliazation ID : " + new Integer(leastItem.getChartSpl()));
					logger.debug(" =====> Coder Chart specialization ID : " + chart.getChartSpecializationId());
					if (new Integer(leastItem.getChartSpl()).equals(chart.getChartSpecializationId()))
						sortedItems.add(leastItem);
				}
				
				if (map.get(currentSplId) == null) {
					if(sortedItems != null && sortedItems.size() > 0)
						map.put(currentSplId, sortedItems);
				}
			} //Completed map 
			
			boolean listMatch = false;
			if(map != null && map.size() > 0) {
				for (UserCharts chart : userChartSpl) {
					logger.debug(" =====> return specialization ID : " + chart.getChartSpecializationId());
					for(Entry<Integer, List<WorkListItem>> entry : map.entrySet()) {
						if (entry.getKey().equals(chart.getChartSpecializationId())) {
							splSortedList = entry.getValue();
							logger.debug(" =====> found item RETURN ");
							listMatch = true;
							break;
						}
					}
					if(listMatch)
						break;
				}
			}
		}
		return splSortedList;
	}

	private WorkListItem getSortedItem(List<WorkListItem> items, List<UserCharts> chartList,
			final Coders coder) {
		WorkListItem returnItem = null;

		logger.debug(" <-------*** USER CHART SPECIALIZATIONS DISPLAY START ***-------->");
		for (UserCharts chart : chartList) {
			logger.debug("  SPECIALIZATION ID  ::  " + chart.getChartSpecializationId());
		}
		logger.debug(" <------- USER CHART SPECIALIZATIONS DISPLAY COMPLETED ------->  ");

		items = getLeastTATItem(items);

		logger.debug(" ====> Least TAT Items : " + items.size());

		// Sort the charts by Discharge date.
		Collections.sort(items, new WorklistDisDateComparator());
		List<WorkListItem> sortedItems = getLeastDisDateItem(items);

		logger.debug(" ====> Least Discharge Date Items : " + sortedItems.size());

		if (sortedItems.size() > 0) {
			// Check if User top priority Specialization is IP
			// boolean userIpSpecialization = checkUserSpecialIP();
			boolean userProrityHigh = checkCoderPriorityHigh(coder);
			boolean isIPCurrent = checkIPIsCurrent(sortedItems.get(ZERO));

			logger.info(" ##### IS HIGH PRIORITY USER ::  " + userProrityHigh);
			logger.info(" ##### IS CURRENT IP TASK ::  " + isIPCurrent);

			if (userProrityHigh && isIPCurrent) {

				Boolean ranValue = getRandomBoolean();

				logger.info(" ##### RANDOM VALUE ::  " + ranValue);

				if (ranValue) {

					for (int index = 0; index < sortedItems.size(); index++) {

						WorkListItem sortedItem = sortedItems.get(index);

						if (sortedItem.getEffortMetric() > 4.0) {
							returnItem = sortedItem;
							break;
						}
					}

					if (returnItem == null) {
						// If Random number is true and do not have HIGH EM
						// item. Check for medium EM item and return that.
						for (int index = 0; index < sortedItems.size(); index++) {

							WorkListItem sortedItem = sortedItems.get(index);

							if (sortedItem.getEffortMetric() <= 4.0 && sortedItem.getEffortMetric() > 2.0) {
								returnItem = sortedItem;
								break;
							}
						}
					}

				} else {
					for (int index = 0; index < sortedItems.size(); index++) {

						WorkListItem sortedItem = sortedItems.get(index);

						if (sortedItem.getEffortMetric() <= 4.0 && sortedItem.getEffortMetric() > 2.0) {
							returnItem = sortedItem;
							break;
						}
					}
					if (returnItem == null) {
						// If Random number is false and do not have Medium EM
						// item. Check for HIGH EM item and return that.
						for (int index = 0; index < sortedItems.size(); index++) {
							WorkListItem sortedItem = sortedItems.get(index);
							if (sortedItem.getEffortMetric() > 4.0) {
								returnItem = sortedItem;
								break;
							}
						}
					}
				}
			}

			if (returnItem == null) {
				returnItem = sortedItems.get(ZERO);
			}

			return returnItem;
		}
		// If nothing then return null
		return null;
	}

	public boolean getRandomBoolean() {
		Random rnd = new Random();
		return rnd.nextBoolean();
	}

	private Boolean checkCoderPriorityHigh(final Coders coder) {
		if (coder.getJobLevel().equals(CoderJobLevel.High.toString()))
			return true;

		return false;
	}

	private Boolean checkIPIsCurrent(final WorkListItem workItem) {
		final List<Integer> splList = getIPChartSpecilizationIds();
		for (Integer id : splList) {
			if (id.equals(Integer.parseInt(workItem.getChartSpl())))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param items
	 *            Implements the remaining TAT value for each work item.
	 */
	private void constructRemTatWorkItems(List<WorkListItem> items) {
		if (items != null && items.size() > 0) {
			for (WorkListItem item : items) {
				Timestamp uploadTime = getUploadDateUptoHours(item.getUploadedDate());
				Long millis = System.currentTimeMillis() - uploadTime.getTime();
				item.setRemainingTat(item.getTat() * 60 - DateUtil.convertMillsToMins(millis));
			}
		}
	}

	/**
	 * Sort User Worklist items by Least TAT.
	 * Find the least TAT item and compare with other items.
	 * Maintain the least items ASC Order.
	 * 
	 * @param items
	 * @return
	 */
	private List<WorkListItem> getLeastTATItem(List<WorkListItem> items) {
		logger.debug(" *** Constructing Least TAT Item By Using REM TAT ****");
		List<WorkListItem> leastTATItems = new ArrayList<WorkListItem>();
		Integer leastTAT = items.get(ZERO).getRemainingTat();
		logger.debug(" *** CURRENT LEAST TAT VALUE :: " + leastTAT);
		for (WorkListItem worklistItem : items) {
			if (leastTAT.equals(worklistItem.getRemainingTat())) {
				leastTATItems.add(worklistItem);
			}
		}
		return leastTATItems;
	}
	
	/**
	 * Sort the worklist by Least Discharge Date item.
	 * Found the leaset discharge date item and compare with other items.
	 * Maintain the least Discharge date items on ASC order.
	 * @param items
	 * @return
	 */
	private List<WorkListItem> getLeastDisDateItem(List<WorkListItem> items) {
		List<WorkListItem> leastDisDateItems = new ArrayList<WorkListItem>();
		java.util.Date leastDisDate = items.get(ZERO).getDischargedDate();
		for (WorkListItem worklistItem : items) {
			if (leastDisDate.equals(worklistItem.getDischargedDate())) {
				leastDisDateItems.add(worklistItem);
			}
		}
		return leastDisDateItems;
	}

	private List<String> constructChartSplList(Set<UserCharts> charts) {
		List<String> chartSplList = new ArrayList<>();
		for (UserCharts chart : charts) {
			chartSplList.add(new Integer(chart.getChartSpecializationId()).toString());
		}
		return chartSplList;
	}

	

	

	/**
	 * 
	 * @param uploadTime
	 * @return
	 * 
	 *         When supervisor upload the chart, sometime it takes time to
	 *         upload multiple excel sheel. In this time difference the first
	 *         upload chart will come first because the rem tat value is less.
	 *         this is failing in below condition: For Example coder A : Chart
	 *         SPL Priority IP-INM 1 ER-ER 2 If supervisor upload the ER chart
	 *         first and than IP chart, then Coder A will get the ER chart first
	 *         because Remaining TAT value is less for ER. This method
	 *         implemented to avoid the Minutes difference We did not get
	 *         confirmation from Eclat team to use this.
	 */

	private Timestamp getUploadDateUptoHours(Timestamp uploadTime) {
		// Timestamp time = new Timestamp(uploadTime.getYear(),
		// uploadTime.getMonth(), uploadTime.getDay(),
		// uploadTime.getHours(), ZERO, ZERO, ZERO);
		return uploadTime;
	}
}
