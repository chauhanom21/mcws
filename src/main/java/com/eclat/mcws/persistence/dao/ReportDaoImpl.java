package com.eclat.mcws.persistence.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.entity.WorkListItem;

@Repository
public class ReportDaoImpl implements ReportDao {

	@Log
	private Logger logger;

	@Autowired
	private SessionFactory sessionFactory;
	

	/**
	 * 
	 * @param client
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	@Override
	public List<WorkListItem> getWorklistForClientwiseReport(final Integer[] clientIds, final String fromDate, final String toDate) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = " from WorkListItem where " + " client_id IN(:clientIds) AND updated_date >= '"+ 
			fromDate + "' AND updated_date < '" + toDate + "' order by client_id";
			
			final Query query = session.createQuery(sqlQuery);
			query.setParameterList("clientIds", clientIds);
			// List<WorkListItem> items = query.list();
			logger.info("-----> Loaded The WorkListItem's for ClientwiseReport Data : "+ query.list().size());
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;

	}

	@Override
	public List<Object[]> getPendingInvoiceReportDataByClientAndDateRange(final Integer clientId, final String fromDate, final String toDate) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = "SELECT w.id as id, w.client_id as clientId, w.account_number as account, w.discharged_date as dischargeDate,"
			+ " cs.chart_type as chartType, rw.comment as notes, rw.status as status"
			+ " FROM worklist w, chart_specialization cs, review_worklist rw"
			+ " WHERE rw.status IN (:statusArray) AND w.client_id =:clientId AND w.id=rw.worklist_id AND cs.id = w.chart_spl"
			+ " AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)";
			
			final Query query = session.createSQLQuery(sqlQuery);
			query.setParameterList("statusArray", new Object[]{ChartStatus.MISC.getStatus(), ChartStatus.InComplete.getStatus()});
			query.setParameter("clientId", clientId);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			logger.debug("-----> Loded The PENDING InvoiceData  list size: "+ query.list().size());
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return new ArrayList<Object[]>();

	}
	
	@Override
	public List<Object[]> getCompletedInvoiceReportDataByClientAndDateRange(final Integer clientId, final String fromDate, final String toDate) {
		Session session = null;
		try {
			session = sessionFactory.openSession();

			final String sqlQuery = "SELECT w.id, w.client_id as clientId, w.account_number as account, w.discharged_date as dischargeDate,"
					+ " w.uploaded_date as receivedDate, w.tat as tat, w.los as los, cs.chart_type as chartType,"
					+ " rw.lastupdated_date as completedDate, rw.comment as notes,  rw.status as status"
					+ " FROM worklist w, chart_specialization cs, review_worklist rw"
					+ " WHERE rw.status = :status AND w.client_id =:clientId AND w.id = rw.worklist_id AND cs.id = w.chart_spl"
					+ " AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)";
			
			final Query query = session.createSQLQuery(sqlQuery);
			query.setParameter("status", ChartStatus.Completed.getStatus());
			query.setParameter("clientId", clientId);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			
			logger.debug("-----> Loaded The Completed InvoiceData list size : "+ query.list().size());
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return new ArrayList<Object[]>();

	}
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List
	 */
	@Override
	public List<WorkListItem> generateFilewiseTATReport(final Timestamp fromDate, final Timestamp toDate){
		Session session = null;
		String sqlQuery = null;
		try {
			session = sessionFactory.openSession();
			sqlQuery = " from WorkListItem where (updatedDate BETWEEN :fromDate AND :toDate)";
			final Query query = session.createQuery(sqlQuery);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			logger.debug("+++++=====> Loaded "+ query.list().size() +" The Worklist Items for FilewiseTAT Report ");
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return List
	 */
	@Override
	public List<Object[]> generateUserTypeTrackingReport(final String fromDate, final String toDate, final String userType){
		
		Session session = null;
		String sqlQuery = null;
		try {
			session = sessionFactory.openSession();
			if("LocalQA".equalsIgnoreCase(userType)){
				sqlQuery = "SELECT qaFName, qaLNmae, empId, MAX(CASE WHEN STATUS = 'InComplete' THEN loads ELSE 0 END) `inCompleteCount`, " 
							+ " MAX(CASE WHEN STATUS = 'Completed' OR STATUS = 'CompletedNR' THEN loads ELSE 0 END) `completed_count`, "	       
							+ " MAX(CASE WHEN STATUS = 'Global CNR' THEN loads ELSE 0 END) `Global_CNR_count` "
							+ " FROM ( SELECT u.first_name AS qaFName, u.last_name AS qaLNmae, u.id AS qaCode, codr.employee_id AS empId, "
							+ " rw.status AS STATUS, COUNT(*) AS loads " 
							+ " FROM review_worklist rw, users u, coders codr WHERE rw.lastupdated_date >= '" + fromDate + "' AND rw.lastupdated_date < '" + toDate + "' AND "
							+ " u.id= rw.user_reviewd AND codr.user_id = rw.user_reviewd AND rw.user_role = 'LocalQA' GROUP BY qaCode, STATUS) aa GROUP BY qaCode, qaFName, qaLNmae ";
				
			} else 	if("Coder".equalsIgnoreCase(userType)){
				sqlQuery = "SELECT coderFName, coderLNmae, empId, MAX(CASE WHEN STATUS = 'InComplete' THEN loads ELSE 0 END) `inCompleteCount`, " 
						+ " MAX(CASE WHEN STATUS = 'Completed' OR STATUS = 'CompletedNR' THEN loads ELSE 0 END) `completedCount`, "
						+ " MAX(CASE WHEN STATUS = 'MISC' THEN loads ELSE 0 END) `miscCount`, "
						+ " MAX(CASE WHEN STATUS = 'Coding InProgress' THEN loads ELSE 0 END) `codingInProgressCount`, "
						+ " MAX(CASE WHEN STATUS = 'Local CNR' THEN loads ELSE 0 END) `cnrCount` "		
						+ " FROM ( SELECT u.first_name AS coderFName, u.last_name AS coderLNmae, u.id AS coderCode, codr.employee_id AS empId,"
						+ " rw.status AS STATUS, COUNT(*) AS loads " 
						+ " FROM review_worklist rw, users u, coders codr WHERE rw.lastupdated_date >= '" + fromDate + "' AND rw.lastupdated_date < '" + toDate + "' AND "
						+ " u.id= rw.user_reviewd AND codr.user_id = rw.user_reviewd AND rw.user_role = 'Coder' GROUP BY coderCode, STATUS) aa GROUP BY coderCode, coderFName, coderLNmae ";
			}
			final Query query = session.createSQLQuery(sqlQuery);
			logger.debug("+++++=====> Loaded The Items for " + userType + " TrackingReport Data: "+ query.list().size());
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		
		return null;
	}
	
	@Override
	public List<Object[]> getCoderProductivityDataByDate(final String fromDate, final String toDate, final List<Integer> clientIds) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			
			final String sqlQuery = "select u.id as userId, u.first_name, u.last_name, u.location, w.id as worklistId, rw.status, cd.client_name,"
				+ " c.chart_type, w.chart_spl, count(w.chart_spl), sum(rw.total_work_time_mins), codr.employee_id"
				+ " FROM worklist w INNER JOIN review_worklist rw  INNER JOIN client_details cd ON cd.id = w.client_id" 
				+ " INNER JOIN chart_specialization c ON c.id = w.chart_spl INNER JOIN users u ON u.id = rw.user_reviewd"
				+ " INNER JOIN coders codr ON codr.user_id = rw.user_reviewd WHERE rw.user_role='Coder' AND rw.worklist_id = w.id AND rw.status IN (:statusArray)"
				+ " AND w.client_id IN (:clientIds) AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate) group by c.chart_type, u.id";

			final Query query = session.createSQLQuery(sqlQuery);
			query.setParameterList("statusArray", new Object[]{ChartStatus.Completed.getStatus(), ChartStatus.InComplete.getStatus(),
					ChartStatus.LocalCNR.getStatus()});
			query.setParameterList("clientIds", clientIds);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			logger.debug("-----> Loded Coder Productivity Data : "+ query.list());
			return query.list();
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		
		return new ArrayList<Object[]>();

	}
	
	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	@Override
	public List<Object[]> getLocalQAProductivityDataByDate(final String fromDate, final String toDate)throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = "SELECT u.id as userId, u.first_name, u.last_name, u.location, cd.client_name,"
					+ " cs.chart_type, cs.chart_specialization, rw.previous_status, count(rw.previous_status),"
					+ " sum(rw.total_work_time_mins), codr.employee_id" 
					+ " from worklist w JOIN review_worklist rw ON rw.worklist_id = w.id " 
					+ "	JOIN chart_specialization cs ON cs.id = w.chart_spl JOIN users u ON u.id = rw.user_reviewd"
					+ "	JOIN coders codr ON codr.user_id = rw.user_reviewd JOIN client_details cd ON cd.id = w.client_id WHERE rw.user_role = 'LocalQA' AND"
					+ " rw.previous_status IN (:stausArray) AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)"
					+ " group by cd.client_name, cs.chart_type, cs.chart_specialization, u.id, rw.previous_status order by u.id";
			
			Query query = session.createSQLQuery(sqlQuery);
			query.setParameterList("stausArray", new Object[]{ChartStatus.LocalCNR.getStatus(), ChartStatus.Completed.getStatus(),
					ChartStatus.CompletedNR.getStatus()});
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			return query.list();
		} catch(final Exception e) {
			logger.error("Exception : " , e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public List<Object[]> getUserCNRChartCountByDate(final String fromDate, final String toDate, 
			final String userRole) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = "SELECT u.id as userId, u.first_name, u.last_name, rw.status, count(rw.status), c.employee_id"
					+ " from worklist w JOIN review_worklist rw ON rw.worklist_id = w.id " 
					+ "	JOIN users u ON u.id = rw.user_reviewd JOIN coders c ON c.user_id = rw.user_reviewd"
					+ "	WHERE c.is_coder IN(:isCoder) AND c.is_local_qa = :isLocalQA AND rw.previous_status IN (:previousStatus)"
					+ " AND rw.status IN (:status) AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)"
					+ " group by u.id order by u.id";
			
			Query query = session.createSQLQuery(sqlQuery);
			
			if(userRole.equals("coder")) {
				query.setParameterList("isCoder", new Object[]{1});
				query.setParameter("isLocalQA", 0);
				query.setParameterList("previousStatus",  new Object[]{ChartStatus.NotStarted.getStatus()});
				query.setParameter("status", ChartStatus.LocalCNR.getStatus());
				
			} else if(userRole.equals("qa")) {
				
				query.setParameterList("isCoder", new Object[]{0, 1});
				query.setParameter("isLocalQA", 1);
				query.setParameterList("previousStatus",  new Object[]{ChartStatus.LocalCNR.getStatus(),
						ChartStatus.NotStarted.getStatus()});
				query.setParameterList("status", new Object[]{ChartStatus.GlobalCNR.getStatus(), 
						ChartStatus.Completed.getStatus()});
			}
			
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			
			logger.debug("UserCNRChartCount : " + query.list().size());
			
			return query.list();
		} catch(final Exception e) {
			logger.error("Exception : " , e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public List<Object[]> getCoderCNRChartDetailsByDate(final String fromDate, final String toDate, 
			final Integer userId) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = "SELECT u.id as userId, cd.client_name, cs.chart_type, cs.chart_specialization,"
					+ " rw.status, count(rw.status) from worklist w JOIN review_worklist rw ON rw.worklist_id = w.id"
					+ " JOIN chart_specialization cs ON cs.id = w.chart_spl JOIN users u ON u.id = rw.user_reviewd"
					+ " JOIN client_details cd ON cd.id = w.client_id WHERE rw.user_role IN (:role)"
					+ " AND rw.user_reviewd=:userId AND rw.previous_status = :previousStatus AND rw.status IN (:statusArray)"
					+ "	AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)"
					+ " group by u.id, cd.client_name, cs.chart_type order by cd.client_name";
			
			Query query = session.createSQLQuery(sqlQuery);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			query.setParameter("userId", userId);
			query.setParameterList("role", new Object[]{"Coder"});
			query.setParameter("previousStatus", ChartStatus.NotStarted.getStatus());
			query.setParameterList("statusArray", new Object[]{ChartStatus.LocalCNR.getStatus()});
			
			logger.debug("---->  Loded total records "+ query.list().size() +" for CoderCNRChartDetails for coder: "+userId);
			return query.list();
			
		} catch(final Exception e) {
			logger.error("Exception : " , e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public List<Object[]> getQACNRChartDetailsByDate(final String fromDate, final String toDate, 
			final Integer userId) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			
			final String sqlQuery = "SELECT userId, clientName, chartType,"
					+" MAX(CASE WHEN status = 'Completed' THEN loads ELSE 0 END) `Completed`,"
					+" MAX(CASE WHEN status = 'Global CNR' THEN loads ELSE 0 END) `Global_CNR`"
					+" FROM ( SELECT u.id as userId, cd.client_name as clientName, cs.chart_type as chartType,"
					+" rw.status as status, count(*) as loads from worklist w JOIN review_worklist rw ON rw.worklist_id = w.id"
					+" JOIN chart_specialization cs ON cs.id = w.chart_spl JOIN users u ON u.id = rw.user_reviewd"
					+" JOIN client_details cd ON cd.id = w.client_id"
					+" WHERE rw.user_reviewd = :userId AND rw.previous_status IN (:previousStatus) AND rw.status IN (:stausArray)"
					+" AND (rw.lastupdated_date BETWEEN :fromDate AND :toDate)"
					+" group by u.id, cd.client_name, cs.chart_type, rw.status order by cd.client_name) aa GROUP BY userId, clientName, chartType";
			
			
			Query query = session.createSQLQuery(sqlQuery);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			query.setParameter("userId", userId);
			query.setParameterList("previousStatus",  new Object[]{ChartStatus.LocalCNR.getStatus(),
					ChartStatus.NotStarted.getStatus()});
			query.setParameterList("stausArray", new Object[]{ChartStatus.GlobalCNR.getStatus(), ChartStatus.Completed.getStatus()});
			
			return query.list();
			
		} catch(final Exception e) {
			logger.error("Exception : " , e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public List<Object[]> getUsersQualityReportDataByDateRange(final String fromDate, 
			final String toDate) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			
			final String sqlQuery = "SELECT u.id as userId, u.first_name, u.last_name, c.client_name as clientName,"
					+ " cs.chart_type as chartType, gs.accuracy, avg(gs.accuracy), codr.employee_id"
					+ " from users u JOIN grading_sheet gs ON gs.user_completed = u.id"
					+ " JOIN client_details c ON c.id = gs.client_id JOIN chart_specialization cs ON cs.id = gs.chart_specialization"
					+ " JOIN worklist w ON w.id = gs.worklist_id JOIN coders codr ON codr.user_id = u.id"
					+ "	WHERE w.status IN (:status) AND (w.updated_date BETWEEN :fromDate AND :toDate)"
					+ " group by userId, clientName, chartType";
			
			
			Query query = session.createSQLQuery(sqlQuery);
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			query.setParameterList("status", new Object[]{
					ChartStatus.LocalAudited.getStatus(), ChartStatus.GlobalAudited.getStatus()
			});
			
			logger.debug("==> Total Records Avaialable for User Auditing Reports : " +query.list().size());
			return query.list();
			
		} catch(final Exception e) {
			logger.error("Exception : " , e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public List<Object[]> getMiscellaneousReportDataByDateRange(final String fromDate, 
			final String toDate) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlquery = "SELECT c.client_name, cs.chart_type, cs.chart_specialization,"
					+" w.admitted_date, w.discharged_date, w.uploaded_date, rw.comment, w.account_number"
					+" FROM worklist w JOIN review_worklist rw ON rw.worklist_id = w.id"
					+" JOIN client_details c ON c.id = w.client_id"
					+" JOIN chart_specialization cs ON cs.id = w.chart_spl" 
					+" WHERE w.status = :status AND (w.updated_date BETWEEN :fromDate AND :toDate);";
			Query query = session.createSQLQuery(sqlquery);
			query.setParameter("status", ChartStatus.MISC.getStatus());
			query.setParameter("fromDate", fromDate);
			query.setParameter("toDate", toDate);
			return query.list();
		} catch (HibernateException hexp){
			throw hexp;
		} finally {
			closeSession(session);
		}
	}
}
