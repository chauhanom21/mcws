package com.eclat.mcws.persistence.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;

@Repository
public class WorkListHibernateDaoImpl implements WorkListDao {

	private static final int ZERO = 0;
	private static final int TWENTY_THIRD = 23;
	private static final int FIFTY_NINE = 59;
	private static final int ONE = 1;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private CoderDao coderDao;

	@Autowired
	private ClientDetailsDao clientDao;

	@Log
	private Logger logger;

	// Commenting the Second level Cache on queries - for the purpose 0f
	// openShift deployment
	@Override
	public List<WorkListItem> findAll() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM WorkListItem");
			return query.list();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public WorkListItem find(Long id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (WorkListItem) session.get(WorkListItem.class, id);
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public WorkListItem save(WorkListItem newEntry) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			if (newEntry.getTat() == 0 || null == newEntry.getTat())
				newEntry.setTat(24);
			session.save(newEntry);
			return newEntry;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public Boolean saveAll(List<WorkListItem> newEntries) {
		Session session = null;
		Transaction txn = null;
		try {
			int count = 0;
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			for (WorkListItem item : newEntries) {
				count ++;
				if (item.getTat() == 0 || null == item.getTat())
					item.setTat(24);

				session.save(item);
				if(count >= 50) {
					session.flush();
					count = 0;
				}
			}
			// All are saved now commit
			txn.commit();
			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	// adding for incomplete worklist items
	@Override
	public Boolean updateIncompleteWorkItems(List<WorkListItem> items) {
		Session session = null;
		Query query, query_worklist = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			for (WorkListItem item : items) {
				// for incompltete items
				// query =
				// session.createQuery("select item from ReviewWorklist item where "
				// +
				// "item.worklist_id = (select id from WorkListItem where accountNumber="
				// + item.getAccountNumber() + " limit 1)");
				if (null != item.getAccountNumber() && null != item.getMrNumber()
						&& !"".equals(item.getAccountNumber()) && !"".equals(item.getMrNumber())) {
					query_worklist = session
							.createQuery(" from WorkListItem where accountNumber = :accountNumber and mrNumber = :mrNumber and status = :workItemStatus");
					query_worklist.setParameter("accountNumber", item.getAccountNumber());
					query_worklist.setParameter("mrNumber", item.getMrNumber());
				} else if (null != item.getAccountNumber()
						&& (null == item.getMrNumber() || "".equals(item.getMrNumber()))) {
					query_worklist = session
							.createQuery(" from WorkListItem where accountNumber = :accountNumber and status = :workItemStatus");
					query_worklist.setParameter("accountNumber", item.getAccountNumber());
				} else if ((null == item.getAccountNumber() || "".equals(item.getAccountNumber()))
						&& null != item.getMrNumber()) {
					query_worklist = session
							.createQuery(" from WorkListItem where mrNumber = :mrNumber and status = :workItemStatus");
					query_worklist.setParameter("mrNumber", item.getMrNumber());
				}

				query_worklist.setParameter("workItemStatus", ChartStatus.InComplete.getStatus());
				WorkListItem dbWorkItem = (WorkListItem) query_worklist.uniqueResult();

				if (dbWorkItem != null) {
					logger.debug("dbWorkItem :" + dbWorkItem.getId() + "&& status :" + dbWorkItem.getStatus()
							+ " && ac no: " + dbWorkItem.getAccountNumber() + " && mr no: " + dbWorkItem.getMrNumber());

					query = session
							.createQuery(" from ReviewWorklist where workListItem.id = :worklistId and status = :workItemStatus");
					query.setParameter("worklistId", dbWorkItem.getId());
					query.setParameter("workItemStatus", ChartStatus.InComplete.getStatus());

					ReviewWorklist reviewWork = (ReviewWorklist) query.uniqueResult();
					if (null != reviewWork && null != item.getComments()) {
						// dbWorkItem.setComments(item.getComments());
						reviewWork.setComment(reviewWork.getComment() + " && New Comment :: " + item.getComments());
						reviewWork.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
						// setting up the status to Incomplete workitem based on
						// who made it to 'Incomplete' state
						if (UserRoles.Coder.toString().equalsIgnoreCase(reviewWork.getUserRole().trim()))
							// reviewWork.setStatus(ChartStatus.CodingInProgress.getStatus());
							reviewWork.setStatus(ChartStatus.CoderAssigned.getStatus());
						else if (UserRoles.LocalQA.toString().equalsIgnoreCase(reviewWork.getUserRole().trim()))
							// reviewWork.setStatus(ChartStatus.QAInProgress.getStatus());
							reviewWork.setStatus(ChartStatus.LocalQAAssigned.getStatus());
						else if (UserRoles.RemoteQA.toString().equalsIgnoreCase(reviewWork.getUserRole().trim()))
							// reviewWork.setStatus(ChartStatus.RemoteQAInProgress.getStatus());
							reviewWork.setStatus(ChartStatus.GlobalQAAssigned.getStatus());

						// updating the review worklist data
						saveOrUpdateReviewWorklist(reviewWork);

						dbWorkItem.setUpdatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
						dbWorkItem.setStatus(reviewWork.getStatus());
						// update(dbWorkItem);
						session.saveOrUpdate(dbWorkItem);
					}
				}
			}
			// All are saved now commit
			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			session.getTransaction().rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void delete(Long id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			WorkListItem worklist = (WorkListItem) session.get(WorkListItem.class, id);
			session.delete(worklist);
		} catch (final HibernateException hexp) {
			logger.error("Exception while Worklist Delete : ", hexp);
		} finally {
			closeSession(session);
		}

	}

	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	
	@Override
	public WorkListItem update(WorkListItem workItem) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.update(workItem);
			txn.commit();
			return workItem;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public void updateWorkListItemToAudit(WorkListItem workListItem) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.update(workListItem);
			logger.debug("WorkItem has been updated......");
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getActiveWorkListByCoder(int coderId) {
		Session session = null;
		Query query = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			/*
			 * query =
			 * session.createQuery("select item from WorkListItem item where " +
			 * "item.coderId = :coderId " +
			 * "and item.status in ("+ChartStatus.CodingInProgress
			 * .getStatus()+","+ChartStatus.InComplete.getStatus()+")");
			 * query.setParameter("coderId", coderId);
			 */

			Coders coder = coderDao.find(coderId);
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.clientDetails", "client");
			criteria.add(Restrictions.eq("item.coderId", coderId));
			Object[] obj = new Object[3];
			obj[0] = ChartStatus.InComplete.getStatus();
			if (coder.getLocalQa()) {
				obj[1] = ChartStatus.LocalQAInProgress.getStatus();
				obj[2] = ChartStatus.LocalQAAssigned.getStatus();
				criteria.add(Restrictions.in("item.status", obj));
			} else if (coder.getCoder()) {
				obj[1] = ChartStatus.CodingInProgress.getStatus();
				obj[2] = ChartStatus.CoderAssigned.getStatus();
				criteria.add(Restrictions.in("item.status", obj));
			} else {
				obj[1] = ChartStatus.GlobalQAInProgress.getStatus();
				obj[2] = ChartStatus.GlobalQAAssigned.getStatus();
				criteria.add(Restrictions.in("item.status", obj));
			}
			// Add distinct condition
			criteria.setProjection(Projections.distinct(Projections.property("id")));
			logger.info("===============Size of the Active workitems (" + coderId + ")====>" + criteria.list().size());
			return (List<WorkListItem>) criteria.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			query = null;
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getCompletedWorksByCoder(int coderId, Timestamp startDate, Timestamp endDate) {
		Session session = null;
		Criteria criteria = null;
		try {
			logger.debug(" ==> startDate: " + startDate + "  endDate: " + endDate);
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.reviewWorkLists", "reviews");
			criteria.add(Restrictions.eq("reviews.userReviewd", coderId));
			criteria.add(Restrictions.between("reviews.updatedDate", startDate, endDate));

			return (List<WorkListItem>) criteria.list();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getAllWorksByCoder(int coderId) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("select item from WorkListItem item where " + "item.coderId = :coderId ");
			query.setParameter("coderId", coderId);
			return (List<WorkListItem>) query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			query = null;
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getInProgWorksByCoder(int coderId) {
		Session session = null;
		Criteria criteria = null;

		try {
			session = sessionFactory.openSession();
			Coders coder = coderDao.find(coderId);
			session = sessionFactory.openSession();

			final String queryStr = "SELECT p FROM WorkListItem p WHERE p.coderId=:userId"
					+ " AND p.status IN (:statusArray)";

			final Query query = session.createQuery(queryStr);
			query.setParameter("userId", coderId);

			List<String> statusList = new ArrayList<String>();
			statusList.add(ChartStatus.InComplete.getStatus());

			if (coder.getLocalQa()) {
				statusList.add(ChartStatus.LocalQAAssigned.getStatus());
				statusList.add(ChartStatus.LocalQAInProgress.getStatus());
				statusList.add(ChartStatus.CodingInProgress.getStatus());
				statusList.add(ChartStatus.CoderAssigned.getStatus());
			} else if (coder.getRemoteQa()) {
				statusList.add(ChartStatus.GlobalQAInProgress.getStatus());
				statusList.add(ChartStatus.GlobalQAAssigned.getStatus());
			} else {
				statusList.add(ChartStatus.CodingInProgress.getStatus());
				statusList.add(ChartStatus.CoderAssigned.getStatus());
			}
			query.setParameterList("statusArray", statusList);

			logger.debug("======= List of the In-Progress Items OF (" + coderId + ")====>" + query.list());

			return (List<WorkListItem>) query.list();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public boolean saveOrUpdateReviewWorklist(final ReviewWorklist reviewWorklist) {
		Session session = null;
		Transaction txn = null;
		// Long millis = 0L;
		try {
			session = sessionFactory.openSession();
			session.evict(reviewWorklist);
			txn = session.beginTransaction();

			// Dao method is calling multiple places.
			// So this code here to make it common and avoid duplicate code
			// writing.
			reviewWorklist.setTotalTimeTaken(getCoderTotalWorkTimeInMins(reviewWorklist));

			session.merge(reviewWorklist);
			txn.commit();
			return true;
		} catch (final Exception e) {
			logger.error("Hibernate Exception :: ", e);
			throw e;
		} finally {
			closeSession(session);
		}
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

	@Override
	public ReviewWorklist getReviewWorklistByUserIdAndWorklistId(final int userId, final long worklistId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(ReviewWorklist.class, "item");
			criteria.createAlias("item.workListItem", "worklist");
			criteria.add(Restrictions.eq("worklist.id", worklistId));
			criteria.add(Restrictions.eq("item.userReviewd", userId));
			return (ReviewWorklist) criteria.uniqueResult();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public ReviewWorklist getReviewWorklistByWorklistIdAndStatus(final Long worklistId, final String status) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(ReviewWorklist.class, "item");
			criteria.createAlias("item.workListItem", "worklist");
			criteria.add(Restrictions.eq("worklist.id", worklistId));
			criteria.add(Restrictions.eq("item.status", status));
			return (ReviewWorklist) criteria.uniqueResult();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getAllWorkListItems() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from WorkListItem p";
			final Query query = session.createQuery(selectQuery);
			final Object result = query.list();
			if (result != null) {
				return (List<WorkListItem>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;

	}

	@Override
	public List<WorkListItem> getWorkListItemsByCoder(int coderId) {

		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from WorkListItem p WHERE p.coderId = :coderId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("coderId", coderId);
			final Object result = query.list();
			if (result != null) {
				return (List<WorkListItem>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getAllWorkListItemsReviewedByQA(final int qaUserId) {
		Session session = null;
		try {
			// Use Criteria
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.reviewWorkLists", "reviews");
			criteria.add(Restrictions.eq("reviews.userReviewd", qaUserId));
			criteria.setCacheable(true);
			criteria.setCacheRegion("worklist.query.cache");
			return (List<WorkListItem>) criteria.list();

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getPresentDayWorkListItemsByQA(final int qaId) {
		Session session = null;
		try {
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, ZERO);
			cal.set(Calendar.MINUTE, ZERO);
			cal.set(Calendar.SECOND, ONE);
			final Date startTime = new Date(cal.getTimeInMillis());

			cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
			cal.set(Calendar.MINUTE, FIFTY_NINE);
			cal.set(Calendar.SECOND, FIFTY_NINE);
			final Date endTime = new Date(cal.getTimeInMillis());

			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.reviewWorkLists", "reviews");
			criteria.add(Restrictions.eq("reviews.userReviewd", qaId));
			criteria.add(Restrictions.between("item.updatedDate", startTime, endTime));
			criteria.setCacheable(true);
			criteria.setCacheRegion("worklist.query.cache");
			return (List<WorkListItem>) criteria.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}

		return null;
	}

	@Override
	public List<WorkListItem> getAllAssignedWorkListItems() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from WorkListItem p WHERE p.coderId IS NOT NULL";
			final Query query = session.createQuery(selectQuery);
			final Object result = query.list();
			if (result != null) {
				return (List<WorkListItem>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getAllAssignedNotCompletedWorkListItems() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "SELECT p from WorkListItem p WHERE p.coderId IS NOT NULL AND p.status != 'Completed' ";
			final Query query = session.createQuery(selectQuery);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getAllNotCompletedWorkListItems() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "SELECT p FROM WorkListItem p WHERE (p.status IS NULL OR p.status != 'Completed')";
			final Query query = session.createQuery(selectQuery);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception :  ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getPresentDayWorkListItemsByCoder(final int coderId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();

			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, ZERO);
			cal.set(Calendar.MINUTE, ZERO);
			cal.set(Calendar.SECOND, ONE);
			final Date startTime = new Date(cal.getTimeInMillis());

			cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
			cal.set(Calendar.MINUTE, FIFTY_NINE);
			cal.set(Calendar.SECOND, FIFTY_NINE);
			final Date endTime = new Date(cal.getTimeInMillis());

			final Criteria crit = session.createCriteria(WorkListItem.class, "workList");
			crit.add(Restrictions.eq("coderId", coderId));
			crit.add(Restrictions.between("updatedDate", startTime, endTime));

			return (List<WorkListItem>) crit.list();
		} catch (final Exception e) {
			logger.error("Exception  : ", e);
		} finally {
			closeSession(session);
		} 
		
		return null;
	}

	/*
	 * do not use cache on this query. this query parameter will change, so If
	 * cache used then It will not fetched the latest records.
	 * 
	 * @see com.eclat.mcws.supervisor.dao.SupervisorDao#
	 * getAllNotCompletedWorkListItemsByDateRange(java.sql.Date, java.sql.Date)
	 */
	@Override
	public List<WorkListItem> getAllNotCompletedWorkListItemsByDateRange(final Timestamp startDate,
			final Timestamp endDate) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT p FROM WorkListItem p WHERE (p.updatedDate BETWEEN :startDate AND :endDate)"
					+ " AND (p.status NOT IN(:status) OR p.status is NULL)";
			final Query query = session.createQuery(queryStr);
			query.setParameterList("status",
					new Object[] { ChartStatus.Completed.getStatus(), ChartStatus.LocalAudited.getStatus(),
							ChartStatus.GlobalAudited.getStatus(), ChartStatus.LocalAudit.getStatus(),
							ChartStatus.GlobalAudit.getStatus(), ChartStatus.MISC.getStatus() });
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception :: ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<ReviewWorklist> getUsersPresentDayWorkItemsById(int userId) {
		Session session = null;
		try {
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, ZERO);
			cal.set(Calendar.MINUTE, ZERO);
			cal.set(Calendar.SECOND, ONE);
			final Date startTime = new Date(cal.getTimeInMillis());

			cal.set(Calendar.HOUR_OF_DAY, TWENTY_THIRD);
			cal.set(Calendar.MINUTE, FIFTY_NINE);
			cal.set(Calendar.SECOND, FIFTY_NINE);
			final Date endTime = new Date(cal.getTimeInMillis());

			session = sessionFactory.openSession();
			final String selectQuery = "SELECT p FROM ReviewWorklist p WHERE p.userReviewd = :userReviewd"
					+ " AND p.updatedDate >= :startDate AND p.updatedDate <= :endDate";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userReviewd", userId);
			query.setParameter("startDate", startTime);
			query.setParameter("endDate", endTime);
			return (List<ReviewWorklist>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getActiveWorkListByClientID(Integer clientId, Coders coder, boolean coderTask) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Criteria criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.clientDetails", "client");
			criteria.add(Restrictions.eq("client.id", clientId));
			if (coderTask) {
				// Coding activities for QA and Coder
				criteria.add(Restrictions.isNull("item.status"));

			} else if (coder.getLocalQa()) { // qa

				criteria.add(Restrictions.ne("item.coderId", coder.getUserId()));
				ClientDetails clientDetails = clientDao.find(clientId);
				Criterion cnr = Restrictions.eq("item.status", ChartStatus.LocalCNR.getStatus());
				Criterion completedNr = Restrictions.eq("item.status", ChartStatus.CompletedNR.getStatus());

				if (clientDetails.isNewClient()) {
					logger.debug(" ==> Fetch task for New Client ..");
					// Create the subquery
					DetachedCriteria reviewCrit = DetachedCriteria.forClass(ReviewWorklist.class, "review");
					reviewCrit.add(Restrictions.eqProperty("review.status", "item.status"));
					Criterion revCompNr = Restrictions.eq("review.status", ChartStatus.CompletedNR.getStatus());
					Criterion reviewCnr = Restrictions.eq("review.status", ChartStatus.LocalCNR.getStatus());

					// Map the subquery back to the outer query.
					reviewCrit.add(Restrictions.ne("review.userReviewd", coder.getUserId()));
					reviewCrit.add(Restrictions.or(reviewCnr, revCompNr));
					// Add the missing projection.
					reviewCrit.setProjection(Projections.property("id"));
					// Add this subquery to the outer query.
					criteria.add(Subqueries.exists(reviewCrit));
				} else if (coder.getNewCoder()) {
					logger.debug(" ==> Fetch task for New Coder ..");
					criteria.add(Restrictions.or(cnr, completedNr));
				} else {
					criteria.add(cnr);
				}
			} else {
				criteria.add(Restrictions.eq("item.status", ChartStatus.GlobalCNR.getStatus()));
				DetachedCriteria reviewCrit = DetachedCriteria.forClass(ReviewWorklist.class, "review");
				reviewCrit.add(Restrictions.eqProperty("review.status", "item.status"));
				// Map the subquery back to the outer query.
				reviewCrit.add(Restrictions.ne("review.userReviewd", coder.getUserId()));
				// Add the missing projection.
				reviewCrit.setProjection(Projections.property("id"));
				// Add this subquery to the outer query.
				criteria.add(Subqueries.exists(reviewCrit));
			}
			criteria.addOrder(Order.asc("item.tat"));
			criteria.addOrder(Order.asc("item.effortMetric"));
			logger.debug("######### Workitems found for client( " + clientId + " ) ::: " + criteria.list());
			if (criteria.list().size() > 0) {
				return (List<WorkListItem>) criteria.list();
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public WorkListItem getAuditWorkListByClientID(Integer clientId, Coders coder) {
		Session session = null;
		Query query = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.createAlias("item.clientDetails", "client");
			criteria.add(Restrictions.eq("client.id", clientId));
			criteria.add(Restrictions.eq("item.audited", true));
			criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			criteria.setMaxResults(1);
			logger.info("===============Size of the workitems related to clients(" + clientId + ")====>"
					+ criteria.list());
			if (criteria.list().size() > 0) {
				return (WorkListItem) criteria.uniqueResult();
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			query = null;
			criteria = null;
			closeSession(session);
		}
	}

	/**
	 * Method is used to get the list of charts to Audit based on the user and
	 * Client
	 */
	public List<WorkListItem> getAuditWorklistItemForUserByClientId(Integer clientId, Coders coder) {
		Session session = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.add(Restrictions.eq("item.status", ChartStatus.Completed.getStatus()));

			criteria.add(Restrictions.ne("item.coderId", coder.getUserId()));
			criteria.add(Restrictions.isNull("item.audited"));
			criteria.createAlias("item.clientDetails", "client");
			criteria.add(Restrictions.eq("client.id", clientId));

			// logger.debug("=> Loading Auditing work items related to client( "+
			// clientId
			// +" ) For User(" + coder.getUserId() +")" +criteria.list());

			return (List<WorkListItem>) criteria.list();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}

	}

	/**
	 * Method is used to get the Completed charts to Audit based on the Client
	 * Configurations
	 */
	@Override
	public WorkListItem getCompletedWorklistForUserByClientId(Integer clientId, Coders coder) {
		Session session = null;
		Criteria criteria = null;
		logger.info("<==========Inside Completed Worklist Item for Coder=========>");
		try {
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			criteria.add(Restrictions.eq("item.status", ChartStatus.Completed.getStatus()));
			criteria.add(Restrictions.ne("item.coderId", coder.getUserId()));
			criteria.add(Restrictions.isNull("item.audited"));
			criteria.createAlias("item.clientDetails", "client");

			// Subquery on Coders table
			DetachedCriteria coderCrit = DetachedCriteria.forClass(Coders.class, "coder");
			// For Remote QA
			if (coder.getRemoteQa()) {
				coderCrit.add(Restrictions.eq("coder.localQa", true));
			} else if (coder.getLocalQa()) {
				coderCrit.add(Restrictions.eq("coder.coder", true));
			}
			// Add the missing projection.
			coderCrit.setProjection(Projections.property("id"));
			// Add this subquery to the outer query.
			criteria.add(Subqueries.exists(coderCrit));

			// Subquery on reviews
			DetachedCriteria reviewCrit = DetachedCriteria.forClass(ReviewWorklist.class, "review");
			// Map the subquery back to the outer query.
			reviewCrit.add(Restrictions.ne("review.userReviewd", coder.getUserId()));
			// Add the missing projection.
			reviewCrit.setProjection(Projections.property("id"));
			// Add this subquery to the outer query.
			criteria.add(Subqueries.exists(reviewCrit));

			criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			criteria.setMaxResults(1);

			logger.debug("=> Loading workitems related to client(" + clientId + ") For User(" + coder.getUserId() + ")"
					+ criteria.list());

			return (WorkListItem) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getAuditInProgressItemsByCoder(Integer coderId) {
		Session session = null;
		Criteria criteria = null;
		try {
			session = sessionFactory.openSession();
			session = sessionFactory.openSession();
			criteria = session.createCriteria(WorkListItem.class, "item");
			// criteria.add(Restrictions.isNull("item.audited"));
			criteria.createAlias("item.reviewWorkLists", "reviews");
			criteria.add(Restrictions.eq("reviews.userReviewd", coderId));
			criteria.add(Restrictions.or(Restrictions.eq("reviews.status", ChartStatus.LocalAudit.getStatus()),
					Restrictions.eq("reviews.status", ChartStatus.GlobalAudit.getStatus())));
			criteria.setCacheable(true);
			criteria.setCacheRegion("review.worklist.query.cache");
			logger.debug("===============Size of the In Prog Audit Of (" + coderId + ")====>" + criteria.list());
			return (List<WorkListItem>) criteria.list();

		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.eclat.mcws.common.dao.WorkListDao#getAuditWorklistItemForClient(int,
	 * java.sql.Date, java.sql.Date)
	 */
	public List<WorkListItem> getCompletedWorklistToAuditByUpdateDateRange(final Timestamp fromDate,
			final Timestamp toDate) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "FROM WorkListItem p WHERE p.status = :status"
					+ " AND p.audited IS NULL AND p.updatedDate >= :fromDate AND p.updatedDate <= :toDate";

			Query query = session.createQuery(selectQuery);
			query.setParameter("status", ChartStatus.Completed.getStatus());
			query.setDate("fromDate", fromDate);
			query.setTimestamp("toDate", toDate);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getAuditItemsForRemoteQAByUpdateDateRange(Timestamp fromDate, Timestamp toDate)
			throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();

			final String selectQuery = "FROM WorkListItem p WHERE p.status IN (:status)"
					+ " AND (p.updatedDate BETWEEN :fromDate AND :toDate)"
					+ " AND p.coderId IN (SELECT id FROM Coders WHERE localQa=true OR coder=true)";

			Query query = session.createQuery(selectQuery);
			query.setParameterList("status",
					new Object[] { ChartStatus.Completed.getStatus(), ChartStatus.LocalAudited.getStatus() });
			query.setDate("fromDate", fromDate);
			query.setTimestamp("toDate", toDate);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	/*
	 * It load all the data of client, within the range of EM Value. The Range
	 * of EM value is based on coder/QA Job Level. return the Work Items on
	 * Order by TAT asc and EM order based on client.
	 * 
	 * @see
	 * com.eclat.mcws.common.dao.WorkListDao#fetchCoderWorkItem(java.lang.Integer
	 * , com.eclat.mcws.persistence.entity.Coders, boolean)
	 */
	public List<WorkListItem> fetchCoderWorkItem(Integer clientId, Coders coder, boolean isCoder,
			List<String> chartSplList, String chartStatus, boolean isUsedForAutoEMProcess, Session parentSession) throws Exception {
		Session session = null;
		try {
			if (parentSession == null)
			session = sessionFactory.openSession();
			else 
				session = parentSession;
			
			final ClientDetails client = clientDao.find(clientId);
			Query query = null;
			final String clientDistOrder = client.getDistOrder();

			if (isCoder) { // Is Coder

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status IS NULL AND p.coderId IS NULL AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) AND p.isUsedForAutoEMProcess =:isUsedForAutoEMProcess "
						+ "order by p.tat asc, p.effortMetric "	+ clientDistOrder;
				query = session.createQuery(strQuery);

			} else if (coder.getLocalQa()) { // LocalQA

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status =:status AND p.coderId != :userId AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) AND p.isUsedForAutoEMProcess =:isUsedForAutoEMProcess "
						+ "order by p.status, p.tat asc, p.effortMetric "
						+ clientDistOrder;
				query = session.createQuery(strQuery);
				query.setParameter("userId", coder.getUserId());
				query.setParameter("status", chartStatus);

			} else { // For RemoteQA

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status IN(:statusArray) AND p.coderId != :userId AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) AND p.isUsedForAutoEMProcess =:isUsedForAutoEMProcess "
						+ "order by p.tat asc, p.effortMetric " + clientDistOrder;
				query = session.createQuery(strQuery);
				query.setParameter("userId", coder.getUserId());
				query.setParameterList("statusArray", new Object[] { ChartStatus.GlobalCNR.getStatus() });
			}

			query.setParameter("client", client);
			query.setParameter("from", 0.1);
			query.setParameter("to", coder.getEmValue());
			query.setParameterList("chartSpl", chartSplList);
			query.setParameter("isUsedForAutoEMProcess", isUsedForAutoEMProcess);
			final List queryList = query.list();
			logger.debug("######### Workitems found for client( " + client.getId() + " ) ::: " + queryList.size());

			if (queryList.size() > 0) {
				return (List<WorkListItem>) queryList;
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			//Added condition for coder daily max workload
			if (parentSession == null)
			closeSession(session);
		}
	}

	public List<WorkListItem> fetchCoderWorkItemByEMRange(final Integer clientId, final Coders coder,
			final boolean isCoder, List<String> chartSplList, String chartStatus, final Double emStart,
			final Double emEnd) throws Exception {

		Session session = null;
		try {
			session = sessionFactory.openSession();
			final ClientDetails client = clientDao.find(clientId);
			Query query = null;
			final String clientDistOrder = client.getDistOrder();

			if (isCoder) { // Is Coder

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status = null AND p.coderId = null AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) order by p.tat asc, p.effortMetric " + clientDistOrder;
				query = session.createQuery(strQuery);

			} else if (coder.getLocalQa()) { // LocalQA

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status =:status AND p.coderId != :userId AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) order by p.status, p.tat asc, p.effortMetric "
						+ clientDistOrder;
				query = session.createQuery(strQuery);
				query.setParameter("userId", coder.getUserId());
				query.setParameter("status", chartStatus);

			} else { // For RemoteQA

				String strQuery = "SELECT p FROM WorkListItem p WHERE p.clientDetails = :client "
						+ "AND p.status IN(:statusArray) AND p.coderId != :userId AND (p.effortMetric BETWEEN :from AND :to) "
						+ "AND p.chartSpl IN (:chartSpl) order by p.tat asc, p.effortMetric " + clientDistOrder;
				query = session.createQuery(strQuery);
				query.setParameter("userId", coder.getUserId());
				query.setParameterList("statusArray", new Object[] { ChartStatus.GlobalCNR.getStatus() });
			}

			query.setParameter("client", client);
			query.setParameter("from", emStart);
			query.setParameter("to", emEnd);
			query.setParameterList("chartSpl", chartSplList);
			final List queryList = query.list();
			logger.debug("######### Workitems found for client( " + client.getId() + " ) ::: " + queryList.size());

			if (queryList.size() > 0) {
				return (List<WorkListItem>) queryList;
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<WorkListItem> getWorkListItemsByAccountNumber(String accountNumber) {
		Session session = null;
		try {
			final String queryStr = "SELECT p FROM WorkListItem p WHERE p.accountNumber=:accountNumber";
			session = sessionFactory.openSession();
			final Query query = session.createQuery(queryStr);
			query.setParameter("accountNumber", accountNumber);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getWorkListItemsByMRNumber(String mrNumber) {
		Session session = null;
		try {
			final String queryStr = "SELECT p FROM WorkListItem p WHERE p.mrNumber=:mrNumber";
			session = sessionFactory.openSession();
			final Query query = session.createQuery(queryStr);
			query.setParameter("mrNumber", mrNumber);
			return (List<WorkListItem>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public boolean updateWorkStartTime(final Long worklistId, final Integer userId) {
		Session session = null;
		Transaction txn = null;
		try {
			final String queryStr = "UPDATE review_worklist set work_start_time = NOW(), lastupdated_date=NOW() "
					+ "WHERE worklist_id=:worklistId AND user_reviewd =:userId";
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			final Query query = session.createSQLQuery(queryStr);
			query.setParameter("worklistId", worklistId);
			query.setParameter("userId", userId);
			query.executeUpdate();
			txn.commit();
			return true;
		} catch (final Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return false;
	}

	@Override
	public boolean updateUserTotalWorkTime(final Long worklistId, final Integer userId, final Integer totalWorkTime) {
		Session session = null;
		try {
			final String queryStr = "UPDATE review_worklist set total_work_time_mins = :totalWorkTime, lastupdated_date=NOW() "
					+ "WHERE worklist_id=:worklistId AND user_reviewd =:userId";
			session = sessionFactory.openSession();
			final Query query = session.createSQLQuery(queryStr);
			query.setParameter("worklistId", worklistId);
			query.setParameter("userId", userId);
			query.setParameter("totalWorkTime", totalWorkTime);
			query.executeUpdate();
			return true;
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return false;
	}

	@Override
	public List<WorkListItem> getAllWorkListItemsRangeByUplodedDate(Date startDate, Date endDate) {
		Session session = null;
		try {
			final String queryStr = "SELECT p FROM WorkListItem p "
					+ "WHERE (p.updatedDate BETWEEN :startDate AND :endDate) OR p.status is NULL";
			session = sessionFactory.openSession();
			final Query query = session.createQuery(queryStr);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<WorkListItem> getWorkListByIds(List<Long> worklistIds) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session.createQuery("SELECT p FROM WorkListItem p WHERE p.id IN(:ids)");
			query.setParameterList("ids", worklistIds);
			return query.list();
		} catch (final Exception e) {
			logger.error("getWorkListByIds : : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<ReviewWorklist> getReviewWorklistsByUserId(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from ReviewWorklist p where p.userReviewd = :userId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			return (List<ReviewWorklist>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List<ReviewWorklist> getReviewWorklistsByUserIdAndDateRange(final int userId, final Timestamp fromDate,
			final Timestamp toDate) {

		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from ReviewWorklist p where p.userReviewd = :userId"
					+ " AND (updatedDate BETWEEN :fromDate AND :toDate)";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			return (List<ReviewWorklist>) query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public List getWorklistDataForSupervisorReport(final String fromDate, final String toDate) {
		Session session = null;
		try {
			session = sessionFactory.openSession();

			final String sqlQuery = "SELECT clientName, chartType,"
					+ " MAX(CASE WHEN status = 'InComplete' THEN loads ELSE 0 END) `InComplete`,"
					+ " MAX(CASE WHEN status = 'Completed' THEN loads ELSE 0 END) `Completed`,"
					+ " MAX(CASE WHEN status = 'MISC' THEN loads ELSE 0 END) `Misc`,"
					+ " MAX(CASE WHEN status = 'Coder Assigned' THEN loads ELSE 0 END) `CoderAssigned`,"
					+ " MAX(CASE WHEN status = 'LocalQA Assigned' THEN loads ELSE 0 END) `LocalQAAssigned`,"
					+ " MAX(CASE WHEN status = 'GlobalQA Assigned' THEN loads ELSE 0 END) `GlobalQAAssigned`,"
					+ " MAX(CASE WHEN status = 'Coding InProgress' THEN loads ELSE 0 END) `CodingInProgress`,"
					+ " MAX(CASE WHEN status = 'LocalQA InProgress' THEN loads ELSE 0 END) `LocalQAInProgress`,"
					+ " MAX(CASE WHEN status = 'GlobalQA InProgress' THEN loads ELSE 0 END) `GlobalQAInProgress`,"
					+ " MAX(CASE WHEN status = 'Local CNR' THEN loads ELSE 0 END) `LocalCNR`,"
					+ " MAX(CASE WHEN status = 'Global CNR' THEN loads ELSE 0 END) `GlobalCNR`,"
					+ " MAX(CASE WHEN status = 'Local Audit' THEN loads ELSE 0 END) `LocalAudit`,"
					+ " MAX(CASE WHEN status = 'Global Audit' THEN loads ELSE 0 END) `GlobalAudit`,"
					+ " MAX(CASE WHEN status = 'Audited' THEN loads ELSE 0 END) `Audited`,"
					+ " MAX(CASE WHEN status IS NULL THEN loads ELSE 0 END) `open`"
					+ " FROM ( SELECT cd.client_name as clientName, cs.chart_type as chartType, wl.status as status, count(*) as loads"
					+ " from worklist wl, chart_specialization cs, client_details cd "
					+ " where wl.updated_date >= '"
					+ fromDate
					+ "' AND wl.updated_date < '"
					+ toDate
					+ "' AND "
					+ " cs.id= wl.chart_spl AND cd.id = wl.client_id GROUP BY clientName, chartType, status) aa GROUP BY clientName,chartType";

			final Query query = session.createSQLQuery(sqlQuery);
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;

	}

	@Override
	public Set<ReviewWorklist> getReviewWorklistsByWorklistId(final long worklistId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final WorkListItem item = (WorkListItem) session.get(WorkListItem.class, worklistId);
			return item.getReviewWorkLists();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public boolean deleteReviewWorklist(final ReviewWorklist reviewWorklist) {
		boolean flag = false;
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.delete(reviewWorklist);
			txn.commit();
		} catch (final Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return flag;
	}

	@Override
	public WorkListItem normalUpdate(WorkListItem workItem) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.update(workItem);

			session.flush();
			logger.debug("WorkItem has been updated......");
			txn.commit();
			return workItem;
		} catch (Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	public boolean updateWorkListItems(final WorkListItem workListItem) {
		boolean flag = false;
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.saveOrUpdate(workListItem);
			txn.commit();
			flag = true;
		} catch (final Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return flag;
	}

	@Override
	public boolean deleteWorklistItems(final List<Long> worklistIds) {
		boolean flag = false;
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			for (Long id : worklistIds) {
				WorkListItem workItem = (WorkListItem) session.get(WorkListItem.class, id);
				session.delete(workItem);
			}
			txn.commit();
			flag = true;
		} catch (final Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return flag;
	}

	public List<WorkListItem> getWorkListItemByChartSpecializationIdsAndEMValueRange(
			final List<String> specializationIds, final Double emStart, final Double emEnd) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session
					.createQuery("SELECT p FROM WorkListItem p WHERE p.chartSpl IN(:specializationIds)"
							+ " AND p.effortMetric (BETWEEN :emStart AND :emEnd");
			query.setParameterList("specializationIds", specializationIds);
			query.setParameter("emstart", emStart);
			query.setParameter("emEnd", emEnd);
			return (List<WorkListItem>) query.list();
		} catch (final HibernateException he) {
			logger.error("Exception ON getWorkListItemByChartSpecializationIds  : " + he);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	public List<WorkListItem> getAutoEmUnProcessedItems() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session
					.createQuery("SELECT p FROM WorkListItem p WHERE p.isUsedForAutoEMProcess =:isUsedForAutoEMProcess");
			query.setParameter("isUsedForAutoEMProcess", false);
			return (List<WorkListItem>) query.list();
		} catch (final HibernateException he) {
			logger.error("Exception ON getAutoEmUnProcessedItems  : " + he);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public Boolean resetAutoEmProcessItems() {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			final Query query = session
					.createQuery("UPDATE WorkListItem SET isUsedForAutoEMProcess =:isUsedForAutoEMProcess WHERE status IS NULL");
			query.setParameter("isUsedForAutoEMProcess", false);
			query.executeUpdate();
			txn.commit();
			return true;
		} catch (final HibernateException he) {
			txn.rollback();
			logger.error("Exception ON getAutoEmUnProcessedItems  : " + he);
		} finally {
			closeSession(session);
		}
		return false;
	}
	
	
	public Boolean updateManualAssignedWorkitem(final WorkListItem worklist , final List<Coders> coders) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			
			if(coders != null && coders.size() > 0) {
				for(Coders coder : coders) {
					session.update(coder);
				}
			}
			session.update(worklist);
			txn.commit();
			return true;
		} catch (final HibernateException he) {
			txn.rollback();
			logger.error("Exception ON getAutoEmUnProcessedItems  : " + he);
		} finally {
			closeSession(session);
		}
		return false;
	}
	
}
