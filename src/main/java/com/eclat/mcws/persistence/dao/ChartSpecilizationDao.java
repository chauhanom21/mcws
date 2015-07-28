package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ChartType;

public interface ChartSpecilizationDao extends Dao<ChartSpecilization, Integer> {
	
	List<ChartSpecilization> getChartSpecilizationByNotINIds(final List<Integer> speIds);
	
	public List<ChartType> findAllChartTypes();
	
	public Boolean saveChartSpecialization(String chartType, String specialization);
	
	public List<ChartSpecilization> getAllUniqueChartSpecialization();
	
	/**
	 * 
	 * @param chartType
	 * @param chartSpl
	 * @return
	 */
	public ChartSpecilization getChartSpecilizationByChartTypeAndSpecialization(final String chartType,
			final String chartSpl);
	
	/**
	 * 
	 * @param chartType
	 * @return
	 */
	public List<Integer> getChartSpecilizationIdByChartType(final String chartType);
	
}
