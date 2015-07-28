package com.eclat.mcws.enums;

public enum UserLocations {
	HYDERABAD("Hyderabad"),
	KARIMNAGAR("Karim Nagar"), 
	BANGLOR("Banglor"), 
	USA("USA");

	private String location;

	public String getLocation() {
		return location;
	}

	private UserLocations(String location) {
		this.location = location;
	}

	public static String[] getValues(){
		return new String[]{"Hyderabad", "Karim Nagar", "Banglor", "USA"};
	}
}
