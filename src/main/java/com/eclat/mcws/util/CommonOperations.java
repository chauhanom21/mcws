package com.eclat.mcws.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.eclat.mcws.dto.TaskDetails;
import com.eclat.mcws.enums.ChartStatus;
import com.eclat.mcws.enums.ChartTypes;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.entity.Coders;

public class CommonOperations {
	
	public static final int TWENTY_THIRD = 23;
	public static final int FIFTY_NINE = 59;
	public static final int ONE = 1;
	
	public static Date convertStringValueToDate(String date){
		Calendar cal = new GregorianCalendar();
		if(date != null && date.length() > 0) {
			final String[] values = date.split("-");
			cal.set(Integer.parseInt(values[0]), Integer.parseInt(values[1]) - 1, Integer.parseInt(values[2]));
		}  else {
			cal.add(Calendar.DATE, -ONE);
		}
		
		return new Date(cal.getTimeInMillis());		
	}
	
	public static int checkForNull(Integer value) {
		int result = 0;
		if (value != null) {
			result = value;
		}
		return result;
	}
	
	public static Map<String, Object> parseJsonData(final String jsonData) {

		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			// convert JSON string to Map
			map = mapper.readValue(jsonData, new TypeReference<HashMap<String, Object>>() {
			});
			return map;
		} catch (Exception e) {	
			e.printStackTrace();
			return null;
		}

	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertStringDataToJsonArray(List<String> values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (String str : values) {
			jsonObj = new JSONObject();
			jsonObj.put("name", str);
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertIdsToJsonArray(List<Integer> values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (Integer id : values) {
			jsonObj = new JSONObject();
			jsonObj.put("id", id);
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}

	@SuppressWarnings("unchecked")
	public static JSONArray convertUserRolesToJson(UserRoles[] values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (UserRoles str : values) {
			jsonObj = new JSONObject();
			jsonObj.put("role", str);
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertUserLocationToJson(String[] values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (String str : values) {
			jsonObj = new JSONObject();
			jsonObj.put("location", str);
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertList2Json(ChartStatus[] values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (ChartStatus str : values) {
			jsonObj = new JSONObject();
			jsonObj.put("text", str.getStatus());
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertList2Json(ChartTypes[] values) {
		JSONObject jsonObj;
		JSONArray jsonArray = new JSONArray();
		for (ChartTypes str : values) {
			jsonObj = new JSONObject();
			jsonObj.put("text", str.getChartType());
			jsonArray.add(jsonObj);
		}
		return jsonArray;

	}
	
	public static JSONObject parseJsonStringData(final String jsonData) {
		JSONParser parser = new JSONParser();
		try {
			final Object obj = parser.parse(jsonData);
			return (JSONObject) obj;

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Method used to evaluate to check the entire input list contains only incomplete list .
	 * Returns true if all contains the Incomplete elements
	 * @param items
	 * @return
	 */
	public static boolean checkListBasedOnStatus(List<TaskDetails> items) {
		int count = 0; 
		boolean result = false;
		for (TaskDetails task : items) {
			if (task.getStatus().equals(ChartStatus.InComplete.getStatus())) {
				count ++;
			}
		}
		if (count == items.size()) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 
	 * @param coder
	 * @param isCoder
	 * @return This return the role of the user, if isCoder true then return coder role 
	 * else check if QA is remote or local based on that It returns the user role.
	 */
	public static String getUserReviewedRole(final Coders coder, final boolean isCoder){
		if (isCoder) {
			return UserRoles.Coder.toString();
		} else if (coder.getLocalQa()) {
			return UserRoles.LocalQA.toString();
		}else if (coder.getRemoteQa()){
				return UserRoles.RemoteQA.toString();
		}
		return null;
	}
	
	public static String findRoleOfUser(final Coders coder) {
		if (coder.getRemoteQa()) {
			return UserRoles.RemoteQA.toString();
		} else if (coder.getLocalQa()) {
			return UserRoles.LocalQA.toString();
		} else if (coder.getCoder()) {
			return UserRoles.Coder.toString();
		}
		return null;
	}
	
	public static Calendar convertStringValueToDateFormat(final String date){
		Calendar cal = new GregorianCalendar();
		if(date != null && date.length() > 0) {
			final String[] values = date.split("/");
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(values[1]));
			cal.set(Calendar.MONTH, Integer.parseInt(values[0])- ONE);
			cal.set(Calendar.YEAR, Integer.parseInt(values[2]));
		}  else {
			cal.add(Calendar.DATE, -ONE);
		}
		return cal;		
	}

	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	public static List<String> convertIntegerListToStringList(List<Integer> ids) {
	    final List<String> strIds = new ArrayList<>();
		if(ids != null && ids.size() > 0){
	    	for(Integer id: ids){
	    		strIds.add(id.toString());
	    	}
	    }
	    return strIds;
	}
	
	public static String getEmployeeId(Object employeeId){
		String empId = "";
		if(employeeId instanceof Integer)
			empId = ((Integer)employeeId).toString();
		else
			empId = (String)employeeId;
		return empId;
	}
	
}
