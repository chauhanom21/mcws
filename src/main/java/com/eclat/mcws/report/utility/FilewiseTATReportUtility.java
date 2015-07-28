package com.eclat.mcws.report.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVWriter;
import ch.qos.logback.classic.Logger;

import com.eclat.mcws.admin.resource.AppStartupComponent;
import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.persistence.dao.ChartSpecilizationDao;
import com.eclat.mcws.persistence.entity.ChartSpecilization;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.ReviewWorklist;
import com.eclat.mcws.persistence.entity.WorkListItem;
import com.eclat.mcws.report.bean.FilewiseTatReportBean;
import com.eclat.mcws.util.CommonOperations;

@Component
public class FilewiseTATReportUtility {

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
	public void init() {
		codersMap = appStartupComponent.getCodersMap();
	}

	public String generateFilewiseTATReport(Map<WorkListItem, String> tatExceededItems, String csvDir,
			String csvFileName) {
		String excel_path = null;
		List<FilewiseTatReportBean> beanList = constructTatReportBeanList(tatExceededItems);
		if (beanList.size() > 0) {
			csvFileName = generateCsvFromList(beanList, csvDir, csvFileName);
			logger.debug(" Generated CSv for filewise tat report !! @ the location : " + csvFileName);
			try {
				excel_path = convertCsvToExcel(csvFileName);
			} catch (IOException e) {
				logger.error("Exception FilewiseTATReport : ", e);
			}
		}
		return excel_path;
	}

	private List<FilewiseTatReportBean> constructTatReportBeanList(Map<WorkListItem, String> tatExceededItems) {
		init();
		List<FilewiseTatReportBean> beanList = null;
		FilewiseTatReportBean cReportBean = null;
		beanList = new ArrayList<FilewiseTatReportBean>();
		Integer count = 1;
		for (Entry<WorkListItem, String> entry : tatExceededItems.entrySet()) {
			WorkListItem item = entry.getKey();
			cReportBean = new FilewiseTatReportBean();
			cReportBean.setsNo(count);
			if (null != item.getAccountNumber())
				cReportBean.setAccountNo(item.getAccountNumber());
			if (null != item.getMrNumber())
				cReportBean.setMrNo(item.getMrNumber());
			if (null != item.getClientDetails())
				cReportBean.setClient(item.getClientDetails().getName());
			// ChartSpecilization
			ChartSpecilization chartSpl = chartSplDao.find(Integer.parseInt(item.getChartSpl()));
			if (null != chartSpl) {
				cReportBean.setChartType(chartSpl.getChartType());
				cReportBean.setChartSpecialization(chartSpl.getChartSpelization());
			}
			// setting the coder details
			for (ReviewWorklist reviewWork : item.getReviewWorkLists()) {
				if (null != reviewWork) {
					Coders coder = codersMap.get(reviewWork.getCoders().getUserId());
					if ("Coder".equalsIgnoreCase(reviewWork.getUserRole())) {
						cReportBean.setCoderCode(coder.getEmployeeId());
						cReportBean.setCoderName(coder.getFirstname() + " " + coder.getLastname());
					} else if ("LocalQA".equalsIgnoreCase(reviewWork.getUserRole())) {
						cReportBean.setLocalQACode(coder.getEmployeeId());
						cReportBean.setLocalQAName(coder.getFirstname() + " " + coder.getLastname());
					}
				}
			}
			if (item.getStatus() != null) {
				cReportBean.setCurrentStatus(item.getStatus());
			} else {
				cReportBean.setCurrentStatus(ChartStatus.NotStarted.getStatus());
			}
			cReportBean.setOutOfTatHrs(entry.getValue());
			count++;
			beanList.add(cReportBean);
		}
		return beanList;
	}

