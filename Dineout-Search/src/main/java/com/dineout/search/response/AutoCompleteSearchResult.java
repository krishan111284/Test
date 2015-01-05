package com.dineout.search.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AutoCompleteSearchResult {
	private Map<String, List<AutoCompleteSuggestionEntry>> suggestionsMap = new LinkedHashMap<String, List<AutoCompleteSuggestionEntry>>();

	public Map<String, List<AutoCompleteSuggestionEntry>> getSuggestionsMap() {
		return suggestionsMap;
	}

	public void setSuggestionsMap(
			Map<String, List<AutoCompleteSuggestionEntry>> suggestionsMap) {
		this.suggestionsMap = suggestionsMap;
	}
	
}
