package com.eclat.mcws.util.rest;

import com.eclat.mcws.util.rest.Response.ErrorType;
import com.eclat.mcws.util.rest.Response.ResponseStatus;


public class ResponseBuilder {
	
	public static <T> Response<T> buildSuccessResponse(T payLoad, String message) {
		Response<T> response = new Response<T>();
		response.setMessage(message);
		response.setPayLoad(payLoad);
		return response;
	}
	
	public static <T> Response<T> buildErrorResponse(ErrorType type, String code, String message) {
		return new Response<T>(new Error(type, code, message));
	}
	
	public static <T> Response<T> buildOkResponse() {
		return new Response<T>(ResponseStatus.OK);
	}
	
}
