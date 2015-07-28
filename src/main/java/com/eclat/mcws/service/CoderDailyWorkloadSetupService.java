package com.eclat.mcws.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.comparator.CoderCompletedLoadComparator;
import com.eclat.mcws.util.comparator.CoderDailyLoadComparator;

@Component
public class CoderDailyWorkloadSetupService {

	@Log
	private Logger logger;

	@Autowired
	private CoderDao coderDao;

	@Autowired
	private WorkListDao worklistDao;

	@Autowired
	private WorkDistributionService workDistributionService;

	@Autowired
	private AppStartupComponent appStartupComponent;

	@Autowired
	private SessionFactory sessionFactory;

	public Boolean setupCoderDailyWorkload() {
		final List<Coders> coders = getAllAvailableCoders();

		logger.info(" ----> Total available Coders are : " + coders.size());
		logger.info(" #######  Detail ###### ");
		for (Coders coder : coders) {

			logger.info(" ----> ID = {}, NAME = {} ", new Object[] { coder.getUserId(),
					coder.getFirstname() + " " + coder.getLastname() });
		}

		Boolean isAssign = processWorkloadAssignment(true);
		if (isAssign) {

			logger.info(" **** Excess Work is avialable. Start re-assignment. ");

			final List<WorkListItem> unProcessedItems = worklistDao.getAutoEmUnProcessedItems();
			if (unProcessedItems != null && unProcessedItems.size() > 0) {
				isAssign = processWorkloadAssignment(false);
			}
		}

		return isAssign;
	}

