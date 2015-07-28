package com.eclat.mcws.admin.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.persistence.dao.ClientDetailsDao;
import com.eclat.mcws.persistence.dao.WorkListDao;
import com.eclat.mcws.persistence.entity.ClientDetails;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.util.CommonHeaders;
import com.eclat.mcws.util.FieldDetails;
import com.eclat.mcws.util.rest.Response;

@Service
public class ExcelFileHandlingServiceImpl implements ExcelFileHandlingService {
	
	@Log
	private Logger logger;
	
	@Autowired
	private WorkListDao workListDao;
	
	@Autowired
	private ClientDetailsDao clientDao;
	
	//@Autowired
	//private CacheManager cacheManager;
	
	@Autowired
	private AppStartupComponent appStartupComponent;
	
	/**
	 * Method to get typed value
	 * @param type
	 * @param value
	 * @return
	 */
	private static Object getTypeValue(Class<?> type, Cell cell) {
		Object typedValue = null;
		if(null != cell){
			if(type == Integer.class){
				typedValue = (int) cell.getNumericCellValue();
			} else if(type == double.class){
				typedValue = cell.getNumericCellValue();
			} else if(type == boolean.class){
				typedValue = cell.getBooleanCellValue();
			} else if(type == String.class){
				typedValue = cell.getStringCellValue();
			} else if(type == Date.class){
				typedValue = cell.getDateCellValue();			
			}
		}
		return typedValue;
	} 
	
	public Response<List<WorkListItem>> getWorkListItems(InputStream inputStream, String clientName, Boolean isIncompleteList, Response<List<WorkListItem>> responseRef) {
		
		Map<String , Integer> map = new LinkedHashMap<>();
		Map<String, FieldDetails> columns = CommonHeaders.getMandatoryColumns();
		List<WorkListItem> items = new ArrayList<>();
		HSSFWorkbook workbook = null;
		HSSFWorkbook updatedWorkbook = null;
		HSSFSheet sheet = null;
		String chartName = "";
		// Loading all the ChartType-Spl in advance from DB.
		appStartupComponent.postInitializeData();
		ClientDetails cd = clientDao.getClientByName(clientName);
	//	Cache cache = cacheManager.getCache("chartSpl");
				
		try {
			//Get the workbook instance for XLS file
			workbook = new HSSFWorkbook(inputStream);
			if(null != workbook && workbook.getNumberOfSheets() > 0){
				logger.debug("Number of sheets in the uploaded Excel : " + workbook.getNumberOfSheets());
				logger.debug("Mandatory Columns by App : " + columns); 
				updatedWorkbook = updateWorkbook(workbook);
				for(int j=0; j < updatedWorkbook.getNumberOfSheets(); j++){				
				
				//Get first sheet from the workbook
				//sheet = workbook.getSheetAt(0);
				sheet = updatedWorkbook.getSheetAt(j);
				//chartName is the sheet name;
				//chartName = workbook.getSheetName(0);
				chartName = updatedWorkbook.getSheetName(j);
				int i=0;
				String key = "";				
				WorkListItem item;			
				for (Row row : sheet) {
					item = new WorkListItem();
					if (i==0) {
						// Prepare the map with initial row header
						for (Cell cell : row) {
							key = cell.getStringCellValue();
							if (columns.containsKey(key)) {
								map.put(key, cell.getColumnIndex());
							}
						}
						// adding for Incomplete list data validation
						if(!map.containsKey("Comments") && isIncompleteList) {
							responseRef.setMessage("Please Upload valid Excel file with IncompleteList Client Data. You Uploaded Regular Worklist instead of Incomplete List");
							return responseRef;
						} else if( map.containsKey("Comments") && !isIncompleteList) {
							responseRef.setMessage(" You may Uploaded Incomplete Worklist data instead of Normal worklist");
							return responseRef;
						}
					} else {
						// Use the map and get the cell values at the indices and prepare the list of Work items
						String accountNo = null;
						String mrNo = null;						
						
						String errorMessage = validateWorksheet(row, map, clientName, chartName);
						if(errorMessage != null) {
							responseRef.setMessage(errorMessage);
							return responseRef;
						} else {
							for ( String column : map.keySet() ) {
								try {
									if (columns.containsKey(column)) {
										Cell cell = row.getCell(map.get(column));
										FieldDetails fd = columns.get(column);
										
										//================
										if("Account No".equalsIgnoreCase(column)) {
											cell.setCellType(1);
											accountNo = cell.getStringCellValue();
											item.setAccountNumber(accountNo);
											logger.debug("Account No: ===============> " + accountNo + " <========");
										} else if("MR Number".equalsIgnoreCase(column)) {
											cell.setCellType(1);
											mrNo = cell.getStringCellValue();
											item.setMrNumber(mrNo);
											logger.debug("MR Number: ===============> " + mrNo + " <========");
										} else if (("Specialization").equalsIgnoreCase(column) && (cell.getStringCellValue() != null && !"".equalsIgnoreCase(cell.getStringCellValue().trim()))) {
											logger.debug("Specialization ::::" + getTypeValue(String.class,cell).toString() + " && ChartType-Specialization ===> "+ chartName+"-"+ getTypeValue(String.class,cell));
											if(appStartupComponent.getChartSplMap().containsKey(chartName+"-"+ getTypeValue(String.class,cell)))
												item.setChartSpl(appStartupComponent.getChartSplMap().get(chartName+"-"+ getTypeValue(String.class,cell)).toString());
											else {
												responseRef.setMessage("ChartType & Specialization  combo does not exist. Please Configure this combo in ChartSpecialization for the  ACC# "  + accountNo + " / "+ mrNo);
												return responseRef;
											}
										} else { 										
											Field f1 = WorkListItem.class.getDeclaredField(fd.getName().trim());
											f1.setAccessible(true);									
											Object value = null;
											try {
												value = getTypeValue(f1.getType(),cell);
												if(null != value){
													logger.debug(value.toString());
													logger.debug(f1.getName());
												}
											}catch (IllegalStateException exception) {
												if (f1.getName()== CommonHeaders.fieldAccountNumber && null != value) {
													value = ""+getTypeValue(Integer.class,cell);
													logger.debug(value.toString());
												}
											}
											f1.set(item, value);
										}																						
									}
								} catch (Exception e) {
									logger.error("Error Occurred for the Key=====>"+column,e);
								}
							}
							// Now add details like cliendId and chart spl to the item
							item.setClientDetails(cd);
							item.setUpdatedDate(new Timestamp(System.currentTimeMillis()));	
							item.setUploadedDate(new Timestamp(System.currentTimeMillis()));
							items.add(item);
						}
					}
					if(i < sheet.getPhysicalNumberOfRows())
						i++;
				
					}
				}
			}
			logger.info("Lenth of the Items======>"+items.size());	
			responseRef.setPayLoad(items);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			workbook = null;
			updatedWorkbook = null;
			sheet = null;
		}
		return responseRef;
		
	}
	
	
	@Override
	@Transactional
	public Boolean saveAllListItems(List<WorkListItem> items) {
		return workListDao.saveAll(items);		
	}
	
