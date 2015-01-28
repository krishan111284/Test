package com.dineout.search.response;

import java.util.ArrayList;
import java.util.Map;

import com.dineout.search.exception.SearchErrors;



public class Header {
	
	private String responseType;
	private String status;
	SearchErrors errors;
	private Map<String,ArrayList<String>> nerEntities;
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
	public Map<String, ArrayList<String>> getNerEntities() {
		return nerEntities;
	}
	public void setNerEntities(Map<String, ArrayList<String>> nerEntities) {
		this.nerEntities = nerEntities;
	}
}
