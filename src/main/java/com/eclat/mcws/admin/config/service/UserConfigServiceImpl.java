package com.eclat.mcws.admin.config.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.config.dto.ChartSpeDetails;
import com.eclat.mcws.admin.config.dto.UserDetails;
import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.UserDAO;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UserRole;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.persistence.entity.UsersClient;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.comparator.ChartComparator;
import com.eclat.mcws.util.comparator.ClientComparator;
import com.eclat.mcws.util.comparator.UsernameComparator;

@Service
public class UserConfigServiceImpl implements UserConfigService {
	
	private static final String QA = "qa";
	private static final String CODER = "coder";
	private static final Double LOW = 2.0;
	private static final Double MEDIUM = 4.0;
	private static final Double HIGH = 6.0;

	@Log
	private Logger logger;
	
	@Autowired
	private CoderDao coderDao;
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private ClientDetailsDao clientDetailsDao;
	
	@Autowired
	private ChartSpecilizationDao chartSpeDao;
	
	@Autowired
	private AppStartupComponent appStartupComponent;
	
	public List<UserDetails> getAllUserDetails() {
		final List<UserDetails> userDetails = new ArrayList<UserDetails>();
		
		Map<Integer, Coders> users =  appStartupComponent.getCodersMap();
		if(users != null && users.size() > 0) {
			for(Map.Entry<Integer, Coders> entry : users.entrySet()) {
				UserDetails userDetail = new UserDetails();
				userDetail.setUserId(entry.getValue().getUserId());
				userDetail.setUsername(entry.getValue().getUsername());
				userDetail.setFirstname(entry.getValue().getFirstname());
				userDetail.setLastname(entry.getValue().getLastname());
				userDetails.add(userDetail);
			}
		}
		Collections.sort(userDetails, new UsernameComparator());
		return userDetails;
	}
	
	public UserDetails getUserDetailsById(final Integer userId, final boolean loadClientDetail) {
		final Coders coder = coderDao.find(userId);
		if(coder != null) {
			Users user = userDao.find(userId);
			UserDetails userDetail = new UserDetails();
			userDetail.setUserId(user.getId());
			userDetail.setGender(user.getGender());
			userDetail.setUsername(user.getUsername());
			userDetail.setPassword(user.getPassword());
			userDetail.setFirstname(coder.getFirstname());
			userDetail.setLastname(coder.getLastname());
			userDetail.setLocation(coder.getLocation());
			userDetail.setJobLevel(coder.getJobLevel());
			userDetail.setRole(CommonOperations.findRoleOfUser(coder));
			userDetail.setEmployeeId(coder.getEmployeeId());
			userDetail.setNewCoder(coder.getNewCoder());
			userDetail.setCoder(coder.getCoder());
			userDetail.setLocalQA(coder.getLocalQa());
			userDetail.setRemoteQA(coder.getRemoteQa());
			userDetail.setMaxWorkload(coder.getCoderMaxWorkLoad());
			if(loadClientDetail) {
				final Set<UsersClient> userclients = coder.getUserClients();
				List<UsersClient> sortedUsrClients = new ArrayList<>(userclients);
				Collections.sort(sortedUsrClients, new ClientComparator());
				
				if(sortedUsrClients.size() > 0) {
					final List<ClientDetails> clientDetails = new ArrayList<ClientDetails>();
					for(UsersClient userClient : sortedUsrClients) {
						clientDetails.add(clientDetailsDao.find(userClient.getClientId()));
					}
					userDetail.setClientDetails(clientDetails);
				}
				
				final Set<UserCharts> usercharts = coder.getUserCharts();
				List<UserCharts> sortedUsrCharts = new ArrayList<>(usercharts);
				Collections.sort(sortedUsrCharts, new ChartComparator());
				
				if(sortedUsrCharts.size() > 0) {
					final List<ChartSpeDetails> chartSpeDetails = new ArrayList<ChartSpeDetails>();
					for(UserCharts userChart : sortedUsrCharts) {
						ChartSpeDetails chartSpeObj = new ChartSpeDetails();
						ChartSpecilization chartSpe = chartSpeDao.find(userChart.getChartSpecializationId());
						chartSpeObj.setId(chartSpe.getId());
						chartSpeObj.setName(chartSpe.getChartType()+"-"+chartSpe.getChartSpelization());
						chartSpeDetails.add(chartSpeObj);
					} 
					userDetail.setChartSpeDetails(chartSpeDetails);
				}
			}
			return userDetail;
		}
		return null;
	}
	
