package com.eclat.mcws.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.TaskDetails;

@Component
public class JsonConstructor {
	
	@Log
	private Logger logger;
	
	public String constructColumnValCount(TaskDetails taskDetail){
		if(taskDetail != null) {
			logger.debug("*********** CodingData ::: "+taskDetail.getCodingData());
			if(taskDetail.getCodingData() != null && taskDetail.getCodingData().trim().length() > 0){
				Map<String, Object> map = CommonOperations.parseJsonData(taskDetail.getCodingData());
				JSONObject json  = new JSONObject();
				try {
					for(String key : map.keySet()) {
						final Object value = map.get(key);
						if(value != null && !"-".equalsIgnoreCase(value.toString().trim())) {
							if(value instanceof String || value instanceof Integer) {
								json.put(key, 1);
							} else if(value instanceof ArrayList){
								int count = 0;
								
								if(key.equalsIgnoreCase("icdGroup") || key.equalsIgnoreCase("sdxpoaGroup")
										|| key.equalsIgnoreCase("geCptGroup") || key.equalsIgnoreCase("ipIcdGroup")
										|| key.equalsIgnoreCase("rcGroup")){
									
									populateGroupArrayValueCount(json, (ArrayList)value);
								}
								for(Object val: (ArrayList)value) {
									if(val instanceof ArrayList){
										//Ignore junk Value
									} else {
										final LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>)val;
										Object Value =  lhm.get("value");
										if(Value != null && !"-".equalsIgnoreCase(Value.toString().trim())){
											count++;
										}
									}
								}
								json.put(key, count);
							}
						} else {
							json.put(key, 0);
						}
					}
				} catch(Exception e){
					logger.error("Got Exception While Constructing column length : : : " , e);
				}
				logger.debug("====> columnValCount : " +json.toJSONString());
				taskDetail.setColumnValCount(json);
			}
		}
		return null;
	}
	
	private void populateGroupArrayValueCount(JSONObject json, ArrayList value){
		getJsonKeyValueCount("icd", json, (ArrayList)value);
		getJsonKeyValueCount("cpt", json, (ArrayList)value);
		getJsonKeyValueCount("mod", json, (ArrayList)value);
		getJsonKeyValueCount("phy", json, (ArrayList)value);
		getJsonKeyValueCount("dos", json, (ArrayList)value);
		getJsonKeyValueCount("revenueCode", json, (ArrayList)value);
		getJsonKeyValueCount("sdx", json, (ArrayList)value);
		getJsonKeyValueCount("sdx_poa", json, (ArrayList)value);
	}
	
	private void getJsonKeyValueCount(final String key, JSONObject json, ArrayList value) {
		boolean addtoJson = false;
		int count = 0;
		for(Object val: (ArrayList)value) {
			if(val instanceof ArrayList){
				//Ignore junk Value
			} else {
				final LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>)val;
				Object obj =  lhm.get(key);
				if(obj instanceof ArrayList){
					for(Object groupVal: (ArrayList)obj) {
						final LinkedHashMap<String, Object> sublhm = (LinkedHashMap<String, Object>)groupVal;
						Object subValue =  sublhm.get("value");
						if(subValue != null && !"undefined".equalsIgnoreCase(subValue.toString())) {
							addtoJson = true;
							if(!"-".equalsIgnoreCase(subValue.toString().trim())) {
								count++;
							}
						}
					}
				}
			}
		}
		
		if(addtoJson)
			json.put(key, count);
	}
}