	private boolean processWorkloadAssignment(boolean checkMaxLoad) {
		Boolean isAssign = true;
		final List<Coders> coders = getAllAvailableCoders();
		Session session = sessionFactory.openSession();
		Transaction txn = session.beginTransaction();
		Map<Coders, Boolean> codersMap = new HashMap<>();
		try {
			while (coders.size() > 0) {
				Coders coder = null;
				final List<Coders> remCoders = new ArrayList<>();
				
				if(checkMaxLoad)
					coder = getLeastDailyAssignedLoadCoder(coders);
				else
					coder = getCoderToAssignedExcessWorkLoad(codersMap);
				
				if(coder != null){
					ChartRestQueryDetails restQuery = new ChartRestQueryDetails();
					restQuery.setUserId(coder.getUserId());
					restQuery.setCoderId(coder.getUserId());
					restQuery.setCoderTask(true);

					final WorkListItem item = workDistributionService.getCoderWorkItem(coder, restQuery, false, session);
					if (item != null) {
						if (checkMaxLoad) {
							if (coder.getCoderMaxWorkLoad() > (coder.getCoderDailyWorkLoad() + coder.getCompleteLoad())) {
								Double maxworkLoad = coder.getCoderDailyWorkLoad() + item.getEffortMetric();
								coder.setCoderDailyWorkLoad(maxworkLoad);

								item.setIsUsedForAutoEMProcess(true);
								session.update(item);
							} else {
								// This is required when coders has InComplete/coderAssined charts
								final Double emCount = getCoderInCompleteWorkEMCount(coder.getUserId());
								
								//If supervisor run the daily workload setup in middle of the day 
								//then adding completedLoad to dailyWorkLoad
								final Double maxDailyWork = (coder.getCoderDailyWorkLoad()+ coder.getCompleteLoad()) + emCount;
								coder.setCoderDailyWorkLoad(maxDailyWork);
								
								session.update(coder);
								remCoders.add(coder);
							}
						} else {
							Double maxworkLoad = coder.getCoderDailyWorkLoad() + item.getEffortMetric();
							coder.setCoderDailyWorkLoad(maxworkLoad);

							item.setIsUsedForAutoEMProcess(true);
							session.update(item);
						}
					} else {
						if (checkMaxLoad) {
							//If supervisor run the daily workload setup in middle of the day
							//then adding completedLoad to dailyWorkLoad
							Double maxDailyWork = coder.getCoderDailyWorkLoad() + coder.getCompleteLoad();
							coder.setCoderDailyWorkLoad(maxDailyWork);
							
							// This is required when coders has InComplete/coderAssigned charts
							final Double emCount = getCoderInCompleteWorkEMCount(coder.getUserId());
							final Double maxDailyEMWork = coder.getCoderDailyWorkLoad() + emCount;
							coder.setCoderDailyWorkLoad(maxDailyEMWork);
						}
						session.update(coder);
						remCoders.add(coder);
					}
					coders.removeAll(remCoders);
				} else {
					if(!checkMaxLoad)
						initializeCodersMapForExcessWorkAssignment(codersMap, coders);
				}
			}
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			logger.error(" Exp ON setupCoderDailyWorkload : ", e);
			isAssign = false;
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return isAssign;
	}

	private List<Coders> getAllAvailableCoders() {
		final List<Coders> coders = new ArrayList<>();

		appStartupComponent.postInitializeData();

		Map<Integer, Coders> codersMap = appStartupComponent.getCodersMap();
		for (Entry<Integer, Coders> entry : codersMap.entrySet()) {
			if (entry.getValue().getIsAvailable()) {
				if (entry.getValue().getCoder() != null && entry.getValue().getCoder())
					coders.add(entry.getValue());
			}
		}
		return coders;
	}

	/**
	 * 
	 * @param coders
	 * @return coder who's (dailyWorkload + completedLoad) is least.
	 */
	public Coders getLeastDailyAssignedLoadCoder(final List<Coders> coders) {
		Collections.sort(coders, new CoderDailyLoadComparator());
		return coders.get(0);
	}

	public Boolean resetDailyCodersWorkload() {
		final Boolean isReset = worklistDao.resetAutoEmProcessItems();
		if (isReset) {
			return coderDao.resetCoderDailyWorkload();
		}
		return false;
	}
	
	/**
	 * This will initialize the MAP<coder, boolean>.
	 * For each coder has boolean flag to identify if the coder's dailyWorkload increased or not 
	 * into the current running cycle.  
	 * This will executes when all the coders (dailyWorkload >= static load)
	 * AND After that if system has more work to assign.
	 * @param codersMap
	 * @param coders
	 */
	private void initializeCodersMapForExcessWorkAssignment(Map<Coders, Boolean> codersMap, List<Coders> coders){
		//codersMap.clear();
		for(Coders coder : coders) {
			codersMap.put(coder, false);
		}
	}
	
	/**
	 * This will executes after all coders dailyWorkload >= static load
	 * AND After that if system has more work to assign.
	 * 
	 * @param codersMap
	 * @return Coder object which dailyWorkload EM not increase during current cycle.
	 */
	private Coders getCoderToAssignedExcessWorkLoad(Map<Coders, Boolean> codersMap) {
		Coders coder = null ;
		if(codersMap.size() > 0) {
			for(Entry<Coders, Boolean> entry : codersMap.entrySet()) {
				if(!entry.getValue()) {
					coder = entry.getKey();
					codersMap.put(coder, true);
					break;
				}
			}
		}
		return coder;
	}

	/**
	 * If coderAssigned and InComplete charts is remained on his/her pool
	 * Then doing sum of all coderAssigned and InComplete chart EM and return.
	 * This EM count will add to coder dailyWorkload.
	 * @param coderId
	 * @return
	 */
	public Double getCoderInCompleteWorkEMCount(final Integer coderId) {
		List<WorkListItem> items = worklistDao.getInProgWorksByCoder(coderId);
		Double emCount = 0.0;
		if (items != null) {
			for (WorkListItem task : items) {
				if (task.getStatus().equals(ChartStatus.InComplete.getStatus()) ||
						task.getStatus().equals(ChartStatus.CoderAssigned.getStatus())) {
					emCount = emCount + task.getEffortMetric();
				}
			}
		}
		return emCount;
	}

}
