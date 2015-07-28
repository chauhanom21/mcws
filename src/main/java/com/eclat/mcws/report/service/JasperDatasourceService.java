package com.eclat.mcws.report.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.report.bean.DailyProductivityBean;
import com.eclat.mcws.report.bean.ReportBean;

@Service
public class JasperDatasourceService {

	@Log
	private Logger logger;
	
	@Autowired
	private WorkListDao worklistDao;
	
	/**
	 * Returns a data source that's wrapped within {@link JRDataSource}
	 * @return
	 */
	public JRDataSource getDataSource(final String fromDate, final String toDate, final String client, final String chartType) {
		final ArrayList<ReportBean> reportBeanList = new ArrayList<ReportBean>();

		final List<Object[]> entities = worklistDao.getWorklistDataForSupervisorReport(fromDate, toDate);
		for (Object[] entity : entities) {
			constructReportBeanArrayList(entity, reportBeanList);
		}
		
		ArrayList<ReportBean> reports = null;
		if(client != null && client.trim().length() > 0 ) {
			reports = filterReportByClient(reportBeanList, client);
			if(chartType != null && chartType.trim().length() > 0) {
				reports = filterReportByChartType(reports, chartType);
			}
			
			return new JRBeanCollectionDataSource(reports);
		
		} else if(chartType != null && chartType.trim().length() > 0){
			
			reports = filterReportByChartType(reportBeanList, chartType);
			return new JRBeanCollectionDataSource(reports);
		}
		
		// Return wrapped collection
		return new JRBeanCollectionDataSource(reportBeanList);
	}
	
	/**
	 * Returns a data source that's wrapped within {@link JRDataSource}
	 * @return
	 */
	public ArrayList<ReportBean> getReportBeanList(final String fromDate, final String toDate, final String client, final String chartType) {
		final ArrayList<ReportBean> reportBeanList = new ArrayList<ReportBean>();

		final List<Object[]> entities = worklistDao.getWorklistDataForSupervisorReport(fromDate, toDate);
		for (Object[] entity : entities) {
			constructReportBeanArrayList(entity, reportBeanList);
		}
		
		ArrayList<ReportBean> reports = null;
		if(client != null && client.trim().length() > 0 ) {
			reports = filterReportByClient(reportBeanList, client);
			if(chartType != null && chartType.trim().length() > 0) {
				reports = filterReportByChartType(reports, chartType);
			}
		} else if(chartType != null && chartType.trim().length() > 0){
			reports = filterReportByChartType(reportBeanList, chartType);
		} else {
			reports = reportBeanList;
		}
		
		// Return ReportBean collection
		return reports;
	}
	
	
	private void constructReportBeanArrayList(Object[] entity,	final ArrayList<ReportBean> reportBeanList) {
		final Map<String, Object> values = new HashMap<String, Object>();
		int count = 0;
		for (int i = 0; i < entity.length; i++) {
			if(i==0){
				values.put("client", entity[0]);
    		}
    		if(i==1){
    			values.put("chartType", entity[1]);
    		}
    		if(i==2){
    			values.put(ChartStatus.InComplete.getStatus(), ((BigInteger)entity[2]).intValue());
    			count = count + ((BigInteger)entity[2]).intValue();
    		}
    		if(i==3){
    			values.put(ChartStatus.Completed.getStatus(), ((BigInteger)entity[3]).intValue());
    			count = count + ((BigInteger)entity[3]).intValue();
    		}
    		if(i==4){
    			values.put(ChartStatus.MISC.getStatus(), ((BigInteger)entity[4]).intValue());
    			count = count + ((BigInteger)entity[4]).intValue();
    		}
    		if(i==5){
    			values.put(ChartStatus.CoderAssigned.getStatus(), ((BigInteger)entity[5]).intValue());
    			count = count + ((BigInteger)entity[5]).intValue();
    		}
    		if(i==6){
    			values.put(ChartStatus.LocalQAAssigned.getStatus(), ((BigInteger)entity[6]).intValue());
    			count = count + ((BigInteger)entity[6]).intValue();
    		}
    		if(i==7){
    			values.put(ChartStatus.GlobalQAAssigned.getStatus(), ((BigInteger)entity[7]).intValue());
    			count = count + ((BigInteger)entity[7]).intValue();
    		}
    		if(i==8){
    			values.put(ChartStatus.CodingInProgress.getStatus(), ((BigInteger)entity[8]).intValue());
    			count = count + ((BigInteger)entity[8]).intValue();
    		}
    		if(i==9){
    			values.put(ChartStatus.LocalQAInProgress.getStatus(),((BigInteger)entity[9]).intValue());
    			count = count + ((BigInteger)entity[9]).intValue();
    		}
    		if(i==10){
    			values.put(ChartStatus.GlobalQAInProgress.getStatus(), ((BigInteger)entity[10]).intValue());
    			count = count + ((BigInteger)entity[10]).intValue();
    		}
    		if(i==11){
    			values.put(ChartStatus.LocalCNR.getStatus(), ((BigInteger)entity[11]).intValue());
    			count = count + ((BigInteger)entity[11]).intValue();
    		}
    		if(i==12){
    			values.put(ChartStatus.GlobalCNR.getStatus(), ((BigInteger)entity[12]).intValue());
    			count = count + ((BigInteger)entity[12]).intValue();
    		}
    		if(i==13){
    			values.put(ChartStatus.LocalAudit.getStatus(), ((BigInteger)entity[13]).intValue());
    			count = count + ((BigInteger)entity[13]).intValue();
    		}
    		if(i==14){
    			values.put(ChartStatus.GlobalAudit.getStatus(), ((BigInteger)entity[14]).intValue());
    			count = count + ((BigInteger)entity[14]).intValue();
    		}
			// if(i==15){
			// values.put(ChartStatus.Audited.getStatus(),
			// ((BigInteger)entity[15]).intValue());
			// count = count + ((BigInteger)entity[15]).intValue();
			// }
    		if(i==16){
    			values.put(ChartStatus.NotStarted.getStatus(), ((BigInteger)entity[16]).intValue());
    			count = count + ((BigInteger)entity[16]).intValue();
    		}
		}
		values.put("count", count);
		
		logger.debug(" ==>  values : "+values.toString());
		logger.debug(" ==>  values : "+values.values());
		ReportBean reportBean = produceReportBeanData((String) values.get("client"), (String) values.get("chartType"),
				(Integer) values.get(ChartStatus.Completed.getStatus()), (Integer) values.get(ChartStatus.InComplete.getStatus()), 
				(Integer) values.get(ChartStatus.MISC.getStatus()), 
				(Integer) values.get(ChartStatus.CoderAssigned.getStatus()), (Integer) values.get(ChartStatus.LocalQAAssigned.getStatus()),
				(Integer) values.get(ChartStatus.GlobalQAAssigned.getStatus()),
				(Integer) values.get(ChartStatus.CodingInProgress.getStatus()), (Integer) values.get(ChartStatus.LocalQAInProgress.getStatus()),
				(Integer) values.get(ChartStatus.GlobalQAInProgress.getStatus()),
				(Integer) values.get(ChartStatus.LocalCNR.getStatus()), (Integer) values.get(ChartStatus.GlobalCNR.getStatus()),
				(Integer) values.get(ChartStatus.LocalAudit.getStatus()), (Integer) values.get(ChartStatus.GlobalAudit.getStatus()),
				(Integer) values.get(ChartStatus.GlobalAudited.getStatus()), 
				(Integer) values.get(ChartStatus.NotStarted.getStatus()));

		reportBeanList.add(reportBean);
	}