	public String generateCsvFromList(List<FilewiseTatReportBean> reportList, final String csvDirPath,
			final String csvFileName) {
		// creating csv writer
		FileWriter fw = null;
		CSVWriter csvWriter = null;

		List<String[]> records = new ArrayList<String[]>();
		// add header record
		records.add(new String[] { "S.NO", "Acc#", "MR No", "Client", "Chart Type", "Chart Specialization",
				"Coder Emp code", "Coder Name", "Local QA  Emp code", "Local QA Name", "Hrs(OUT of TAT)",
				"Current Status" });
		Iterator<FilewiseTatReportBean> it = reportList.iterator();
		while (it.hasNext()) {
			FilewiseTatReportBean c = it.next();
			records.add(new String[] { String.valueOf(c.getsNo()), c.getAccountNo(), c.getMrNo(), c.getClient(),
					c.getChartType(), c.getChartSpecialization(), String.valueOf(c.getCoderCode()), c.getCoderName(),
					String.valueOf(c.getLocalQACode()), c.getLocalQAName(), String.valueOf(c.getOutOfTatHrs()),
					c.getCurrentStatus() });
		}
		// return records;
		try {
			// Checks whether the dir & file exists IF NOT it will create the
			// dir & file
			reportUtility.createFileDirectory(csvDirPath, csvFileName);
			fw = new FileWriter(new File(csvFileName));

			csvWriter = new CSVWriter(fw);
			csvWriter.writeAll(records);
			logger.debug(" Done  writing data to csv for clientwise_consolidation_report");

		} catch (IOException ie) {
			ie.printStackTrace();
			return csvFileName;
		} catch (Exception e) {
			e.printStackTrace();
			return csvFileName;
		} finally {
			try {
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e5) {
				e5.printStackTrace();
			}

			if (null != fw)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return csvFileName;
	}

	/*
	 * private String convertCsvToExcel(String csvFileName){ // renaming the csv
	 * file to excel i.e .xls File file = new File(csvFileName); String
	 * excelFileName = csvFileName.substring(0, csvFileName.length()-4) +
	 * "_Excel.xls"; file.renameTo(new File(excelFileName)); return
	 * excelFileName; }
	 */

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
			// System.out.println("Line : " + line);
			al = new ArrayList();
			String strar[] = line.split(cvsSplitBy);
			for (int j = 0; j < strar.length; j++) {
				al.add(strar[j]);
			}
			arList.add(al);
			// System.out.println();
			i++;
		}
		br.close();
		// System.out.println("arList : " + arList);
		try {
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Filewise_TAT_Report");
			for (int k = 0; k < arList.size(); k++) {
				ArrayList ardata = (ArrayList) arList.get(k);
				HSSFRow row = sheet.createRow(0 + k);
				for (int p = 0; p < ardata.size(); p++) {
					HSSFCell cell = row.createCell(p);
					String data = ardata.get(p).toString();
					// System.out.println("DATA : " + data);
					if (k > 0 && (p == 0 || p == 6 || p == 8)) {
						if (null != data || "null".equalsIgnoreCase(data))
							data = data.replaceAll("\"", "");
						data = data.replaceAll("=", "");
						if (null != data && !"null".equalsIgnoreCase(data)) {
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							if (data.contains("\\."))
								cell.setCellValue(Double.parseDouble(data));
							else
								cell.setCellValue(CommonOperations.getEmployeeId(data));
						}
					} else if (data.startsWith("=") || data.startsWith("\"")) {
						data = data.replaceAll("\"", "");
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(data);
					}
					// */
					// cell.setCellValue(ardata.get(p).toString());
				}
				// System.out.println();
			}
			generatedExcelPath = csvfileName.substring(0, csvfileName.length() - 4) + "_Excel.xls";
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
	 * checking for the work items whose actual TAT is exceeds the worklist TAT
	 * table
	 * 
	 * @return
	 */
	public Map<WorkListItem, String> getExceededTATCharts(final List<WorkListItem> entities) {
		Map<WorkListItem, String> tatExceededItems = new HashMap<>();
		Integer actualTat = 0;
		logger.debug(" ====> Total item loded : " + entities.size());
		int count = 0;
		for (WorkListItem item : entities) {
			actualTat = item.getTat();
			final Long exccedTime = getExccedTATVAlue(item);

			final Integer exccedTatHours = DateUtil.convertMillsecondToHours(exccedTime);
			if (exccedTatHours > actualTat) {
				String exccedTatValue = DateUtil.convertMinsToHHMM(DateUtil.convertMillsToMins(exccedTime) - actualTat
						* 60);
				tatExceededItems.put(item, exccedTatValue);
				count++;
			}
		}
		return tatExceededItems;
	}

	private Long getExccedTATVAlue(WorkListItem item) {
		long tatExccedTime = 0L;
		final String status = item.getStatus();
		if (status != null) {
			if ((status.equals(ChartStatus.LocalAudit.getStatus())
					|| status.equals(ChartStatus.GlobalAudit.getStatus()) || status.equals(ChartStatus.LocalAudited
					.getStatus())) || status.equals(ChartStatus.GlobalAudited.getStatus())) {
				Set<ReviewWorklist> reviewWorkLists = item.getReviewWorkLists();
				ReviewWorklist reviewWorklist = null;
				for (ReviewWorklist reviewWork : reviewWorkLists) {
					if (reviewWork.getStatus().equals(ChartStatus.Completed.getStatus())) {
						reviewWorklist = reviewWork;
					}
				}
				if (reviewWorklist != null) {
					long updatedTime = reviewWorklist.getUpdatedDate().getTime();
					long receivedTime = item.getUploadedDate().getTime();
					tatExccedTime = updatedTime - receivedTime;
				}
			} else {
				long updatedTime = item.getUpdatedDate().getTime();
				long receivedTime = item.getUploadedDate().getTime();
				tatExccedTime = updatedTime - receivedTime;
			}
		} else {
			logger.debug("Status is NULL ----------------");
			long updatedTime = new Date().getTime();
			long receivedTime = item.getUploadedDate().getTime();
			tatExccedTime = updatedTime - receivedTime;
		}

		return tatExccedTime;
	}
}
