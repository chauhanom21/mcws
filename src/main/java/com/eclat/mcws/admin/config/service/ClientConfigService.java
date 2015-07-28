package com.eclat.mcws.admin.config.service;

import java.util.List;

import com.eclat.mcws.admin.config.dto.ChartSpeDetails;
import com.eclat.mcws.persistence.entity.ChartType;
import com.eclat.mcws.persistence.entity.ClientDetails;

public interface ClientConfigService {
	/**
	 * 
	 * @return All Chart Specialization details
	 */
	public List<ChartSpeDetails> getAllChartSpecializationDetails();
	
	/**
	 * 
	 * @return All Chart Specialization details
	 */
	public List<ChartSpeDetails> getUserNotMappedChartSpecializationDetails(final Integer userId);
	
	/**
	 * 
	 * @param userId
	 * @return ClientDetails
	 */
	public List<ClientDetails> loadUserNotMappedClientDetails(final Integer userId);
	
	/**
	 * 
	 * @return All ChartType details
	 */
	public List<ChartType> getAllChartTypes();
	
	public Boolean saveChartSpecialization(String chartType, String specialization) throws Exception;
	
}
