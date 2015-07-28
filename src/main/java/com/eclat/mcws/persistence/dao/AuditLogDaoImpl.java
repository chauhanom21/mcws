package com.eclat.mcws.persistence.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartAuditStatus;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.entity.AuditLog;
import com.eclat.mcws.persistence.entity.AuditLogDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.GradingSheet;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.CommonOperations;

@Service
public class AuditLogDaoImpl implements AuditLogDao {

	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sessionFactory; 
	
	@Autowired
	CoderDao coderDao;
	
	@Autowired
	WorkListDao workListDao;
	
	private static final int ZERO = 0;
	private static final int ONE = 1;
	
	@Override
	public boolean saveOrUpdateAuditLog(AuditLog auditLog) throws Exception {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.saveOrUpdate(auditLog);
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	public boolean saveAuditLogDetails(AuditLog auditLog, Set<AuditLogDetails> auditLogDetails) throws Exception {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			
			Query query = session.createQuery("DELETE FROM AuditLogDetails WHERE id > 0 AND status=:status");
			query.setParameter("status", ChartAuditStatus.OPEN.toString());
			query.executeUpdate();
			
			session.saveOrUpdate(auditLog);
			int count = 0;
			for(AuditLogDetails auditLogDetail : auditLogDetails) {
				count++;
				auditLogDetail.setAuditLog(auditLog);
				session.saveOrUpdate(auditLogDetail);
				if(count > 50) {
					session.flush();
					count = 0;
				}
			}
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	public boolean saveOrUpdateAuditLogDetails(AuditLogDetails auditLogDetail) throws Exception {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.saveOrUpdate(auditLogDetail);
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	@Transactional
	public boolean updateWorklistAndAuditLogDetails(AuditLogDetails auditLogDetail, 
			WorkListItem workListItem, Integer auditorId, String auditStatus) throws Exception {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			
			final ReviewWorklist reviewWorklist = new ReviewWorklist();
			Coders auditor = coderDao.find(auditorId);
			reviewWorklist.setUserReviewd(auditor.getUserId());
			reviewWorklist.setUserRole(CommonOperations.getUserReviewedRole(auditor, false));
			reviewWorklist.setStatus(auditStatus);
			reviewWorklist.setPreviousStatus(workListItem.getStatus());
			reviewWorklist.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			reviewWorklist.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			reviewWorklist.setTotalTimeTaken(0);
			reviewWorklist.setIsTempData(false);
			workListItem.setCoderId(auditor.getUserId());
			workListItem.setStatus(auditStatus);
			workListItem.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			reviewWorklist.setWorkListItem(workListItem);
			session.save(reviewWorklist);
			
			session.update(auditLogDetail);
			
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	public AuditLogDetails getAuditLogDetailsById(Long id) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (AuditLogDetails)session.get(AuditLogDetails.class, id);
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	@Override
	public AuditLogDetails getAuditLogDetailsByWorklistId(Long worklistId) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("SELECT p FROM AuditLogDetails p WHERE p.worklistId=:worklistId"
					+ " AND p.status=:status");
			query.setParameter("worklistId", worklistId);
			query.setParameter("status", ChartAuditStatus.INPROGRESS.toString());
			query.setMaxResults(1);
			return (AuditLogDetails)query.uniqueResult();
			
		} catch(final Exception e) {
			logger.error(" Exception Loading AuditLog for client and coder : ::: " , e);
			throw e;
		} finally{
			closeSession(session);
		}
	}
	
	@Override
	public AuditLogDetails getAuditLogDetailsByClientAndCoderId(Integer clientId, Integer coderId) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("SELECT p FROM AuditLogDetails p WHERE p.auditClientId=:clientId"
					+ " AND p.auditCoderId=:coderId AND p.status=:status");
			query.setParameter("clientId", clientId);
			query.setParameter("coderId", coderId);
			query.setParameter("status", ChartAuditStatus.OPEN.toString());
			query.setMaxResults(1);
			return (AuditLogDetails)query.uniqueResult();
			
		} catch(final Exception e) {
			logger.error(" Exception Loading AuditLog for cleint and coder : ::: " , e);
		}
		return null;
	}
	
