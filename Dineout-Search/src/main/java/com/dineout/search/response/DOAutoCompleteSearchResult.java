package com.dineout.search.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DOAutoCompleteSearchResult {
	private Map<String, List<DOAutoCompleteSuggestionEntry>> suggestionsMap = new LinkedHashMap<String, List<DOAutoCompleteSuggestionEntry>>();

	public Map<String, List<DOAutoCompleteSuggestionEntry>> getSuggestionsMap() {
		return suggestionsMap;
	}

	public void setSuggestionsMap(
			Map<String, List<DOAutoCompleteSuggestionEntry>> suggestionsMap) {
		this.suggestionsMap = suggestionsMap;
	}
	
}
