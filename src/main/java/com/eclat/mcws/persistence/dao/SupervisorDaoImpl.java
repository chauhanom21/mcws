package com.eclat.mcws.persistence.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;



@Repository
public class SupervisorDaoImpl implements SupervisorDao {
	
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
	public void executeResetCompletedLoadForAll(String userType){
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String selectQuery = null;
			//final String selectQuery = "UPDATE Coders SET completeLoad=0, lastUpdatedDate = '" + new java.sql.Timestamp(new java.util.Date().getTime()).toString() + "'"
			//							+ " WHERE coder=1 AND localQa=1 AND qa=0 ";
			
			// query for remoteQA
			if("remoteQA".equalsIgnoreCase(userType)) {
				selectQuery = "UPDATE coders SET completeLoad=0.0, coder_daily_workload =  0.0, last_update_date = NOW()  WHERE is_local_qa=0 AND is_remote_qa=1";
				logger.debug("Query for ResetCompletedLoadFor RemoteQA :::: " + selectQuery);
			}
			// query for localQA's and Coders
			else {
				selectQuery = "UPDATE coders SET completeLoad=0.0, coder_daily_workload =  0.0, last_update_date = NOW() WHERE is_coder=1 OR is_local_qa=1";
				logger.debug("Query for ResetCompletedLoadFor LocalQA and Coder's :::: " + selectQuery);
			}
			
			final Query query = session.createSQLQuery(selectQuery);
			final Query safeUpdateQuery = session.createSQLQuery("SET SQL_SAFE_UPDATES=0");
			safeUpdateQuery.executeUpdate();
			query.executeUpdate();
			tx.commit();
		} catch (final Exception e) {
			tx.rollback();
			logger.error("Exception : ", e);
			logger.error(e.getMessage());
		} finally {
			closeSession(session);
		}
	}

}
