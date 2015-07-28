package com.eclat.mcws.admin.config.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eclat.mcws.admin.config.dto.ChartSpeDetails;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ChartType;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;

@Service
public class ClientConfigServiceImpl implements ClientConfigService {

	@Autowired
	private ChartSpecilizationDao chartSpeDao;
	
	@Autowired
	private CoderDao coderDeo;
	
	@Autowired
	private ClientDetailsDao clientDetailDao;
	
	@Override
	public List<ChartSpeDetails> getAllChartSpecializationDetails() {
		
		final List<ChartSpeDetails> chartSpeDetails = new ArrayList<ChartSpeDetails>();
		final List<ChartSpecilization> chartSpeList = chartSpeDao.findAll();
		if(chartSpeList !=null && chartSpeList.size() > 0) {
			for(ChartSpecilization chartSpec : chartSpeList){
				ChartSpeDetails chartSpeDetail = new ChartSpeDetails();
				chartSpeDetail.setId(chartSpec.getId());
				chartSpeDetail.setName(chartSpec.getChartType()+"-"+chartSpec.getChartSpelization());
				chartSpeDetails.add(chartSpeDetail);
			}
		}
		return chartSpeDetails;
	}
	
	@Override
	public List<ChartType> getAllChartTypes() {
		
		final List<ChartType> chartTypesList = chartSpeDao.findAllChartTypes();
		if(chartTypesList !=null && chartTypesList.size() > 0) {
			return chartTypesList;
		}
		return null;
	}
	public List<ChartSpeDetails> getUserNotMappedChartSpecializationDetails(final Integer userId) {
		final List<ChartSpeDetails> chartSpeDetails = new ArrayList<ChartSpeDetails>();
		Coders coder = coderDeo.find(userId);
		final Set<UserCharts> userCharts = coder.getUserCharts();
		
		List<ChartSpecilization> chartSpeList = null;
		if(userCharts != null && userCharts.size() > 0) {
			final List<Integer> speIds = new ArrayList<Integer>();
			for(UserCharts usrChart : userCharts) {
				speIds.add(usrChart.getChartSpecializationId());
			}
			chartSpeList = chartSpeDao.getChartSpecilizationByNotINIds(speIds);
		} else {
			chartSpeList = chartSpeDao.findAll();
		}
		
		if(chartSpeList != null) {
			for(ChartSpecilization chartSpe : chartSpeList) { 
				ChartSpeDetails chartSpeObj = new ChartSpeDetails();
				chartSpeObj.setId(chartSpe.getId());
				chartSpeObj.setName(chartSpe.getChartType()+"-"+chartSpe.getChartSpelization());
				chartSpeDetails.add(chartSpeObj);
			}
		} 
		
		return chartSpeDetails;
	}
	
	public List<ClientDetails> loadUserNotMappedClientDetails(final Integer userId) {
		List<ClientDetails> clientDetails = new ArrayList<ClientDetails>();
		Coders coder = coderDeo.find(userId);
		
		final Set<UsersClient> userClients = coder.getUserClients();
		
		if(userClients != null && userClients.size() > 0) {
			List<Integer> clientIds = new ArrayList<Integer>();
			for(UsersClient usersClient :userClients) {
				clientIds.add(usersClient.getClientId());
			}
			clientDetails = clientDetailDao.getClientDetailsNotINClientIds(clientIds);
		} else {
			clientDetails = clientDetailDao.findAll();
		}
		return clientDetails;
	}
	
	public Boolean saveChartSpecialization(String chartType, String specialization) throws Exception {
		Boolean flag = false;
		try{
			flag = chartSpeDao.saveChartSpecialization(chartType, specialization);
		}catch(Exception ex) {
			throw ex;
		}
		return flag;
	}

}
