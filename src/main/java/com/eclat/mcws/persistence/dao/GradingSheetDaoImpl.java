package com.eclat.mcws.persistence.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.GradingSheet;
import com.eclat.mcws.persistence.entity.WeightageMaster;

@Repository
public class GradingSheetDaoImpl implements GradingSheetDao {
	
	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sessionFactory; 
	
	//Commenting the Second level Cache on queries - for the purpose 0f openShift deployment
	
	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
	@Override
	public List<GradingSheet> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GradingSheet find(Long id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (GradingSheet)session.get(GradingSheet.class, id);
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		
		return null;
	}

	@Override
	public GradingSheet save(GradingSheet newEntry) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.save(newEntry);
			txn.commit();
			return newEntry;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public Boolean saveAll(List<GradingSheet> newEntries) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			for(GradingSheet entry : newEntries){
				session.save(entry);
			}
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		return false;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean updateGradingSheet(final GradingSheet gradingSheet){
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.update(gradingSheet);
			txn.commit();
			return true;
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		return false;
	}
	
	@Override
	public GradingSheet getGradingSheetByWorklistId(final Long worklistId){
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryStr = "SELECT p FROM GradingSheet p WHERE p.worklistId = :worklistId";
			final Query query = session.createQuery(queryStr);
			query.setParameter("worklistId", worklistId);
			return (GradingSheet) query.uniqueResult();
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		
		return null;
	}
	
	@Override
	public List<WeightageMaster> getAllWeightageMasterData() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session.createQuery("SELECT p FROM WeightageMaster p");
			return (List<WeightageMaster>) query.list();
		} catch (final Exception e){
			logger.error("Got Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public List<WeightageMaster> getAllWeightageMasterDataByChartType(final String chartType)  throws Exception {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session.createQuery("SELECT p FROM WeightageMaster p WHERE p.chartType=:chartType");
			query.setParameter("chartType", chartType);
			return (List<WeightageMaster>) query.list();
		} catch (final Exception e) {
			throw e;
		} finally {
			closeSession(session);
		}
	}

}
