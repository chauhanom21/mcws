package com.eclat.mcws.report.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVWriter;
import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.ClientChartMapping;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.bean.ClientwiseReportBean;
import com.eclat.mcws.report.bean.InvoiceReportBean;
import com.eclat.mcws.report.bean.LocalQADailyProductivityBean;
import com.eclat.mcws.report.bean.ReportBean;
import com.eclat.mcws.util.comparator.RevWorkUpdateDateASCComparator;


@Component
public class ClientwiseReportUtility {
	
	private static String COMPLETED = "Completed";
	private static String PENDING = "Pending";
	private static String NOT_STARTED = "Not Started";
	
	@Log
	private Logger logger;
	
	@Autowired
	private ChartSpecilizationDao chartSplDao;
	
	@Autowired
	private ReportsUtility reportUtility;

	@Autowired
	private AppStartupComponent appStartupComponent;
	
	Map<Integer, Coders> codersMap;
	
	@PostConstruct
	public void init(){
		codersMap = appStartupComponent.getCodersMap();
	}
	// generate ClientwiseReportBeanList from worklist data.
	public List<ClientwiseReportBean> generateClientwiseReportBeanList(List<WorkListItem> workItems){
		logger.debug(" workitems size  in generateClientwiseReportBeanList: " + workItems.size());
		
		init();
		
		List<ClientwiseReportBean> reportList = null;
		reportList = new ArrayList<ClientwiseReportBean>();
		Integer count = 1;
		for(WorkListItem item : workItems){
			
			ClientwiseReportBean cReportBean = new ClientwiseReportBean();
			cReportBean.setsNo(count);
			if(null != item.getAccountNumber())
				cReportBean.setAccountNo(item.getAccountNumber());
			if(null != item.getMrNumber())
				cReportBean.setMrNo(item.getMrNumber());
			//service Type
			if(null != item.getServiceType())
				cReportBean.setServiceType(item.getServiceType());
			
			if(null != item.getClientDetails())
			cReportBean.setClient(item.getClientDetails().getName());
			if(null != item.getPatientName())
				cReportBean.setPatientName(item.getPatientName().replace(",", " "));
			if(null != item.getInsurance())
				cReportBean.setInsurance(item.getInsurance());
			//ChartSpecilization 
			ChartSpecilization chartSpl =  chartSplDao.find(Integer.parseInt(item.getChartSpl()));
			if(null != chartSpl){
				cReportBean.setChartType(chartSpl.getChartType());
				cReportBean.setChartSpecialization(chartSpl.getChartSpelization());	
			}
			//Campus code
			if(null != item.getCampusCode())
				cReportBean.setCampusCode(item.getCampusCode());
			
			if(null != item.getAdmittedDate())
				cReportBean.setAdmitDate(item.getAdmittedDate());
			if(null != item.getDischargedDate())
				cReportBean.setDischargeDate(item.getDischargedDate());
			if(null != item.getUploadedDate())
				cReportBean.setReceivedDate(item.getUploadedDate());
			if(null != item.getLos() || !"".equals(item.getLos()))
				cReportBean.setLos(item.getLos());
			if(null != item.getStatus())
			cReportBean.setStatus(item.getStatus());
			
			final Set<ReviewWorklist> reviewWorkList = item.getReviewWorkLists();
			final List<ReviewWorklist> rwList = new ArrayList<>(reviewWorkList);
			Collections.sort(rwList, new RevWorkUpdateDateASCComparator());
			// setting the coder details
			for(ReviewWorklist reviewWork : rwList){
				if(null != reviewWork){
					Coders coder = codersMap.get(reviewWork.getCoders().getUserId());
					if("Coder".equalsIgnoreCase(reviewWork.getUserRole())){
						cReportBean.setEmpCode(coder.getEmployeeId());
						cReportBean.setEmpName(coder.getFirstname() + " " + coder.getLastname());
						cReportBean.setCodedDate(reviewWork.getUpdatedDate());
						if("Completed".equalsIgnoreCase(reviewWork.getStatus())){
							cReportBean.setCompletedDate(reviewWork.getUpdatedDate());
							//cReportBean.setComments(reviewWork.getComment());	
						} else if("CompletedNR".equalsIgnoreCase(reviewWork.getStatus())){
							cReportBean.setReview100("Yes");
						}
					} else if("LocalQA".equalsIgnoreCase(reviewWork.getUserRole())){
						cReportBean.setLocalQACode(coder.getEmployeeId());
						cReportBean.setLocalQAName(coder.getFirstname() + " " + coder.getLastname());
						if("Completed".equalsIgnoreCase(reviewWork.getStatus())){
							cReportBean.setCompletedDate(reviewWork.getUpdatedDate());
							//cReportBean.setComments(reviewWork.getComment());
						} else if("CompletedNR".equalsIgnoreCase(reviewWork.getStatus())){
							cReportBean.setReview100("Yes");
						}
					}
					logger.debug(" --------> RW Id : " +reviewWork.getId());
					logger.debug(" --------> RW Comments : " +reviewWork.getComment());
					cReportBean.setComments(reviewWork.getComment());	
				}
			}
			// setting child &  parent account deatails
			cReportBean.setChildAccount("NA");
			cReportBean.setParentAccount("NA");
			//cReportBean.setReview100("NA");
			if(null == cReportBean.getStatus() || "".equalsIgnoreCase(cReportBean.getStatus().trim()))
				cReportBean.setStatus(NOT_STARTED);
			reportList.add(cReportBean);
			count++;
		}
		
		return reportList;
	}
	// csv generation 
		public Boolean generateCsvFromList(List<ClientwiseReportBean> reportList, final String csvDirPath, final String csvFileName){
			// creating csv writer
			FileWriter fw = null;
			CSVWriter csvWriter = null;
			
			List<String[]> records = new ArrayList<String[]>();
	        //add header record
	        records.add(new String[]{"S.NO", "Coder-Emp code", "Coder-Emp Name", "Client", "Acc#", "MRN", "Service Type", "Child Acc#", "Parent Acc#",
	        							"Patient Name", "Insurance", "Chart Type", "Chart Specialization", "Campus Code", "Admit Date", "Discharge Date", 
	        							"Received Date", "Coded Date", "Completed Date", "LOS", "Emp Code-Local QA", "Emp Name-Local QA", 
	        							"100 % Review (Y/N)", "Status", "Comments"});
	        Iterator<ClientwiseReportBean> it = reportList.iterator();
	        while(it.hasNext()){
	        	ClientwiseReportBean c = it.next();
	            records.add(new String[]{String.valueOf(c.getsNo()), String.valueOf(c.getEmpCode()), c.getEmpName(), c.getClient(), c.getAccountNo(), c.getMrNo(), c.getServiceType(),c.getChildAccount(), c.getParentAccount(),
	            						c.getPatientName(), c.getInsurance(), c.getChartType(),	c.getChartSpecialization(), c.getCampusCode(), DateUtil.convertDateToString(c.getAdmitDate()), DateUtil.convertDateToString(c.getDischargeDate()),
	            						DateUtil.convertDateToString(c.getReceivedDate()), DateUtil.convertDateToString(c.getCodedDate()), DateUtil.convertDateToString(c.getCompletedDate()), String.valueOf(c.getLos()), String.valueOf(c.getLocalQACode()), c.getLocalQAName(),
	            						c.getReview100(), c.getStatus(), c.getComments()});
	        }
	       // return records;
	        try{
	        	// Checks whether the dir & file exists IF NOT it will create the dir & file
	        	reportUtility.createFileDirectory(csvDirPath, csvFileName);
	        	fw = new FileWriter(new File(csvFileName));
				
				csvWriter = new CSVWriter(fw);		 
				csvWriter.writeAll(records);
				logger.debug(" Done  writing data to csv for clientwise_consolidation_report" );
				
			}catch(IOException ie){
				ie.printStackTrace();
				return false;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}finally{
				try {
					csvWriter.flush();
					csvWriter.close();
				} catch (IOException e5) {
					e5.printStackTrace();
				}
				
				if(null != fw )
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	        return true;
		}
		
		// csv to Excel gebnerator
		public String convertCsvToExcel(String csvfileName) throws IOException {
			String generatedExcelPath = null;
			ArrayList arList = null;
			ArrayList al = null;
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			int i = 0;
			arList = new ArrayList();
			br = new BufferedReader(new FileReader(csvfileName));
			while ((line = br.readLine()) != null) {
				//System.out.println("Line : " + line);
				al = new ArrayList();
				String strar[] = line.split(cvsSplitBy);
				for (int j = 0; j < strar.length; j++) {
					al.add(strar[j]);
				}
				arList.add(al);
				//System.out.println();
				i++;
			}
			br.close();
			//System.out.println("arList : " + arList);
			try {
				HSSFWorkbook hwb = new HSSFWorkbook();
				HSSFSheet sheet = hwb.createSheet("Clientwise_Consolidation_Report");
				for (int k = 0; k < arList.size(); k++) {
					ArrayList ardata = (ArrayList) arList.get(k);
					HSSFRow row = sheet.createRow(0 + k);
					for (int p = 0; p < ardata.size(); p++) {
						HSSFCell cell = row.createCell( p);
						String data = ardata.get(p).toString();
					//	System.out.println("DATA : " + data);
						if (k > 0 && ( p == 0 || p == 1 || p == 17 || p == 18) ) {						
							if(null != data || "null".equalsIgnoreCase(data))
							data = data.replaceAll("\"", "");
							data = data.replaceAll("=", "");
							if(null != data && !"null".equalsIgnoreCase(data)){
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
								if(data.contains("\\."))
									cell.setCellValue(Double.parseDouble(data));
								else
									cell.setCellValue(Integer.parseInt(data));
							}
						} else if (data.startsWith("=") || data.startsWith("\"")){
							data = data.replaceAll("\"", ""); 
							cell.setCellType(Cell.CELL_TYPE_STRING);
							cell.setCellValue(data);
						} else {
							data = data.replaceAll("\"", ""); 
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							cell.setCellValue(data);
						}
						// */
						// cell.setCellValue(ardata.get(p).toString());
					}
					//System.out.println();
				}
				generatedExcelPath = csvfileName.substring(0, csvfileName.length()-4) + "_Excel.xls";
				FileOutputStream fileOut = new FileOutputStream(generatedExcelPath);
				hwb.write(fileOut);
				fileOut.close();
				logger.debug("Your excel file has been generated");
			} catch (Exception ex) {
				ex.printStackTrace();
				return generatedExcelPath;
			}
			return generatedExcelPath;
		
		}
		
		
	/**
	 * 
	 * @param entities
	 * @param clientChartMaps
	 * @return
	 */
	public Map<String, List<InvoiceReportBean>> generateInvoiceReportBeanListForCompletedChart(List<Object[]> entities, 
			final List<ClientChartMapping> clientChartMaps) {
		
		final Map<String, List<InvoiceReportBean>> reportMap = new HashMap<String, List<InvoiceReportBean>>();
		
		for(ClientChartMapping clientChartMap : clientChartMaps) {
			List<InvoiceReportBean> invoiceBeanList  =  reportMap.get(clientChartMap.getChartType());
			if (invoiceBeanList == null) {
				invoiceBeanList = new ArrayList<InvoiceReportBean>();
				reportMap.put(clientChartMap.getChartType(), invoiceBeanList);
			}
			long snoCount = 0;
			for (Object[] entity : entities) {
				snoCount++;
				if(clientChartMap.getChartType().equals((String)entity[7])) {
					InvoiceReportBean invoiceReportBean = new InvoiceReportBean();
					invoiceReportBean.setSno(snoCount);
					invoiceReportBean.setClientId((Integer)entity[1]);
					invoiceReportBean.setAccount((String)entity[2]);
					if((Timestamp)entity[3] != null){
						invoiceReportBean.setDischargeDate(DateUtil.convertDateToString((Timestamp)entity[3]));
					} else {
						invoiceReportBean.setDischargeDate("");
					}
					invoiceReportBean.setReceivedDate(DateUtil.df.format((Timestamp)entity[4]));
					invoiceReportBean.setTat((Integer)entity[5]);
					invoiceReportBean.setLos((Integer)entity[6]);
					invoiceReportBean.setChartType((String)entity[7]);
					invoiceReportBean.setCompletedDate(DateUtil.df.format((Timestamp)entity[8]));
					invoiceReportBean.setNotes((String)entity[9]);
					invoiceBeanList.add(invoiceReportBean);
				}
		    }
		}
		
		return reportMap;
	}
	
	/**
	 * 
	 * @param entities
	 * @param clientChartMaps
	 * @return
	 */
	public Map<String, List<InvoiceReportBean>> generateInvoiceReportBeanListForPendingChart(List<Object[]> entities, 
			final List<ClientChartMapping> clientChartMaps) {
		
		final Map<String, List<InvoiceReportBean>> reportMap = new HashMap<String, List<InvoiceReportBean>>();
		
		for(ClientChartMapping clientChartMap : clientChartMaps) {
			List<InvoiceReportBean> invoiceBeanList  =  reportMap.get(clientChartMap.getChartType());
			if (invoiceBeanList == null) {
				invoiceBeanList = new ArrayList<InvoiceReportBean>();
				reportMap.put(clientChartMap.getChartType(), invoiceBeanList);
			}
			long snoCount = 0;
			for (Object[] entity : entities) {
				snoCount++;
				if(clientChartMap.getChartType().equals((String)entity[4])) {
					InvoiceReportBean invoiceReportBean = new InvoiceReportBean();
					invoiceReportBean.setSno(snoCount);
					invoiceReportBean.setClientId((Integer)entity[1]);
					invoiceReportBean.setAccount((String)entity[2]);
					if((Timestamp)entity[3] != null){
						invoiceReportBean.setDischargeDate(DateUtil.df.format((Timestamp)entity[3]));
					} else{
						invoiceReportBean.setDischargeDate("");
					}
					invoiceReportBean.setChartType((String)entity[4]);
					invoiceReportBean.setNotes((String)entity[5]);
					invoiceReportBean.setStatus((String)entity[6]);
					invoiceBeanList.add(invoiceReportBean);
				}
		    }
		}
		
		return reportMap;
	}
	
	/**
	 * 
	 * @param reportList
	 * @param csvFileName
	 * @return true/false
	 */
	public Boolean generateInvoiceReportCSVFiles(List<InvoiceReportBean> reportBeanList, String csvFileName, String type)throws Exception{
		// creating csv writer
		FileWriter fw = null;
		CSVWriter csvWriter = null;
		
		List<String[]> records = new ArrayList<String[]>();
		
		if(type.equals(COMPLETED)) {
			  //add header record
	        records.add(new String[]{"S.NO", "Acc#", "Discharge Date", "Received Date", "Completed Date", "LOS", "TAT", "NOTES"});
	        Iterator<InvoiceReportBean> it = reportBeanList.iterator();
	        long snoCount = 1;
	        while(it.hasNext()){
	        	InvoiceReportBean c = it.next();
	            records.add(new String[]{String.valueOf(snoCount++), c.getAccount(), 
	            		c.getDischargeDate(), c.getReceivedDate(), c.getCompletedDate(), 
	            		String.valueOf(c.getLos()), String.valueOf(c.getTat()), c.getNotes()});
	        }
		} else if(type.equals(PENDING)) {
			  //add header record
	        records.add(new String[]{"S.NO", "Acc#", "Discharge Date", "Notes", "Status"});
	        Iterator<InvoiceReportBean> it = reportBeanList.iterator();
	        long snoCount = 1;
	        while(it.hasNext()){
	        	InvoiceReportBean c = it.next();
	            records.add(new String[]{String.valueOf(snoCount++), c.getAccount(), 
	            		c.getDischargeDate(), c.getNotes(), c.getStatus()});
	        }
		}
      
        
       // return records;
        try{
			fw = new FileWriter(new File(csvFileName));
			
			csvWriter = new CSVWriter(fw);		 
			csvWriter.writeAll(records);
			
			logger.info("--------> Done  writing data to csv : "+csvFileName);
			
		} catch(Exception e) {
			throw e;
		} finally {
			try {
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e5) {
				e5.printStackTrace();
			}
			if(null != fw )
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
        return true;
	}
	
	
	/**
	 * 
	 * @param report data Bean List
	 * @param csvFileName
	 * @return true/false
	 */
	public Boolean generateLocalQAReportCSVFiles(List<LocalQADailyProductivityBean> reportBeanList, String csvFileName)throws Exception{
		// creating csv writer
		FileWriter fw = null;
		CSVWriter csvWriter = null;
		
		List<String[]> records = new ArrayList<String[]>();
		
		//add header record
        records.add(new String[]{"Emp Code", "Employee Name", "Location", "Client Name", "Chart Type", "Chart Specilaization", "CNR File count",
        		"CNR-HRs", "100% Review File count", "100% Review -HRs", "Post Audits File count", "Post Audit-Hrs", "Total Files", "Total Hrs",
        		"Avg Hrs"});
        Iterator<LocalQADailyProductivityBean> it = reportBeanList.iterator();
        while(it.hasNext()) {
        	LocalQADailyProductivityBean c = it.next();
            records.add(new String[]{String.valueOf(c.getEmpcode()), c.getEmpname(), c.getLocation(), c.getClientName(), 
            		c.getChartType(), c.getChartSpecialization(), String.valueOf(c.getCnrFileCount()),  String.valueOf(c.getCnrHours()),  
            		String.valueOf(c.getReviewFilecount()), String.valueOf(c.getReviewFileHours()), String.valueOf(c.getAuditsFileCount()), 
            		String.valueOf(c.getAuditsFileHours()), String.valueOf(c.getTotalCharts()), String.valueOf(c.getTotalHours()), 
            		String.valueOf(c.getAvgHours())});
        }
  
        
       // return records;
        try{
			fw = new FileWriter(new File(csvFileName));
			
			csvWriter = new CSVWriter(fw);		 
			csvWriter.writeAll(records);
			
			logger.info("--------> Done  writing data to csv : "+csvFileName);
			
		} catch(Exception e) {
			throw e;
		} finally {
			try {
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e5) {
				e5.printStackTrace();
			}
			if(null != fw )
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
        return true;
	}
	
	
	public boolean generateInvoiceReportsCSVFile(final Map<String, List<InvoiceReportBean>> reportData, final String fileLocation, 
			final String type, final String clientName) throws Exception 
	{
		//Create a blank sheet
        Workbook wb  = new XSSFWorkbook();

        for(Map.Entry<String, List<InvoiceReportBean>> entry : reportData.entrySet()) {
			
			generateExcelSheet(wb.createSheet(entry.getKey()), entry.getValue(), type);
			logger.debug(" Added Sheet : " + entry.getKey() +" On Report File : "+clientName +"_"+type+"_Chart_Details.xlsx");
		}
        try 
		{
			//Write the workbook in file system
		    FileOutputStream out = new FileOutputStream(new File(fileLocation+"/"+clientName +"_"+type+"_Chart_Details.xlsx"));
		    wb.write(out);
		    out.close();
		    logger.debug(" Generated CSV file for Invoice report !! @ the location : " + fileLocation+type+".xlsx");
		    
		    return true;
		} 
		catch (Exception e) 
		{
		    e.printStackTrace();
		}
        return false;
    }
	
	/**
     *  Added the columns and records for reports
     */
    private void generateExcelSheet(Sheet sheet, List<InvoiceReportBean> reportBeanList, final String type) 
    {
		//This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		
		if(type.equals(COMPLETED)) {
			 //add header record
			data.put("1", new Object[] {"S.NO", "Acc#", "Discharge Date", "Received Date", "Completed Date", "LOS", "TAT", "NOTES"});
			Iterator<InvoiceReportBean> it = reportBeanList.iterator();
	        long snoCount = 2;
	        while(it.hasNext()) {
	        	InvoiceReportBean c = it.next();
	        	data.put(new Long(snoCount).toString(), new Object[]{String.valueOf(snoCount++), c.getAccount(), 
	            		c.getDischargeDate(), c.getReceivedDate(), c.getCompletedDate(), 
	            		String.valueOf(c.getLos()), String.valueOf(c.getTat()), c.getNotes()});
	        	snoCount++;
	        }
		} else if(type.equals(PENDING)) {
			 //add header record
			data.put("1", new Object[]{"S.NO", "Acc#", "Discharge Date", "Notes", "Status"});
	        Iterator<InvoiceReportBean> it = reportBeanList.iterator();
	        long snoCount = 2;
	        while(it.hasNext()){
	        	InvoiceReportBean c = it.next();
	        	data.put(new Long(snoCount).toString(), new Object[]{String.valueOf(snoCount++), c.getAccount(), 
	            		c.getDischargeDate(), c.getNotes(), c.getStatus()});
	        }
		}
		 
		//Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		int rownum = 0;
		for (String key : keyset)
		{
		    Row row = sheet.createRow(rownum++);
		    Object [] objArr = data.get(key);
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
		
    }
    
    /**
	 * 
	 * @param report data Bean List
	 * @param csvFileName
	 * @return true/false
	 */
	public Boolean generateSupervisorReportCSVFiles(List<ReportBean> reportBeanList, String csvFileName)throws Exception{
		// creating csv writer
		FileWriter fw = null;
		CSVWriter csvWriter = null;
		
		List<String[]> records = new ArrayList<String[]>();
		
		//add header record
        records.add(new String[]{"Client", "ChartType", "Completed", "MISC", "InComplete", "Coder Assigned", "LocalQA Assigned",
        		"GlobalQA Assigned", "Coding InProgress", "LocalQA InProgress", "GlobalQA InProgress", "Local CNR", "Global CNR", 
        		"Local Audit", "Global Audit", "Audited", "Not Started", "Total Charts"});
        Iterator<ReportBean> it = reportBeanList.iterator();
        while(it.hasNext()) {
        	ReportBean c = it.next();
            records.add(new String[]{String.valueOf(c.getClient()), c.getChartType(), String.valueOf(c.getCompleted()), 
            		String.valueOf(c.getMisc()), String.valueOf(c.getInComplete()), String.valueOf(c.getCoderAssigned()), 
            		String.valueOf(c.getLocalQAAssigned()),  String.valueOf(c.getGlobalQAAssigned()),  
            		String.valueOf(c.getCoderInProgress()), String.valueOf(c.getLocalQAInProgress()), 
            		String.valueOf(c.getGlobalQAInProgress()), String.valueOf(c.getLocalCNR()), String.valueOf(c.getGlobalCNR()),
            		String.valueOf(c.getLocalAudit()),String.valueOf(c.getGlobalAudit()),String.valueOf(c.getAudited()),
            		String.valueOf(c.getOpen()), String.valueOf(c.getCount())});
        }
       // return records;
        try{
			fw = new FileWriter(new File(csvFileName));
			csvWriter = new CSVWriter(fw);		 
			csvWriter.writeAll(records);
			logger.info("--------> Done  writing data to csv : "+csvFileName);
		} catch(Exception e) {
			throw e;
		} finally {
			try {
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e5) {
				e5.printStackTrace();
			}
			if(null != fw )
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
        return true;
	}
	
}