	public Coders saveOrUpdateUser(final UserDetails userDetails) {
		Coders coder = null;
		Users user = null;
		if(userDetails.getUserId()!= null) {
			coder = coderDao.find(userDetails.getUserId());
			user = userDao.find(userDetails.getUserId());
		} else {
			coder = new Coders();
			coder.setCoderDailyWorkLoad(0.00);
			user = new Users();
		}
		user.setFirstname(userDetails.getFirstname());
		user.setLastname(userDetails.getLastname());
		user.setLocation(userDetails.getLocation());
		user.setGender(userDetails.getGender());
		user.setUsername(userDetails.getUsername());
		user.setPassword(userDetails.getPassword());
		user.setIsAvailable(true);
		final Users userEntity = userDao.save(user); 
		if(userEntity != null && userEntity.getId() > 0) {
			UserRole userRole = new UserRole();
			userRole.setUserId(userEntity.getId());
			if(userDetails.getLocalQA() || userDetails.getRemoteQA()) {
				userRole.setRoles(QA);
			} else {
				userRole.setRoles(CODER);
			}
			userDao.saveOrUpdateUserRole(userRole);
			
			coder.setUserId(userEntity.getId());
			coder.setEmployeeId(userDetails.getEmployeeId());
			if(userDetails.getCoder() != null && userDetails.getCoder()) {
				coder.setCoder(userDetails.getCoder());
			} else {
				coder.setCoder(false);
			}
			if(userDetails.getLocalQA() != null && userDetails.getLocalQA()) {
				coder.setLocalQa(userDetails.getLocalQA());
			} else {
				coder.setLocalQa(false);
			}
			if(userDetails.getRemoteQA() != null && userDetails.getRemoteQA()) {
				coder.setRemoteQa(userDetails.getRemoteQA());
			} else {
				coder.setRemoteQa(false);
			}
			if(userDetails.getNewCoder() != null && userDetails.getNewCoder()) {
				coder.setNewCoder(userDetails.getNewCoder());
			} else {
				coder.setNewCoder(false);
			}
			
			coder.setEmValue(getUserMaxWorkEMValue(userDetails.getJobLevel()));
			coder.setJobLevel(userDetails.getJobLevel());
			coder.setCoderMaxWorkLoad(userDetails.getMaxWorkload());
			coder.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			coderDao.save(coder);
		}
		/* Re-initialized the data*/
		appStartupComponent.postInitializeData();
		return coder;
	}
	
	
	public boolean saveOrUpdateUserClientDetails(final Integer userId, final List<Integer> clientIds) throws Exception {
		Coders user = (coderDao.find(userId));
		return clientDetailsDao.saveOrUpdateUserClientDetails(user, clientIds);
	}
	
	public boolean saveOrUpdateUserChartSpeDetails(final Integer userId, final List<Integer> chartSpeIds) throws Exception {
		Coders user = (coderDao.find(userId));
		return clientDetailsDao.saveOrUpdateUserChartSpeDetails(user, chartSpeIds);
	}
	
	private Double getUserMaxWorkEMValue(final String jobLevel){
		Double emValue = 0.0;
		switch (jobLevel) {
		
		case "Low":
			emValue = LOW;
			break;
		case "Medium":
			emValue = MEDIUM;
			break;
		case "High":
			emValue = HIGH;
			break;
		default:
			emValue = LOW;
			break;
		}
		return emValue;
	}
}
