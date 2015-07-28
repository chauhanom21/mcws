package com.eclat.mcws.persistence.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.CodingCharts;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.persistence.entity.UsersClient;

@Repository
public class CoderDaoImpl implements CoderDao {
	
	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private UserDAO userDao;
	
	//Commenting the Second level Cache on queries - for the purpose 0f openShift deployment
	
	@Override
	public List<Coders> findAll() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM Coders");
			return query.list();
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}		
	}

	@Override
	public Coders find(Integer userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Coders coder = (Coders) session.get(Coders.class, userId);	
			if(coder != null){
				final Users user = userDao.find(userId);
				coder.setFirstname(user.getFirstname());
				coder.setLastname(user.getLastname());
				coder.setLocation(user.getLocation());
				coder.setIsAvailable(user.getIsAvailable());
			}
			return coder; 
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}	
	}

	@Override
	public Coders save(Coders newEntry) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.saveOrUpdate(newEntry);
			txn.commit();
			return newEntry;
		} catch (Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}			
	}

	
	@Override
	public Boolean saveAll(List<Coders> newsEntries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}
	
	public boolean saveOrUpdateCodingChart(final CodingCharts codingChart) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn  = session.beginTransaction();
			session.saveOrUpdate(codingChart);
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
	
	
	public CodingCharts getCodingChartById(final long id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Object result = session.get(CodingCharts.class, id);
			if(result != null) {
				return (CodingCharts) result;
			}
			return null;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public CodingCharts getCodingChartByWorklistId(final long worklistId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery  = "SELECT p FROM CodingCharts p WHERE p.worklistId = :worklistId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("worklistId", worklistId);
			final Object result = query.uniqueResult();
			if(result != null){
				return (CodingCharts) result;
			}
		} catch (Exception e) {
			logger.error("Exception :: ", e);
			throw e;
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	public CodingCharts getCodingChartByChartTypeAndWorklistId(final String chartType, final long worklistId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery  = " SELECT p FROM CodingCharts p WHERE p.worklistId = :worklistId" +
					" AND p.chartType = :chartType ";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("worklistId", worklistId);
			query.setParameter("chartType", chartType);
			final Object result = query.uniqueResult();
			if(result != null){
				return (CodingCharts) result;
			}
		} catch (Exception e) {
			logger.error("Exception :: ", e);
			throw e;
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public List<UsersClient> getAllUserClients(){
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UsersClient p";
			final Query query = session.createQuery(selectQuery);
			return (List<UsersClient>) query.list();
			
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public List<UsersClient> getAllUserClientsByClientId(final int clientId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UsersClient p WHERE p.clientId = :clientId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("clientId", clientId);
			return (List<UsersClient>) query.list();
			
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public List<UserCharts> getUsersChartByChartSpeIdAndUserIds(final List<Integer> userIds, final int chartSpeId){
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String sqlQuery = "SELECT * FROM usercharts WHERE chart_specialization_id = :chartSpeId"+
					" AND user_id IN (:user_ids)";
			final Query query = session.createSQLQuery(sqlQuery).addEntity(UserCharts.class);
			query.setParameter("chartSpeId", chartSpeId);
			query.setParameterList("user_ids", userIds);
			return (List<UserCharts>) query.list();
		} catch (final Exception e) {
			logger.error(" Exception : " , e);
		} finally {
			closeSession(session);
		}
		
		return null;
	}
	
	@Override
	public List<Coders> getCodersAndLocalQA() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT p FROM Coders p WHERE p.coder=:isCoder OR p.localQa=:isLocalQA";
			final Query query = session.createQuery(queryStr);
			query.setParameter("isCoder", true);
			query.setParameter("isLocalQA", true);
			return (List<Coders>)query.list();	
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	public void saveOrUpdateNotes(final String notes, final Integer userId) throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "UPDATE Coders p SET notes=:notes WHERE userId=:userId";
			final Query query = session.createQuery(queryStr);
			query.setParameter("userId", userId);
			query.setParameter("notes", notes);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
	}
	
	public List<Integer> getAllRemoteQAIds() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT p.id from Coders p  WHERE p.remoteQa=1";
			final Query query = session.createQuery(queryStr);
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return new ArrayList<Integer>(0);
	}
	
	public BigDecimal getTotalCodersEMCount() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT sum(coder_max_workload) FROM coders";
			final Query query = session.createSQLQuery(queryStr);
			return (BigDecimal) query.uniqueResult();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	public List<Integer> getUserIdsByRole(final boolean coder, final boolean localQA, final boolean remoteQA) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT p.id from Coders p WHERE p.remoteQa=:remoteQA "
					+ " AND p.coder=:coder AND p.localQa=:localQA";
			final Query query = session.createQuery(queryStr);
			if(coder){
				query.setParameter("coder", 1);
				query.setParameter("localQA", 0);
				query.setParameter("remoteQA", 0);
			} else if(localQA){
				query.setParameter("coder", 0);
				query.setParameter("localQA", 1);
				query.setParameter("remoteQA", 0);
			} else if(remoteQA){
				query.setParameter("coder", 0);
				query.setParameter("localQA", 0);
				query.setParameter("remoteQA", 1);
			}
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return new ArrayList<Integer>(0);
	}
	
	@Override
	public Coders getCoderQaDetailByUserId(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from Coders p WHERE p.id = :userId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			final Object result = query.uniqueResult();
			if(result != null)
			return (Coders) result;

		} catch (final Exception e) {
			logger.error("Exception : ", e);
			logger.error(e.getMessage());
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public Boolean resetCoderDailyWorkload() {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			final String selectQuery = "UPDATE Coders SET coderDailyWorkLoad = 0.00 WHERE coder = true";
			final Query query = session.createQuery(selectQuery);
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
	
	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

}
