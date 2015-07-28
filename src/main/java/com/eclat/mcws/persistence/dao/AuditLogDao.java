package com.eclat.mcws.persistence.dao;

import java.util.List;
import java.util.Set;

import com.eclat.mcws.persistence.entity.AuditLog;
import com.eclat.mcws.persistence.entity.AuditLogDetails;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.GradingSheet;
import com.eclat.mcws.persistence.entity.WorkListItem;

public interface AuditLogDao {

	boolean saveOrUpdateAuditLog(AuditLog auditLog) throws Exception;
	
	public boolean saveAuditLogDetails(AuditLog auditLog, Set<AuditLogDetails> auditLogDetails) throws Exception;
	
	public boolean saveOrUpdateAuditLogDetails(AuditLogDetails auditLogDetail) throws Exception;
	
	public boolean updateWorklistAndAuditLogDetails(AuditLogDetails auditLogDetail, 
			WorkListItem worklistItem, Integer auditorId, String auditStatus) throws Exception;
	
	public AuditLogDetails getAuditLogDetailsById(Long id) throws Exception;
	
	public AuditLogDetails getAuditLogDetailsByWorklistId(Long worklistId) throws Exception;
	
	public AuditLogDetails getAuditLogDetailsByClientAndCoderId(Integer clientId, Integer coderId) throws Exception;
	
	public boolean saveAuditDataAndUpdateAuditLogAndWorklistAndUserWorkload(GradingSheet gradingSheet,
			Coders coder) throws Exception;
	
	/**
	 * 
	 * @param clientId, user
	 * @return List<AuditLogDetails>
	 * @throws Exception
	 */
	public List<AuditLogDetails> getAuditLogDetailsByClientId(Integer clientId, Coders user) throws Exception;
}
