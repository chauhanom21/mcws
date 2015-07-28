package com.eclat.mcws.util.rest;

public class Response<T> {

	public enum ResponseStatus {
		OK, WARNING, ERROR, VALIDATION_ERROR, DATA_NOT_FOUND;
	}

	public enum ErrorType {
		VALIDATION_ERROR, NOT_FOUND, ERROR, LOGIN_ERROR, DUPLICATE_ENTRY;
	}

	private ResponseStatus status;

	private final Error error;

	private String message;

	private Boolean enableTaskOption = true;

	private T payLoad;

	private Integer statusCode;

	public Response(ResponseStatus status) {
		this.setStatus(status);
		this.error = null;
	}

	public Response() {
		this(ResponseStatus.OK);
	}

	public Response(Error error) {
		this.setStatus(ResponseStatus.ERROR);
		this.setPayLoad(null);
		this.error = error;
		this.message = error.getMessage();
		this.setStatusCode(501);
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public Error getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(T payLoad) {
		this.payLoad = payLoad;
	}

	public Boolean getEnableTaskOption() {
		return enableTaskOption;
	}

	public void setEnableTaskOption(Boolean enableTaskOption) {
		this.enableTaskOption = enableTaskOption;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

}
