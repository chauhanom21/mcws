package com.eclat.mcws.admin.service;

import java.io.InputStream;
import java.util.List;

import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.util.rest.Response;

public interface ExcelFileHandlingService {
	
//	List<WorkListItem> getWorkListItems(InputStream stream,	String clientName);
	
	Response<List<WorkListItem>> getWorkListItems( InputStream inputStream, String clientName, Boolean isIncomplete, Response<List<WorkListItem>> responseRef);
	
	Boolean saveAllListItems(List<WorkListItem> items);

	List<ClientDetails> getClients();
	
	Boolean updateIncompleteWorkItems(List<WorkListItem> items);
}
