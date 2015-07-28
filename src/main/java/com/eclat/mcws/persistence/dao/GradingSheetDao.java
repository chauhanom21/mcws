package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.entity.GradingSheet;
import com.eclat.mcws.persistence.entity.WeightageMaster;

public interface GradingSheetDao extends Dao<GradingSheet, Long> {

	boolean updateGradingSheet(final GradingSheet gradingSheet);
	
	GradingSheet getGradingSheetByWorklistId(final Long worklistId);
	
	List<WeightageMaster> getAllWeightageMasterData();
	
	List<WeightageMaster> getAllWeightageMasterDataByChartType(final String chartType)  throws Exception;
}
