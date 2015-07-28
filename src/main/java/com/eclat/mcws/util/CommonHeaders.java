package com.eclat.mcws.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommonHeaders {
	
	public static String accountNumber = "Account No";
	public static String mrNumber = "MR Number";
	public static String patientName = "Patient Name";
	public static String dischargedDate = "Discharge Date";
	public static String admittedDate = "Admitted Date";
	public static String los = "Los";
	public static String tat = "Tat";
	public static String dollarAmount = "Dollar Amount";
	public static String specialization = "Specialization";
	public static String fieldAccountNumber = "accountNumber";
	// Effort Metric 
	public static String em = "EM Value";
	// Coder Name
	public static String coderName = "Coder";
	// Comments
	public static String comments = "Comments";
	// D/C Date
	public static String codedDate = "Coded Date";//"D/C Date";
	//Client Name
	public static String clientName = "Client Name";
	//Insurance
	public static String insurance = "Insurance";
	public static String chartType = "Category";
	
	public static String serviceType = "Service Type";
	public static String campusCode = "Campus Code";
	
	public static Map<String,FieldDetails> getMandatoryColumns() {
		Map<String, FieldDetails> columns = new LinkedHashMap<>();
		columns.put(accountNumber,new FieldDetails("accountNumber", String.class));
		columns.put(mrNumber, new FieldDetails("mrNumber", String.class));
		columns.put(patientName, new FieldDetails("patientName", String.class));
		columns.put(dischargedDate, new FieldDetails("dischargedDate", Date.class));
		columns.put(admittedDate, new FieldDetails("admittedDate", Date.class));
		columns.put(los, new FieldDetails("los", Integer.class));
		columns.put(tat, new FieldDetails("tat", Integer.class));
		columns.put(dollarAmount, new FieldDetails("dollarAmount", Integer.class));
		columns.put(em, new FieldDetails("effortMetric", Double.class));
		columns.put(specialization, new FieldDetails("specialization", String.class));
		//clientName
		columns.put(clientName, new FieldDetails("clientName", String.class));
		// Coder
		columns.put(coderName, new FieldDetails("coderName", String.class));
		//Comments 
		columns.put(comments, new FieldDetails("comments", String.class));
		// CodedDate // D/C Date
		columns.put(codedDate, new FieldDetails("codedDate", Date.class));
		//Insurance
		columns.put(insurance, new FieldDetails("insurance", String.class));
		//ChartType i.e Category
		columns.put(chartType, new FieldDetails("chartType", String.class));
		
		//Adding on 15-Dec-14 for ORHS worklist template
		//Service Type
		columns.put(serviceType, new FieldDetails("serviceType", String.class));
		//Campus Code
		columns.put(campusCode, new FieldDetails("campusCode", String.class));
		return columns;
	}
	
	public static List<String> getIncompleteSheetColumns() {
		List<String> list = new ArrayList<String>();
		list.add(fieldAccountNumber);
		list.add(coderName);
		list.add(comments);
		list.add(codedDate);
		return list;
	}
	
	public static Map getInitialValues() {
		
		Map<String , Integer> map = new HashMap<>();
		map.put(accountNumber, 0);
		map.put(patientName, 0);
		map.put(dischargedDate, 0);
		map.put(admittedDate, 0);
		map.put(los, 0);
		map.put(tat, 0);
		map.put(dollarAmount, 0);
		//map.put(specialization, 0);
		return map;
		
	}
	
	public static String getClientName() {
		return clientName;
	}

	public static void setClientName(String clientName) {
		CommonHeaders.clientName = clientName;
	}

	public static String getCoderName() {
		return coderName;
	}

	public static void setCoderName(String coderName) {
		CommonHeaders.coderName = coderName;
	}

	public static String getComments() {
		return comments;
	}

	public static void setComments(String comments) {
		CommonHeaders.comments = comments;
	}

	public static String getCodedDate() {
		return codedDate;
	}

	public static void setCodedDate(String codedDate) {
		CommonHeaders.codedDate = codedDate;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		CommonHeaders.accountNumber = accountNumber;
	}

	public static String getMrNumber() {
		return mrNumber;
	}

	public static void setMrNumber(String mrNumber) {
		CommonHeaders.mrNumber = mrNumber;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		CommonHeaders.patientName = patientName;
	}

	public String getDischargedDate() {
		return dischargedDate;
	}

	public void setDischargedDate(String dischargedDate) {
		CommonHeaders.dischargedDate = dischargedDate;
	}

	public String getAdmittedDate() {
		return admittedDate;
	}

	public void setAdmittedDate(String admittedDate) {
		CommonHeaders.admittedDate = admittedDate;
	}

	public String getLos() {
		return los;
	}

	public void setLos(String los) {
		CommonHeaders.los = los;
	}

	public String getTat() {
		return tat;
	}

	public void setTat(String tat) {
		CommonHeaders.tat = tat;
	}

	public String getDollarAmount() {
		return dollarAmount;
	}

	public void setDollarAmount(String dollarAmount) {
		CommonHeaders.dollarAmount = dollarAmount;
	}
	
	public static String getInsurance() {
		return insurance;
	}

	public static void setInsurance(String insurance) {
		CommonHeaders.insurance = insurance;
	}	
	
	public static String getChartType() {
		return chartType;
	}

	public static void setChartType(String chartType) {
		CommonHeaders.chartType = chartType;
	}

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		for (Object values : getMandatoryColumns().values()) {
			System.out.println(values.toString());
			if (values == String.class) {
				System.out.println("Its a String");
			}
		}
		Long millis = System.currentTimeMillis() - new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000).getTime();
		Double hrsDiff = millis/(60.0 * 60.0 * 1000);
		System.out.println("hrsDiff : " + hrsDiff);
		DecimalFormat df = new DecimalFormat("##0.00");
		//String totalHrs = (df.format(24 - hrsDiff));
		System.out.println(convertToHrsMins(df.format(24 - hrsDiff)));
		//System.out.println(" =====> " + totalHrs  + " %%%% " + System.currentTimeMillis()/(60.0 * 60.0 * 1000));
		
		System.out.println(convertToHrsMins("22.50"));
		
	}
	
	private static  String convertToHrsMins(String totalDecimalHrs){
		String[] array = totalDecimalHrs.toString().split("\\.");
		String Hrs = array[0];
		System.out.println("totalDecimalHrs : " +totalDecimalHrs +" &&array[0] : " + array[0] + " && array[1] : " + array[1]);
		Integer Mins = (Integer.parseInt(array[1]) * 60/100); 
		DecimalFormat df = new DecimalFormat("##0.00");
		//System.out.println(" Hrs : " + Hrs + " && Mins : " + (Mins) + " &&&&&&&&&&&&&&&&&&"+ df.format(Mins));
		return Integer.parseInt(Hrs) + ":" + Mins ;
	}
}
