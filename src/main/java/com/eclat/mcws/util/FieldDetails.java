package com.eclat.mcws.util;

public class FieldDetails {
	
	private String name;
	
	private Object dataType;
	
	public FieldDetails(String name, Object dataType) {
		this.name = name;
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getDataType() {
		return dataType;
	}

	public void setDataType(Object dataType) {
		this.dataType = dataType;
	}
	
	

}