	@Override	
	public List<ClientDetails> getClients() {
		return clientDao.findAll();		
	}
	
	// adding for Incomplete Items
	@Override
	@Transactional
	public Boolean updateIncompleteWorkItems(List<WorkListItem> items){
		return workListDao.updateIncompleteWorkItems(items);
			
	}
	
	//HSSFWorkbook updatedWorkbook = updateWorkbook(workbook);
	private HSSFWorkbook updateWorkbook(HSSFWorkbook workbook){
		String chartName = null;
		for(int j=0; j < workbook.getNumberOfSheets(); j++){
			chartName = workbook.getSheetName(j);
			if("Info".equalsIgnoreCase(chartName) || "Notes".equalsIgnoreCase(chartName))
				workbook.removeSheetAt(j);
		}
		return workbook;
	}
	
	// validateWorkSheet Here 
	private String validateWorksheet (Row row, Map<String , Integer> map, String clientName, String chartName) {
		String errorMsg = null;
		String mrNoCell = null;
		String accountNoCell = null;
		String clientNameCell = row.getCell(map.get(CommonHeaders.clientName)).getStringCellValue();
		String chartTypeCell = row.getCell(map.get(CommonHeaders.chartType)).getStringCellValue();
		String specializationCell = row.getCell(map.get(CommonHeaders.specialization)).getStringCellValue();
		Date dischargedDateCell = row.getCell(map.get(CommonHeaders.dischargedDate)).getDateCellValue();
		Cell accountNoExCell = row.getCell(map.get(CommonHeaders.accountNumber));
		
		if(accountNoExCell != null) {
			accountNoExCell.setCellType(1);
			accountNoCell = accountNoExCell.getStringCellValue();
		}
		Cell mrNoExCell = row.getCell(map.get(CommonHeaders.mrNumber));
		if(mrNoExCell != null) {
			mrNoExCell.setCellType(1);
			mrNoCell = mrNoExCell.getStringCellValue();
		}
		logger.debug("Row No:" + row.getRowNum() + " ===" + row.toString() );
		if( clientNameCell.isEmpty() && chartTypeCell.isEmpty() && specializationCell.isEmpty()) 
			errorMsg ="Please check the format of excel, there might be EMPTY row(s) exists in the excel sheet. Remove all the EMPTY rows !!";			
		else if(accountNoCell.isEmpty() && mrNoCell.isEmpty()) 
			errorMsg = "Account No && MR Number both are empty for one/more charts in worksheet";
		else if(clientNameCell.isEmpty()) 
			errorMsg = "Client Name value for the account / MR " + accountNoCell + "  "+ mrNoCell +" in given excel is not available..";
		else if(dischargedDateCell == null) 
			errorMsg = "Discharge Date value for the account / MR " + accountNoCell + "  "+ mrNoCell +" , Chart Type "+chartTypeCell+" in given excel is not available..";
		else if(!clientNameCell.equalsIgnoreCase(clientName)) 
			errorMsg = "You Uploaded Invalid excel File for selected Client. Client Name in the excel file is differed from selected Client.";
		else if(chartTypeCell.isEmpty() || (!chartTypeCell.equalsIgnoreCase(chartName) )) 
			errorMsg = "Category( ChartType ) value for the account / MR " + accountNoCell + "  "+ mrNoCell +"  in excel is Missing OR Mismatching with worksheet name";
		else if (specializationCell.isEmpty()) 
			errorMsg = "Specialization value in given excel is not available for the  account# " + accountNoCell + " / "+ mrNoCell;
		
		return errorMsg;
	}		
		
}
