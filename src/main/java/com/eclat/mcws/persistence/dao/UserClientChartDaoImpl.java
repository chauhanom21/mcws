package com.eclat.mcws.persistence.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;

@Repository
public class UserClientChartDaoImpl implements UserClientChartDao {

	@Log
	private Logger logger;

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<UsersClient> getUsersClientDetailByUserId(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UsersClient p WHERE p.userId = :userId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			final Object result = query.list();
			if (result != null) {
				return (List<UsersClient>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public UserCharts getUserChartsById(int id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UserCharts p WHERE p.id = :id";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("id", id);
			final Object result = query.uniqueResult();
			if (result != null) {
				return (UserCharts) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;

	}

	@Override
	public List<UserCharts> getUsersChartsDetailByUserId(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UserCharts p WHERE p.userId = :userId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			final Object result = query.list();
			if (result != null) {
				return (List<UserCharts>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}
	
	@Override
	public List<UserCharts> getUserChartByUserIdAndSpecializationIds(final int userId, 
			final List<Integer> specializationIds) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from UserCharts p WHERE p.userId = :userId"
					+ " AND p.chartSpecializationId IN(:specializationIds)";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			query.setParameterList("specializationIds", specializationIds);
			final Object result = query.list();
			if (result != null) {
				return (List<UserCharts>) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	private void closeSession(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

}
