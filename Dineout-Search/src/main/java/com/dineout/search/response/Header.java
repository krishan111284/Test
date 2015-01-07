package com.dineout.search.response;

import java.util.Map;

import com.dineout.search.exception.SearchErrors;



public class Header {
	
	private String responseType;
	private String status;
	SearchErrors errors;
	private Map<String,String> nerEntities;
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public SearchErrors getErrors() {
		return errors;
	}
	public void setErrors(SearchErrors errors) {
		this.errors = errors;
	}
	public Map<String, String> getNerEntities() {
		return nerEntities;
	}
	public void setNerEntities(Map<String, String> nerEntities) {
		this.nerEntities = nerEntities;
	}
}
