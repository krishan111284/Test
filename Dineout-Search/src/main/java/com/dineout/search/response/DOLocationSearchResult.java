package com.dineout.search.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DOLocationSearchResult {
	private Map<String, List<DOLocationSearchResponseEntry>> suggestionsMap = new LinkedHashMap<String, List<DOLocationSearchResponseEntry>>();

	public Map<String, List<DOLocationSearchResponseEntry>> getSuggestionsMap() {
		return suggestionsMap;
	}

	public void setSuggestionsMap(
			Map<String, List<DOLocationSearchResponseEntry>> suggestionsMap) {
		this.suggestionsMap = suggestionsMap;
	}
	
}
