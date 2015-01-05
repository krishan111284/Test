package com.dineout.search.response;

import java.util.Map;

public class DOSpellCheck {
	Map<String,Object> suggestions;

	public Map<String, Object> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(Map<String, Object> suggestions) {
		this.suggestions = suggestions;
	}
	
}
