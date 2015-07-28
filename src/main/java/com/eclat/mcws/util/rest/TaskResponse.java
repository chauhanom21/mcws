package com.eclat.mcws.util.rest;


public class TaskResponse<T> extends Response<T> {
	
	
	private Boolean enableTaskOption = true;	
	
	private Boolean enableCoderTaskOption = true;	
	
	public TaskResponse(ResponseStatus status) {
		super(status);
	}
	
	public TaskResponse() {
		this(ResponseStatus.OK);
	}
	
	public TaskResponse(Error error) {
		super(error);
	}	

	public Boolean getEnableTaskOption() {
		return enableTaskOption;
	}

	public void setEnableTaskOption(Boolean enableTaskOption) {
		this.enableTaskOption = enableTaskOption;
	}

	public Boolean getEnableCoderTaskOption() {
		return enableCoderTaskOption;
	}

	public void setEnableCoderTaskOption(Boolean enableCoderTaskOption) {
		this.enableCoderTaskOption = enableCoderTaskOption;
	}
		
}
