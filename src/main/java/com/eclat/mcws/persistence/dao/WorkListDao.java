package com.eclat.mcws.persistence.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;

public interface WorkListDao extends Dao<WorkListItem, Long> {

	WorkListItem update(WorkListItem workListItem);

	void updateWorkListItemToAudit(WorkListItem workListItem) throws Exception;

	List<WorkListItem> getActiveWorkListByCoder(int coderId);

	List<WorkListItem> getCompletedWorksByCoder(int coderId, Timestamp startDate, Timestamp endDate);

	List<WorkListItem> getAllWorksByCoder(int coderId);

	List<WorkListItem> getInProgWorksByCoder(int coderId);

	WorkListItem normalUpdate(WorkListItem workItem);
	
	public boolean updateWorkListItems(final WorkListItem workListItem);

	boolean saveOrUpdateReviewWorklist(final ReviewWorklist reviewWorklist);

	/**
	 * 
	 * @param userId
	 * @param worklistId
	 * @return
	 */
	ReviewWorklist getReviewWorklistByUserIdAndWorklistId(final int userId, final long worklistId);

	/**
	 * 
	 * @param userId
	 * @param status
	 * @return
	 */
	ReviewWorklist getReviewWorklistByWorklistIdAndStatus(final Long worklistId, final String status);

	public List<WorkListItem> getAllWorkListItems();

	public List<WorkListItem> getAllAssignedWorkListItems();

	public List<WorkListItem> getAllAssignedNotCompletedWorkListItems();

	public List<WorkListItem> getAllNotCompletedWorkListItems();

	public List<WorkListItem> getAllNotCompletedWorkListItemsByDateRange(final Timestamp startDate,
			final Timestamp endDate);

	public List<WorkListItem> getPresentDayWorkListItemsByCoder(final int coderId);

	public List<WorkListItem> getPresentDayWorkListItemsByQA(final int qaId);

	public List<WorkListItem> getWorkListItemsByCoder(final int coderId);

	public List<WorkListItem> getAllWorkListItemsReviewedByQA(final int qaId);

	public List<ReviewWorklist> getUsersPresentDayWorkItemsById(final int userId);

	List<WorkListItem> getActiveWorkListByClientID(Integer clientId, Coders coder, boolean coderTask);

	WorkListItem getAuditWorkListByClientID(Integer clientId, Coders coder);

	WorkListItem getCompletedWorklistForUserByClientId(Integer clientId, Coders coder);

	List<WorkListItem> getAuditInProgressItemsByCoder(Integer coderId);

	// added for Incomplete work items
	Boolean updateIncompleteWorkItems(List<WorkListItem> items);

	List<WorkListItem> getAuditWorklistItemForUserByClientId(Integer clientId, Coders coder);

	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return Completed worklist item for Auditing
	 * @throws Exception
	 */
	public List<WorkListItem> getCompletedWorklistToAuditByUpdateDateRange(final Timestamp fromDate,
			final Timestamp toDate) throws Exception;

	public List<WorkListItem> getWorkListItemsByAccountNumber(final String accountNumber);

	public List<WorkListItem> getWorkListItemsByMRNumber(final String mrNumber);

	/**
	 * 
	 * @param clientId
	 * @param coder
	 * @param isCoder
	 * @return List
	 * @throws Exception
	 */
	public List<WorkListItem> fetchCoderWorkItem(Integer clientId, Coders coder, boolean isCoder,
			List<String> chartSplList, String chartStatus, boolean isUsedForAutoEMProcess, Session parentSession)
			throws Exception;

	/**
	 * 
	 * @param clientId
	 * @param coder
	 * @param isCoder
	 * @param chartSplList
	 * @param chartStatus
	 * @param emStart
	 * @param emEnd
	 * @return
	 * @throws Exception
	 */
	public List<WorkListItem> fetchCoderWorkItemByEMRange(final Integer clientId, final Coders coder,
			final boolean isCoder, List<String> chartSplList, String chartStatus, final Double emStart,
			final Double emEnd) throws Exception;

	/**
	 * 
	 * @param worklistId
	 * @param userId
	 * @return
	 */
	boolean updateWorkStartTime(final Long worklistId, final Integer userId);

	/**
	 * 
	 * @param worklistId
	 * @param userId
	 * @return
	 */
	boolean updateUserTotalWorkTime(final Long worklistId, final Integer userId, final Integer totalWorkTime);

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return List
	 */
	public List<WorkListItem> getAllWorkListItemsRangeByUplodedDate(final Date startDate, final Date endDate);

	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws Exception
	 */
	public List<WorkListItem> getAuditItemsForRemoteQAByUpdateDateRange(Timestamp fromDate, Timestamp toDate)
			throws Exception;

	/**
	 * 
	 * @param worklistIds
	 * @return List
	 */
	List<WorkListItem> getWorkListByIds(List<Long> worklistIds);

	public List<ReviewWorklist> getReviewWorklistsByUserId(final int userId);

	public List<ReviewWorklist> getReviewWorklistsByUserIdAndDateRange(final int userId, final Timestamp fromDate,
			final Timestamp toDate);

	public List getWorklistDataForSupervisorReport(final String fromDate, final String toDate);

	public Set<ReviewWorklist> getReviewWorklistsByWorklistId(final long workListId);

	public boolean deleteReviewWorklist(final ReviewWorklist reviewWorklist);


	/**
	 * 
	 * @param worklistIds
	 * @return TRUE/FALSE
	 * 
	 *         It deletes selected worklist and related records from
	 *         review_worklist table.
	 */
	public boolean deleteWorklistItems(final List<Long> worklistIds);

	/**
	 * 
	 * @param specializationIds
	 * @param emStart
	 * @param emEnd
	 * @return
	 */
	public List<WorkListItem> getWorkListItemByChartSpecializationIdsAndEMValueRange(
			final List<String> specializationIds, final Double emStart, final Double emEnd);

	/**
	 * 
	 * @return worklist item which are not process for coder's daily workload
	 *         assignment
	 */
	public List<WorkListItem> getAutoEmUnProcessedItems();
	
	/**
	 * 
	 * @return Set the worklist item to default mode, make is_used_for_auto_em_process to false
	 */
	public Boolean resetAutoEmProcessItems();
	
	/**
	 * 
	 * @param worklist
	 * @param coders
	 * @return
	 */
	public Boolean updateManualAssignedWorkitem(final WorkListItem worklist , final List<Coders> coders);
	
}
