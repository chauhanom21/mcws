package com.eclat.mcws.persistence.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.entity.UserRole;
import com.eclat.mcws.persistence.entity.Users;

public class UserDAOImpl implements UserDAO {

	@Log
	private Logger logger;

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Users loadUserById(final Integer userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (Users) session.get(Users.class, userId);
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public Users loadUserByUsername(final String username) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from Users p WHERE p.username = :username";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("username", username);
			final Object result = query.uniqueResult();
			if (result != null) {
				return (Users) result;
			}

		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public UserRole loadUserRoleByUserId(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryString = "SELECT p from UserRole WHERE p.userId = : userId";
			final Query query = session.createQuery(queryString);
			query.setParameter("userId", userId);
			final Object result = query.uniqueResult();
			if (result != null) {
				return (UserRole) result;
			}
		} catch (final Exception e) {
			logger.error("Exception: ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public void saveOrUpdateUserRole(final UserRole userRole) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			session.saveOrUpdate(userRole);
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<Users> getAllUsers() {
		Session session = null;
		final String selectQuery = "select p from Users p";
		try {
			session = sessionFactory.openSession();
			final Query query = session.createQuery(selectQuery);
			final Object result = query.list();
			if (result != null) {
				return (List<Users>) result;
			}
		} catch (final Exception e) {
			logger.error("Exception : ", e);
		} finally {
			closeSession(session);
		}
		return null;
	}

	@Override
	public Users getUserById(int userId) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String selectQuery = "select p from Users p WHERE p.id = :userId";
			final Query query = session.createQuery(selectQuery);
			query.setParameter("userId", userId);
			final Object result = query.uniqueResult();
			if (result != null) {
				return (Users) result;
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

	@Override
	public Users find(Integer id) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			return (Users) session.get(Users.class, id);
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public List<Users> findAll() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM Users");
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public Users save(Users newEntry) {
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
	public List<Users> getAllAvailableUsers() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM Users WHERE isAvailable = true");
			return query.list();
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public Boolean updateUserAvailability(final Integer userId, final Boolean isAvailable) {
		Session session = null;
		Transaction txn = null;
		try {
			session = sessionFactory.openSession();
			txn = session.beginTransaction();
			final Query query = session.createQuery("UPDATE Users SET isAvailable=:isAvailable WHERE id=:userId");
			query.setParameter("isAvailable", isAvailable);
			query.setParameter("userId", userId);
			query.executeUpdate();
			txn.commit();
			return true;
		} catch (final HibernateException he) {
			logger.error("Exp On updateUserAvailability :: ", he);
			throw he;
		} finally {
			closeSession(session);
		}
	}

	@Override
	public Boolean saveAll(List<Users> newsEntries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}
}
