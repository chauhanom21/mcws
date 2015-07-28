package com.eclat.mcws.report.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVWriter;
import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.report.bean.CoderTrackingReportBean;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.DecimalFormatUtils;

@Component
public class CoderTrackingReportUtility {
	
	@Log
	private Logger logger;
		
	@Autowired
	private ReportsUtility reportUtility;
	
	public String generateCoderTrackingReport(List<CoderTrackingReportBean> entities, final String csvDirPath, String csvFileName, Map<String,Object> responseMap){
		String excel_path = null;
		csvFileName = generateCsvFromList(entities, csvDirPath, csvFileName);
		responseMap.put("csvFilePath", csvFileName);
		logger.debug(" Generated CSv for filewise tat report !! @ the location : " + csvFileName);
		try {
			excel_path = convertCsvToExcel(csvFileName);
		} catch (IOException e) {
			logger.error(" Exception Coder Tracking Report : ", e );
		}
		return excel_path;
	}
	
	private String generateCsvFromList(List<CoderTrackingReportBean> reportList, final String csvDirPath, final String csvFileName){
		// creating csv writer
		FileWriter fw = null;
		CSVWriter csvWriter = null;
		
		List<String[]> records = new ArrayList<String[]>();
        //add header record
        records.add(new String[]{ "S.NO", "Start Date", "End Date", "Coder Name", "Coder  Empcode",  "Completed" , "InComplete", "MISC", "CodingInProgress", "CNR" });
        Iterator<CoderTrackingReportBean> it = reportList.iterator();
        while(it.hasNext()){
        	CoderTrackingReportBean c = it.next();
            records.add( new String[]{  String.valueOf(c.getsNo()),  DateUtil.fomatDate( c.getFromDate()), DateUtil.fomatDate(c.getToDate()), c.getCoderFirstName() + " " + c.getCoderLastName(), 
            							String.valueOf(c.getCoderEmpCode()), String.valueOf(c.getCompletedItemsCount()), String.valueOf(c.getInCompleteItemsCount()), 
            							String.valueOf(c.getMiscItemsCount()), String.valueOf(c.getCodingInProgressItemsCount()), String.valueOf(c.getCnrItemsCount()) });
        }
       // return records;
        try{
        	// Checks whether the dir & file exists IF NOT it will create the dir & file
        	reportUtility.createFileDirectory(csvDirPath, csvFileName);
        	fw = new FileWriter(new File(csvFileName));
			
			csvWriter = new CSVWriter(fw);		 
			csvWriter.writeAll(records);
			logger.debug(" Done  writing data to csv for coder_tracking_report" );
			
		}catch(IOException ie){
			ie.printStackTrace();
			return csvFileName;
		}catch(Exception e){
			e.printStackTrace();
			return csvFileName;
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
        return csvFileName;
	}
	
	// csv to excel
	private String convertCsvToExcel(String csvfileName) throws IOException {
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
			HSSFSheet sheet = hwb.createSheet("Coder_Tracking_Report");
			for (int k = 0; k < arList.size(); k++) {
				ArrayList ardata = (ArrayList) arList.get(k);
				HSSFRow row = sheet.createRow(0 + k);
				for (int p = 0; p < ardata.size(); p++) {
					HSSFCell cell = row.createCell( p);
					String data = ardata.get(p).toString();
				//	System.out.println("DATA : " + data);
					if (k > 0 && ( p == 0 || p == 4 || p == 5 || p == 6 || p == 7 || p == 8 || p == 9) ) {						
						if(null != data || "null".equalsIgnoreCase(data))
						data = data.replaceAll("\"", "");
						data = data.replaceAll("=", "");
						if(null != data && !"null".equalsIgnoreCase(data)){
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if(data.contains("\\."))
								cell.setCellValue(Double.parseDouble(data));
							else
								cell.setCellValue(CommonOperations.getEmployeeId(data));
						}
					} else if (data.startsWith("=") || data.startsWith("\"")){
						data = data.replaceAll("\"", ""); 
						cell.setCellType(Cell.CELL_TYPE_STRING);
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
	 * @param dirPath
	 * @param fileName
	 * @return TRUE/FALSE
	 */
	public boolean generateCoderQualityReport(List<Object[]> entities, final String dirPath, String fileName){
		boolean isRepGenerated = false;
		XSSFWorkbook workbook = new XSSFWorkbook();
		
		//create sheet on workbook
		XSSFSheet sheet = workbook.createSheet("Coder Wise Accuaracy");
		//create data map
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		//Add headers
		data.put("1", new Object[]{"S.No","Coder Emp code","Coder Name","Client Name","Chart Type","AVG Accuracy"});
		
		try {
			reportUtility.createFileDirectory(dirPath, fileName);
			constructUsersQualityReportdata(entities, data);
			
			
			logger.debug(" ***** =====>DATA SIZE " + data.size());
			//Iterate over data and write to sheet
			Set<String> keyset = data.keySet();
			int rownum = 0;
			for (String key : keyset)
			{
				
			    Row row = sheet.createRow(rownum++);
			    Object [] objArr = data.get(key);
			    
			    logger.debug(" *****------> objArr " + objArr);
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
		    logger.debug(" ***** Generated Coder Quality report " + fileName);
		} catch (Exception e) {
			logger.error("Exception generate Coder Quality Report :: ", e );
			isRepGenerated = false;
		}
		return isRepGenerated;
	}
	
	private void constructUsersQualityReportdata(final List<Object[]> entities, Map<String, Object[]> data) {
		logger.debug(" ***** Constructing Users Quality Report data. total entity items :  "+entities.size());
		Integer snoCount = 2;
		for(Object[] entity : entities){
			data.put(snoCount.toString(), new Object[]{
				(snoCount.intValue() - 1), CommonOperations.getEmployeeId(entity[7]), ((String)entity[1] + (String)entity[2]), (String)entity[3],
				(String)entity[4], DecimalFormatUtils.dfNum.format((Double)entity[6])});
			snoCount++;
		}
		
		logger.debug(" ***** Report Data Construction Completed... ");
	}
	
}
