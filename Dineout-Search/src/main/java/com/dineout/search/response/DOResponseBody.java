package com.dineout.search.response;

import java.util.HashMap;
import java.util.Map;

public class DOResponseBody implements IResponseBody{
	
	private int numFound;
	private Map<String, Object> searchresult = new HashMap<String, Object>();
	
	
	public Map<String, Object> getSearchresult() {
		return searchresult;
	}
	public void setSearchresult(Map<String, Object> searchresult) {
		this.searchresult = searchresult;
	}
	public int getNumFound() {
		return numFound;
	}
	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}
	
	
	

}