	private ReportBean produceReportBeanData(String client, String chartType, int completed, int inComplete,
			int misc, int coderAssigned, int localQAAssigned, int globalQAAssigned, int coderInProgress, 
			int localQAInProgress, int globalQAInProgress, int localCNR, int globalCNR, int localAudit, 
			int globalAudit, int audited, int open) {
		ReportBean reportBean = new ReportBean();

		reportBean.setClient(client);
		reportBean.setChartType(chartType);
		reportBean.setCompleted(completed);
		reportBean.setInComplete(inComplete);
		reportBean.setMisc(misc);
		reportBean.setCoderAssigned(coderAssigned);
		reportBean.setLocalQAAssigned(localQAAssigned);
		reportBean.setGlobalQAAssigned(globalQAAssigned);
		reportBean.setCoderInProgress(coderInProgress);
		reportBean.setLocalQAInProgress(localQAInProgress);
		reportBean.setGlobalQAInProgress(globalQAInProgress);
		reportBean.setLocalCNR(localCNR);
		reportBean.setGlobalCNR(globalCNR);
		reportBean.setLocalAudit(localAudit);
		reportBean.setGlobalAudit(globalAudit);
		reportBean.setAudited(audited);
		reportBean.setOpen(open);
		final Long count = new Long(completed + inComplete + misc + coderAssigned + localQAAssigned + globalQAAssigned +
				coderInProgress + localQAInProgress + globalQAInProgress + localCNR + globalCNR + localAudit + globalAudit 
				+ audited + open);
		reportBean.setCount(count);
		return reportBean;
	}
	
	private ArrayList<ReportBean> filterReportByClient(final ArrayList<ReportBean> reportBeanList, final String client){
		final ArrayList<ReportBean> filteredReport = new ArrayList<ReportBean>();
		for(ReportBean report : reportBeanList){
			if(report.getClient().equals(client)){
				filteredReport.add(report);
			}
		}
		return filteredReport;
	}
	
	private ArrayList<ReportBean> filterReportByChartType(final ArrayList<ReportBean> reportBeanList, final String chartType){
		final ArrayList<ReportBean> filteredReport = new ArrayList<ReportBean>();
		for(ReportBean report : reportBeanList){
			if(report.getChartType().equals(chartType)){
				filteredReport.add(report);
			}
		}
		return filteredReport;
	}
	
	/**
	 * Returns a data source that's wrapped within {@link JRDataSource}
	 * @return
	 */
	public JRDataSource getCoderDailyProductivtyDataSource(List<DailyProductivityBean> prodReportBeanList ) {
		
		// Return wrapped collection
		return new JRBeanCollectionDataSource(prodReportBeanList);
	}
	
}
