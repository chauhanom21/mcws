package com.eclat.mcws.report.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.ChartTypes;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.report.bean.CoderTrackingReportBean;
import com.eclat.mcws.report.bean.DailyProductivityBean;
import com.eclat.mcws.report.bean.LocalQADailyProductivityBean;
import com.eclat.mcws.report.bean.LocalQATrackingReportBean;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.DecimalFormatUtils;

@Component
public class ReportsUtility {
	
	private static final String ORHS = "ORHS";
	private static final String MCCG = "MCCG";
	private static final String HC = "HC";
	private static final String PC = "PC";
	private static final String HALIFAX = "Halifax";
	private static final double SIXTY = 60.0;
	private static final int ZERO = 0;
	private static final int ONE = 1;
	
	@Log
	private Logger logger;
	
	@Autowired
	private CoderDao coderDao;
	
	@Autowired
	private ClientDetailsDao clientDetailDao;
	
	public List<DailyProductivityBean> generateCoderProductivityReportbeanList(List<Object[]> entities) {
		
		logger.debug("== > generateCoderProductivityReportbeanList : " +entities.size());
		
		final List<DailyProductivityBean> reportBean = new ArrayList<DailyProductivityBean>();
		final List<Coders> users = coderDao.getCodersAndLocalQA();
		
		final Map<Integer, List<Object[]>> reportMap = new HashMap<Integer, List<Object[]>>();
		
		for(Object[] entity : entities) {
			List<Object[]> entityList = reportMap.get((Integer)entity[0]);
			if(entityList ==  null) {
				entityList = new ArrayList<Object[]>();
			}
			reportMap.put((Integer)entity[0], entityList);
			entityList.add(entity);
		}
		
		if(users != null) {
			for(Coders coder : users) {
				for(Map.Entry<Integer, List<Object[]>> entry : reportMap.entrySet()) {
					if((coder.getUserId()) == entry.getKey().intValue()) {
						logger.debug("== > Calculating For USER : " +coder.getUserId());
						reportBean.add(produceDailyProductivityBean(entry.getValue()));
					}
				}
			}
		}	
		return reportBean;
	}
	
	private DailyProductivityBean produceDailyProductivityBean(List<Object[]> entitySet) {
		
		Integer totalMinsAllCharts = 0;
		
		DailyProductivityBean bean = new DailyProductivityBean();
		
		bean.setEmpcode((String)entitySet.get(0)[11]);
		bean.setEmpname((String)entitySet.get(0)[1] + " " + (String)entitySet.get(0)[2]);
		bean.setLocation((String)entitySet.get(0)[3]);
		
		for(Object[] entity : entitySet) {
			
			if(((String)entity[6]).equalsIgnoreCase(ORHS)) {
				totalMinsAllCharts = constructORHSChartDetails(bean, entity, totalMinsAllCharts);
			}
			else if (((String)entity[6]).equalsIgnoreCase(HC)) {
				totalMinsAllCharts = constructHCChartDetails(bean, entity, totalMinsAllCharts);
			}
			else if (((String)entity[6]).equalsIgnoreCase(MCCG)) {
				totalMinsAllCharts = constructMCCGChartDetails(bean, entity, totalMinsAllCharts);
			}
			else if (((String)entity[6]).equalsIgnoreCase(PC)) {
				totalMinsAllCharts = constructPCChartDetails(bean, entity, totalMinsAllCharts);
			}
			else if (((String)entity[6]).equalsIgnoreCase(HALIFAX)) {
				totalMinsAllCharts = constructHALIFAXChartDetails(bean, entity, totalMinsAllCharts);
			}
		}
		
		Long totalCharts = new Long (bean.getOrhsIPChart() + bean.getOrhsERChart() + bean.getOrhsOPChart() +  bean.getOrhsANCChart()
				 + bean.getOrhsM2Chart() + bean.getOrhsMDACChart() + bean.getOrhsEKGECGChart()  + bean.getOrhsPhyChart()
				 + bean.getHcIPChart() + bean.getHcOPChart() +bean.getHcERChart()+bean.getMccgIPChart()
				 + bean.getPcIPChart()+bean.getPcOPChart() + bean.getHalifaxEDChart() + bean.getHalifaxIPChart());
		
		/*long totalHours = (bean.getOrhsIPChartHrs() + bean.getOrhsERChartHrs() + bean.getHcIPChartHrs() + bean.getHcOPChartHrs()
				+bean.getOrhsOPChartHrs() + bean.getOrhsANCChartHrs() + bean.getOrhsM2ChartHrs()+ bean.getOrhsMDAChartHrs()
				+ bean.getOrhsEKGECGChartHrs()+ bean.getOrhsPhyChartHrs()+bean.getHcERChartHrs()+bean.getMccgIPChartHrs()
				+bean.getPcIPChartHrs()+bean.getPcOPChartHrs() + bean.getHalifaxEDChartHrs() + bean.getHalifaxIPChartHrs());
		*/
		bean.setTotalCharts(totalCharts);
		bean.setTotalHours(DateUtil.convertMinsToHHMM(totalMinsAllCharts));
		if(totalMinsAllCharts <= ZERO){
			totalMinsAllCharts = ZERO;
		}
		if( totalCharts <= ZERO ) {
			totalCharts = (long) ONE;
		}
		logger.debug("  totalMinsAllCharts : "+ totalMinsAllCharts +" totalcharts: "+totalCharts);
		bean.setAvgHours(DateUtil.convertMinsToHHMM(totalMinsAllCharts / totalCharts.intValue()));
		return bean;
	}

