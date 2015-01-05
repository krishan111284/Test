package com.dineout.search.service;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.DOAutoSearchRequest;
import com.dineout.search.response.DOAutoCompleteSearchResult;

public interface DOAutoCompleteService {
	public DOAutoCompleteSearchResult getSuggestion(DOAutoSearchRequest request, SearchErrors errors);

}
