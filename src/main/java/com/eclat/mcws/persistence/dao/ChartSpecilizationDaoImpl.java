package com.eclat.mcws.persistence.dao;

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
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ChartType;

@Repository
public class ChartSpecilizationDaoImpl implements ChartSpecilizationDao {
	
	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<ChartSpecilization> findAll() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM ChartSpecilization");
			return (List<ChartSpecilization>)query.list();
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}				
	}

	@Override
	public ChartSpecilization find(Integer id) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			return (ChartSpecilization) session.get(ChartSpecilization.class, id);			
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}	
	}

	@Override
	public ChartSpecilization save(ChartSpecilization newsEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean saveAll(List<ChartSpecilization> newsEntries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}
	
	public List<ChartSpecilization> getChartSpecilizationByNotINIds(final List<Integer> speIds) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("SELECT p FROM ChartSpecilization p WHERE p.id NOT IN(:speIds)");
			query.setParameterList("speIds", speIds);
			return query.list();
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
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
	public List<ChartType> findAllChartTypes() {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM ChartType");
			return (List<ChartType>)query.list();
			
		} catch (Exception e) {
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		}				
	}
	
	@Override
	public Boolean saveChartSpecialization(String chartType, String specialization) {
		Session session = null;
		Transaction tx = null;
		Boolean addedChartSpl = false;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			ChartSpecilization cs = new ChartSpecilization();
			cs.setChartType(chartType);
			cs.setChartSpelization(specialization);
			
			session.save(cs);
			
			if( null != chartType ){
				List<ChartType> chartList = findAllChartTypes();			
				Integer count = 0;
				for(ChartType chart : chartList) {
					if( chartType.equalsIgnoreCase(chart.getChartType()))
						count++;				
				}
				if( count == 0) {
					ChartType newChart = new ChartType();
					newChart.setChartType(chartType);
					session.save(newChart);
				}
			}
			
			tx.commit();
			addedChartSpl = true;
		} catch (Exception e) {
			tx.rollback();;
			logger.error("Exception : ", e);
			throw e;
		} finally {
			closeSession(session);
		} 
		return addedChartSpl;
	}

	@Override
	public List<ChartSpecilization> getAllUniqueChartSpecialization() {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final Query query = session.createQuery("SELECT p FROM ChartSpecilization p "
					+ "GROUP BY chartSpelization");
			return query.list();
		} catch (final Exception e){
			logger.error("Get Unique chartSpecialization : ", e);
		} finally{
			closeSession(session);
		}
		return new ArrayList<ChartSpecilization>();
	}
	
	@Override
	public ChartSpecilization getChartSpecilizationByChartTypeAndSpecialization(final String chartType,
			final String chartSpl) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryString = "SELECT p FROM ChartSpecilization p "
					+"WHERE p.chartType=:chartType AND p.chartSpelization=:chartSpl";
			final Query query = session.createQuery(queryString);
			query.setParameter("chartType", chartType);
			query.setParameter("chartSpl", chartSpl);
			return (ChartSpecilization)query.uniqueResult();
		} catch(final Exception e){
			logger.error("Get ChartSpecilization by Chart Type and Spl : " , e);
		} finally{
			closeSession(session);
		}
		return null;
	}
	
	public List<Integer> getChartSpecilizationIdByChartType(final String chartType){
		Session session = null;
		try {
			session = sessionFactory.openSession();
			final String queryString = "SELECT p.id FROM ChartSpecilization p "
					+"WHERE p.chartType=:chartType";
			final Query query = session.createQuery(queryString);
			query.setParameter("chartType", chartType);
			return (List<Integer>)query.list();
		} catch(final Exception e){
			logger.error("Get ChartSpecilization by Chart Type and Spl : " , e);
		} finally {
			closeSession(session);
		}
		return null;
	}
}
