package com.eclat.mcws.util.rest;

import com.eclat.mcws.util.rest.Response.ErrorType;

public class Error {
	
	private final ErrorType type;
	
	private String code;

	private String message;
	
	public Error(ErrorType type, String code, String message) {
		this.type = type;
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ErrorType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "[" + getCode() + "] - " + getMessage();
	}
	
	
}
