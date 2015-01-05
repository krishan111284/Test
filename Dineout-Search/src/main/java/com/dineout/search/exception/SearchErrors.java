package com.dineout.search.exception;

import java.util.ArrayList;
import java.util.List;

public class SearchErrors {
	private List<SearchError> errors = new ArrayList<SearchError>();

	public List<SearchError> getErrors() {
		return errors;
	}
	public void add(SearchError error){
		errors.add(error);
	}
	
	public boolean hasErrors(){
		return errors.size() != 0;
	}
	
	public void addAll(SearchErrors error){
		errors.addAll(error.getErrors());
	}
}
