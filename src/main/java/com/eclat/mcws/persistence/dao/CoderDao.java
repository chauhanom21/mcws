package com.eclat.mcws.persistence.dao;

import java.math.BigDecimal;
import java.util.List;

import com.eclat.mcws.persistence.entity.CodingCharts;
import com.eclat.mcws.persistence.dao.Dao;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserCharts;
import com.eclat.mcws.persistence.entity.UsersClient;

public interface CoderDao extends Dao<Coders, Integer> {

	boolean saveOrUpdateCodingChart(final CodingCharts codingChart);
	
	CodingCharts getCodingChartById(final long id);
	
	CodingCharts getCodingChartByWorklistId(final long long1);
	
	CodingCharts getCodingChartByChartTypeAndWorklistId(final String chartType, final long worklistId);
	
	List<UsersClient> getAllUserClients();
	
	List<UsersClient> getAllUserClientsByClientId(final int clientId);
	
	public List<UserCharts> getUsersChartByChartSpeIdAndUserIds(final List<Integer> userIds, final int chartSpeId);
	
	List<Coders> getCodersAndLocalQA();
	
	public void saveOrUpdateNotes(final String notes, final Integer userId) throws Exception;
	
	public List<Integer> getAllRemoteQAIds(); 
	
	public BigDecimal getTotalCodersEMCount();
	
	public List<Integer> getUserIdsByRole(final boolean coder, final boolean localQA, final boolean remoteQA); 
	
	public Coders getCoderQaDetailByUserId(final int userId);
	
	public Boolean resetCoderDailyWorkload();
	
}
