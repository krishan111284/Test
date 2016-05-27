package com.dineout.search.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class DOAutoCompleteSearchResult {
	private Map<String,Object> suggestionsMap = new LinkedHashMap<String,Object>();

	public Map<String,Object> getSuggestionsMap() {
		return suggestionsMap;
	}

	public void setSuggestionsMap(Map<String,Object> suggestionsMap) {
		this.suggestionsMap = suggestionsMap;
	}
	
}
