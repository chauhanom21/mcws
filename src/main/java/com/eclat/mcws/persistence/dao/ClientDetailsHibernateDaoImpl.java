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
import com.eclat.mcws.persistence.entity.ClientChartMapping;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;

@Repository
public class ClientDetailsHibernateDaoImpl implements ClientDetailsDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Log
	private Logger logger;
	
	@Override
	public List<ClientDetails> findAll() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM ClientDetails");
			return query.list();
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}		
	}

	@Override
	public ClientDetails find(Integer id) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			return (ClientDetails) session.get(ClientDetails.class, id);			
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}	
	}

	@Override
	public ClientDetails save(ClientDetails newEntry) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			session.saveOrUpdate(newEntry);		
			return newEntry;
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}			
	}

	@Override
	public Boolean saveAll(List<ClientDetails> newEntries) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			for (ClientDetails item: newEntries) {
				session.save(item);
			}
			//All are saved now commit
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
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}
	
	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	@Override
	public ClientDetails getClientDetailById(int clientId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (ClientDetails)session.get(ClientDetails.class, clientId);
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public ClientDetails getClientByName(String name) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM ClientDetails client where client.name = :name");
			query.setParameter("name", name);
			return (ClientDetails) query.uniqueResult();	
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}	
	}
	
	@Override
	public List<UsersClient> getUsersClientByClientId(Integer clientId) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createSQLQuery("SELECT * FROM userclients where client_id = :clientId")
					.addEntity(UsersClient.class);
			query.setParameter("clientId", clientId);
			return (List<UsersClient>) query.list();	
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}	
		return null;
	}
	
	@Override
	public List<ClientChartMapping> getClientChartsDetailsByClientId(final Integer clientId) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM ClientChartMapping where clientId = :clientId");
			query.setParameter("clientId", clientId);
			return (List<ClientChartMapping>) query.list();	
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}	
		return null;
	}
	
	@Override
	public List<Integer> getAllClientIds() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("SELECT p.id FROM ClientDetails p");
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}	
		return null;
	}
	
	public List<ClientDetails> getClientDetailsNotINClientIds(List<Integer> clientIds) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("SELECT p FROM ClientDetails p WHERE p.id NOT IN(:clientIds)");
			query.setParameterList("clientIds", clientIds);
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}	
		return null;
	}

	public boolean saveOrUpdateUserClientDetails(final Coders coder, final List<Integer> clientIds) throws Exception {
		
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			
			//Delete All user client details by userId
			Query query = session.createSQLQuery("DELETE FROM userclients WHERE user_id=:userId");
			query.setParameter("userId", coder.getUserId());
			query.executeUpdate();
			
			if(clientIds.size() > 0) {
				int count = 1;
				for(Integer clientId : clientIds) {
					UsersClient userClient = new UsersClient();
					userClient.setClientId(clientId);
					userClient.setAuditPercentage(25);
					userClient.setPriority(count);
					userClient.setCoder(coder);
					count++;
					session.saveOrUpdate(userClient);
				}
				txn.commit();
			}
			return true;	
		} catch (Exception e) {
			logger.error("Exception : ", e);
			txn.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}
	
	public boolean saveOrUpdateUserChartSpeDetails(final Coders coder, final List<Integer> chartSpeIds) throws Exception {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			
			//Delete All user Chart Specialization details by userId
			Query query = session.createSQLQuery("DELETE FROM usercharts WHERE user_id=:userId");
			query.setParameter("userId", coder.getUserId());
			query.executeUpdate();
			
			int count = 1;
			for(Integer speId : chartSpeIds) {
				UserCharts userChart = new UserCharts();
				userChart.setChartSpecializationId(speId);
				userChart.setCoder(coder);
				userChart.setPriority(count);
				count++;
				session.saveOrUpdate(userChart);
			}
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
	
	
}