	@Override
	@Transactional
	public boolean saveAuditDataAndUpdateAuditLogAndWorklistAndUserWorkload(GradingSheet gradingSheet,
			 Coders coder) throws Exception {
		
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.save(gradingSheet);
			AuditLogDetails auditLogDetail = getAuditLogDetailsByWorklistId(gradingSheet.getWorklistId());
			if(auditLogDetail != null) {
				auditLogDetail.setAuditDataId(gradingSheet.getId());
				auditLogDetail.setStatus(ChartAuditStatus.COMPLETED.toString());
				session.update(auditLogDetail);
			}
			
			String auditStatus = ChartStatus.GlobalAudited.getStatus();
			if(coder.getLocalQa()){
				auditStatus = ChartStatus.LocalAudited.getStatus();
			}
			
			final ReviewWorklist reviewWorklist = constructReviewWorklist(gradingSheet.getWorklistId(), 
					coder.getUserId(), auditStatus, gradingSheet.getGradingSheetData());
			
			session.update(reviewWorklist);
			
			session.update(confCompletedWorkloadOfCoder(gradingSheet.getWorklistId(), coder.getUserId()));
			
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	
	private ReviewWorklist constructReviewWorklist(long worklistId, int auditorId, String status, String auditingDetails) throws Exception {
		
		final ReviewWorklist reviewWorklist = workListDao.getReviewWorklistByUserIdAndWorklistId(auditorId, worklistId);
		if(reviewWorklist != null) {
			reviewWorklist.setStatus(status);
			reviewWorklist.setCodingDetails(auditingDetails);
			reviewWorklist.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			reviewWorklist.setTotalTimeTaken(getAuditTimeTakenByAuditor(reviewWorklist));
			reviewWorklist.getWorkListItem().setAudited(true);
			reviewWorklist.getWorkListItem().setAuditDate(new Timestamp(System.currentTimeMillis()));
			reviewWorklist.getWorkListItem().setStatus(status);
			reviewWorklist.getWorkListItem().setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			return reviewWorklist;
		} else {
			throw new RuntimeException("No Records found in review_worklist table for worklist Id :: " +worklistId);
		}
	}
	
	private int getAuditTimeTakenByAuditor(final ReviewWorklist reviewWorklist){
		Long millis = 0L;
		Integer spent_mins = reviewWorklist.getTotalTimeTaken();
		
		if( null != reviewWorklist.getWorkStartTime() )
			millis = System.currentTimeMillis() - reviewWorklist.getWorkStartTime().getTime();
		else
			millis = System.currentTimeMillis() - reviewWorklist.getCreatedDate().getTime();
					
		return (millis.intValue()/(60 * 1000) + 1 + spent_mins);
	}
	
	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
	private Coders confCompletedWorkloadOfCoder(final Long worklistId, final int userId) {
			final WorkListItem worklistItem = workListDao.find(worklistId);
			Coders coder = coderDao.find(userId);
			Date updateDate = null;
			if(coder.getUpdatedDate() != null) {
				updateDate = new Date(coder.getUpdatedDate().getTime());
				Calendar cal = new GregorianCalendar();
				cal.set(Calendar.HOUR_OF_DAY, ZERO);
				cal.set(Calendar.MINUTE, ZERO);
				cal.set(Calendar.SECOND, ONE); 
				if(updateDate.after(new Date(cal.getTimeInMillis()))) {
					coder.setCompleteLoad(coder.getCompleteLoad() + worklistItem.getEffortMetric());
				} else {
					coder.setCompleteLoad(worklistItem.getEffortMetric());
				}
			} else {
				coder.setCompleteLoad(worklistItem.getEffortMetric());
			}
			coder.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			return coder;
	}

	public List<AuditLogDetails> getAuditLogDetailsByClientId(Integer clientId, Coders coder) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			
			final String sqlQuery = "SELECT * FROM audit_log_details au where au.status=:status AND au.client_id=:clientId"
					+ " AND au.coder_id IN(select user_id from coders where is_coder=:isCoder OR is_local_qa=:isLocaQA)"
					+ " AND au.coder_id !=:coderId group by coder_id";
			
			Query query = session.createSQLQuery(sqlQuery).addEntity(AuditLogDetails.class);
			query.setParameter("clientId", clientId);
			query.setParameter("status", ChartAuditStatus.OPEN.toString());
			query.setParameter("coderId", coder.getUserId());
			
			final String userRole = CommonOperations.findRoleOfUser(coder);
			if(userRole.equalsIgnoreCase(UserRoles.LocalQA.toString())) {
				query.setParameter("isCoder", true);
				query.setParameter("isLocaQA", false);
			} else if(userRole.equalsIgnoreCase(UserRoles.RemoteQA.toString())) {
				query.setParameter("isCoder", false);
				query.setParameter("isLocaQA", true);
			}
			
			return (List<AuditLogDetails>)query.list();
			
		} catch(final Exception e) {
			logger.error(" Exception Loading AuditLog for cleint and coder : ::: " , e);
		} finally {
			closeSession(session);
		}
		return null;
	}
}
