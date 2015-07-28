package com.eclat.mcws.admin.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.UserDAO;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.Users;

@Component
public class AppStartupComponent {

	@Autowired
	private ChartSpecilizationDao chartSplDao;

	@Autowired
	private CoderDao coderDao;

	@Autowired
	private UserDAO userDao;

	@Log
	private Logger logger;

	private Map<String, Integer> chartSplMap;
	private Map<Integer, Coders> codersMap;
	private Map<Integer, ChartSpecilization> chartSplObjMap;

	// @Autowired
	// private CacheManager cacheManager;

	@PostConstruct
	public void postInitializeData() {

		List<ChartSpecilization> listChatSpls = getChartSpl();
		// Cache cache = cacheManager.getCache("chartSpl");
		Map<String, Integer> chartSplMap = new HashMap<String, Integer>();
		for (ChartSpecilization chartSpl : listChatSpls) {
			chartSplMap.put(chartSpl.getChartType() + "-" + chartSpl.getChartSpelization(), chartSpl.getId());
		}

		this.setChartSplMap(chartSplMap);
		logger.debug(" Initial setup for all the chart_specilizations  into a Map : " + chartSplMap);
		// ** Setting All coders data Map
		final List<Coders> coders = coderDao.findAll();
		final List<Users> users = userDao.findAll();
		final Map<Integer, Coders> codersMap = new HashMap<Integer, Coders>();
		for (Coders coder : coders) {
			for (Users user : users) {
				if (user.getId() == coder.getUserId()) {
					coder.setUsername(user.getUsername());
					coder.setFirstname(user.getFirstname());
					coder.setLastname(user.getLastname());
					coder.setUserRole(user.getUserRole());
					coder.setLocation(user.getLocation());
					coder.setIsAvailable(user.getIsAvailable());
				}
			}
			codersMap.put(coder.getUserId(), coder);
		}
		this.setCodersMap(codersMap);

		// ** Setting all chart specialization to map
		final List<ChartSpecilization> chartSpls = chartSplDao.findAll();
		final Map<Integer, ChartSpecilization> chartSplObjMap = new HashMap<Integer, ChartSpecilization>();
		for (ChartSpecilization chartSpl : chartSpls) {
			chartSplObjMap.put(chartSpl.getId(), chartSpl);
		}
		this.setChartSplObjMap(chartSplObjMap);

	}

	public List<ChartSpecilization> getChartSpl() {
		return chartSplDao.findAll();
	}

	public Map<String, Integer> getChartSplMap() {
		return chartSplMap;
	}

	public void setChartSplMap(Map<String, Integer> chartSplMap) {
		this.chartSplMap = chartSplMap;
	}

	public Map<Integer, Coders> getCodersMap() {
		return codersMap;
	}

	public void setCodersMap(Map<Integer, Coders> codersMap) {
		this.codersMap = codersMap;
	}

	public Map<Integer, ChartSpecilization> getChartSplObjMap() {
		return chartSplObjMap;
	}

	public void setChartSplObjMap(Map<Integer, ChartSpecilization> chartSplObjMap) {
		this.chartSplObjMap = chartSplObjMap;
	}
}
