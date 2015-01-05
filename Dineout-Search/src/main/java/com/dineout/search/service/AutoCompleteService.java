package com.dineout.search.service;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.AutocompleteSearchRequest;
import com.dineout.search.response.AutoCompleteSearchResult;

public interface AutoCompleteService {
	public AutoCompleteSearchResult getSuggestion(AutocompleteSearchRequest request, SearchErrors errors);

}