	private Integer constructORHSChartDetails(DailyProductivityBean bean, Object[] entity, Integer totalMinsAllCharts) {
		if(((String)entity[7]).equalsIgnoreCase(ChartTypes.IP.toString()) && entity[8] != null) 
		{
			bean.setOrhsIPChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsIPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue(); 
		} 
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.OP.toString()) && entity[8] != null)
		{
			bean.setOrhsOPChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsOPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.ER.toString()) && entity[8] != null) 
		{
			bean.setOrhsERChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsERChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.ANC.toString()) && entity[8] != null) 
		{			
			bean.setOrhsANCChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsANCChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.MDA.toString()) && entity[8] != null) 
		{
			bean.setOrhsMDACChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsMDAChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.EKG_ECG.toString()) && entity[8] != null) 
		{
			bean.setOrhsEKGECGChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsEKGECGChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.M2.toString()) && entity[8] != null) 
		{
			bean.setOrhsM2Chart(((BigInteger)entity[9]).intValue());
			bean.setOrhsM2ChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.Physician.toString()) && entity[8] != null) 
		{
			bean.setOrhsPhyChart(((BigInteger)entity[9]).intValue());
			bean.setOrhsPhyChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		
		return totalMinsAllCharts;
	}
	
	private Integer constructMCCGChartDetails(DailyProductivityBean bean, Object[] entity, Integer totalMinsAllCharts) {
		if(((String)entity[7]).equalsIgnoreCase(ChartTypes.IP.toString()) && entity[8] != null) 
		{
			bean.setMccgIPChart(((BigInteger)entity[9]).intValue());
			bean.setMccgIPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();			
		} 
		return totalMinsAllCharts;
	}
	
	private Integer constructHCChartDetails(DailyProductivityBean bean, Object[] entity, Integer totalMinsAllCharts) {
		if(((String)entity[7]).equalsIgnoreCase(ChartTypes.IP.toString()) && entity[8] != null) 
		{
			bean.setHcIPChart(((BigInteger)entity[9]).intValue());
			//bean.setHcIPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			bean.setHcIPChartHrs(DateUtil.convertMinsToHHMM(Integer.valueOf(((BigDecimal)entity[10]).intValue())));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.OP.toString()) && entity[8] != null) 
		{
			bean.setHcOPChart(((BigInteger)entity[9]).intValue());
			bean.setHcOPChartHrs(DateUtil.convertMinsToHHMM(Integer.valueOf(((BigDecimal)entity[10]).intValue())));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.ER.toString()) && entity[8] != null) 
		{
			bean.setHcERChart(((BigInteger)entity[9]).intValue());
			bean.setHcERChartHrs(DateUtil.convertMinsToHHMM(Integer.valueOf(((BigDecimal)entity[10]).intValue())));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		return totalMinsAllCharts;
	}
	
	private Integer constructPCChartDetails(DailyProductivityBean bean, Object[] entity, Integer totalMinsAllCharts) {
		if(((String)entity[7]).equalsIgnoreCase(ChartTypes.IP.toString()) && entity[8] != null)
		{
			bean.setPcIPChart(((BigInteger)entity[9]).intValue());
			bean.setPcIPChartHrs(DateUtil.convertMinsToHHMM(Integer.parseInt(((BigDecimal)entity[10]).toString())));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		}
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.OP.toString()) && entity[8] != null)
		{
			bean.setPcOPChart(((BigInteger)entity[9]).intValue());
			//bean.setPcOPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		return totalMinsAllCharts;
	}
	
	private Integer constructHALIFAXChartDetails(DailyProductivityBean bean, Object[] entity, Integer totalMinsAllCharts) {
		if(((String)entity[7]).equalsIgnoreCase(ChartTypes.ED.toString()) && entity[8] != null)
		{
			bean.setHalifaxEDChart(((BigInteger)entity[9]).intValue());
			bean.setHalifaxEDChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		else if(((String)entity[7]).equalsIgnoreCase(ChartTypes.IP.toString()) && entity[8] != null)
		{
			bean.setPcOPChart(((BigInteger)entity[9]).intValue());
			bean.setPcOPChartHrs(DateUtil.convertMinsToHHMM(((BigDecimal)entity[10]).intValue()));
			totalMinsAllCharts += ((BigDecimal)entity[10]).intValue();
		} 
		return totalMinsAllCharts;
	}
	
	private int convertMinutesToHours(final int minutes) {
		final double hours = (new Double(minutes) / SIXTY);
		return (int) Math.ceil(hours);
		//return ZERO;
	}
	
	public List<LocalQADailyProductivityBean> generateLocaQAProductivityReportbeanList(List<Object[]> entities) {
		final List<LocalQADailyProductivityBean> reportBeanList = new ArrayList<LocalQADailyProductivityBean>();
		final Map<Object, List<Object[]>> reportMap = new HashMap<Object, List<Object[]>>();
		
		for(Object[] entity : entities) {
			List<Object[]> entityList = reportMap.get(((Integer)entity[0]+(String)entity[4]+(String)entity[5]+(String)entity[6]));
			if(entityList ==  null) {
				entityList = new ArrayList<Object[]>();
			}
			reportMap.put(((Integer)entity[0]+(String)entity[4]+(String)entity[5]+(String)entity[6]), entityList);
			entityList.add(entity);
		}
		
		for(Map.Entry<Object, List<Object[]>> entry : reportMap.entrySet()) {
				logger.debug("== > Calculating For : " +entry.getKey() + " total Items : "+entry.getValue().size());
				reportBeanList.add(produceLocalQADailyProductivityBean(entry.getValue()));
		}
		
		return reportBeanList;
	}

	private LocalQADailyProductivityBean produceLocalQADailyProductivityBean(List<Object[]> entitySet) {
		
		Integer totalMinsAllCharts = 0;
		LocalQADailyProductivityBean reportBean = new LocalQADailyProductivityBean();
	
		reportBean.setEmpcode(CommonOperations.getEmployeeId(entitySet.get(0)[10]));
		reportBean.setEmpname((String)entitySet.get(0)[1] + " " + (String)entitySet.get(0)[2]);
		reportBean.setLocation((String)entitySet.get(0)[3]);
		reportBean.setClientName((String)entitySet.get(0)[4]);
		reportBean.setChartType((String)entitySet.get(0)[5]);
		reportBean.setChartSpecialization((String)entitySet.get(0)[6]);
		
	
		for(Object[] entity : entitySet) {
			String status = (String)entity[7];
			int chartCount = ((BigInteger)entity[8]).intValue();
			String hours = DateUtil.convertMinsToHHMM(((BigDecimal)entity[9]).intValue());
			if(status.equalsIgnoreCase(ChartStatus.LocalCNR.getStatus())){
				reportBean.setCnrFileCount(chartCount);
				reportBean.setCnrHours(hours);
				totalMinsAllCharts += ((BigDecimal)entity[9]).intValue();
			}
			if(status.equalsIgnoreCase(ChartStatus.Completed.getStatus())){
				reportBean.setAuditsFileCount(chartCount);
				reportBean.setAuditsFileHours(hours);
				totalMinsAllCharts += ((BigDecimal)entity[9]).intValue();
			}
			if(status.equalsIgnoreCase(ChartStatus.CompletedNR.getStatus())){
				reportBean.setReviewFilecount(chartCount);
				reportBean.setReviewFileHours(hours);
				totalMinsAllCharts += ((BigDecimal)entity[9]).intValue();
			}
		}
	
		Long totalCharts = new Long (reportBean.getCnrFileCount() + reportBean.getAuditsFileCount() + reportBean.getReviewFilecount());
		//long totalHours = (reportBean.getCnrHours() + reportBean.getAuditsFileHours() + reportBean.getReviewFileHours());
		reportBean.setTotalCharts(totalCharts);
		reportBean.setTotalHours(DateUtil.convertMinsToHHMM(totalMinsAllCharts));
		if(totalMinsAllCharts <= ZERO){
			totalMinsAllCharts = ZERO;
		}
		if( totalCharts <= ZERO ) {
			totalCharts = (long) ONE;
		}
		
		reportBean.setAvgHours(DateUtil.convertMinsToHHMM(totalMinsAllCharts / totalCharts.intValue()));
		return reportBean;
	}
	
	public List<LocalQATrackingReportBean> constructLocaQATrackingReportbeanList(List<Object[]> entities) {
		final List<LocalQATrackingReportBean> reportBeanList = new ArrayList<LocalQATrackingReportBean>();
		
		for(Object[] entity : entities) {
			LocalQATrackingReportBean localQABean = new LocalQATrackingReportBean();
			//List<Object[]> entityList = reportMap.get(((Integer)entity[0]+(String)entity[4]+(String)entity[5]+(String)entity[6]));
			localQABean.setQaFirstName(entity[0].toString());
			localQABean.setQaLastName(entity[1].toString());
			localQABean.setQaEmpCode(CommonOperations.getEmployeeId(entity[2]));
			localQABean.setInCompleteItemsCount(((BigInteger)entity[3]).intValue());
			localQABean.setCompletedItemsCount(((BigInteger)entity[4]).intValue());
			localQABean.setCnrRemoteQAItemsCount(((BigInteger)entity[5]).intValue());
			
			reportBeanList.add(localQABean);
		}
				
		return reportBeanList;
	}
	
	//constructCoderTrackingReportbeanList
	public List<CoderTrackingReportBean> constructCoderTrackingReportbeanList(List<Object[]> entities) {
		final List<CoderTrackingReportBean> reportBeanList = new ArrayList<CoderTrackingReportBean>();
		
		for(Object[] entity : entities) {
			CoderTrackingReportBean coderBean = new CoderTrackingReportBean();
			//List<Object[]> entityList = reportMap.get(((Integer)entity[0]+(String)entity[4]+(String)entity[5]+(String)entity[6]));
			coderBean.setCoderFirstName(entity[0].toString());
			coderBean.setCoderLastName(entity[1].toString());
			coderBean.setCoderEmpCode(CommonOperations.getEmployeeId(entity[2]));
			coderBean.setInCompleteItemsCount(((BigInteger)entity[3]).intValue());
			coderBean.setCompletedItemsCount(((BigInteger)entity[4]).intValue());			
			coderBean.setMiscItemsCount(((BigInteger)entity[5]).intValue());
			coderBean.setCodingInProgressItemsCount(((BigInteger)entity[6]).intValue());
			coderBean.setCnrItemsCount(((BigInteger)entity[7]).intValue());
			
			reportBeanList.add(coderBean);
		}
				
		return reportBeanList;
	}
	
	
	
	
	
public boolean generateMiscellaneousReport(List<Object[]> entities, final String dirPath, String fileName){
		
		boolean isRepGenerated = false;
		XSSFWorkbook workbook = new XSSFWorkbook();
		//create sheet on workbook
		XSSFSheet sheet = workbook.createSheet("misc_report");
		//create data map
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		List<Object[]> records = new ArrayList<Object[]>();
		//Add headers
		records.add(new Object[]{"S.No","Account No","Client","Chart Type","Chart Sepecialization","Admit Date","Discharge Date"
				,"Received Date","Comments"});
		
		try {
			createFileDirectory(dirPath, fileName);
			constructMiscellaneousReportdata(entities, records);
			
			//Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (Object [] objArr : records)
			{
			    Row row = sheet.createRow(rownum++);
			    //Object [] objArr = data.get(key);
			    int cellnum = 0;
			    for (Object obj : objArr)
			    {
			       Cell cell = row.createCell(cellnum++);
			       if(obj instanceof String)
			            cell.setCellValue((String)obj);
			        else if(obj instanceof Integer)
			            cell.setCellValue((Integer)obj);
			    }
			}
			
			//Write the workbook in file system
		    FileOutputStream out = new FileOutputStream(new File(fileName));
		    workbook.write(out);
		    out.close();
		    isRepGenerated = true;
		    logger.debug(" ***** Generated MISC report " + fileName);
		} catch (Exception e) {
			isRepGenerated = false;
			logger.debug("MISC Report Exception : ", e);
		}
		return isRepGenerated;
	}
	
	
	private void constructMiscellaneousReportdata(final List<Object[]> entities, List<Object[]> data) {
		Integer snoCount = 2;
		for(Object[] entity : entities){
			data.add(new Object[]{(snoCount.intValue() - 1), (String)entity[7], (String)entity[0], (String)entity[1],
				(String)entity[2], DateUtil.convertDateToString((Timestamp)entity[3]), 
				DateUtil.convertDateToString((Timestamp)entity[4]), 
				DateUtil.convertDateToString((Timestamp)entity[5]), (String)entity[6]});
			snoCount++;
		}
	}
	
	

	/**
	 *  It creates the required dir to store the newly generate files. 
	 *  And creates new file as well.
	 */
	public void createFileDirectory(final String dirPath, final String filename) {
		File dir = new File(dirPath);
		if (dir.exists()) {
			logger.debug(" Directory : " + dirPath + " is available.");
			
			if(filename != null) {
				File file = new File(filename);
				if (file.exists()) {
					logger.debug("file : " + filename + " is exist.");
				} else {
					try {
						file.createNewFile();
					} catch (IOException e) {
						logger.error("Exception creating new file", e);
					}
					logger.debug(" File : " + filename + " created.");
				}
			}
		} else {
			dir.mkdirs();
			logger.debug(" Directory : " + dirPath + " is created.");
			
			if(filename != null) {
				File file = new File(filename);
				if (file.exists()) {
					logger.debug("file : " + filename + " is exist.");
				} else {
					try {
						file.createNewFile();
					} catch (IOException e) {
						logger.error("Exception creating new file", e);
					}
					logger.debug(" File : " + filename + " created.");
				}
			}
		}
	}
}
