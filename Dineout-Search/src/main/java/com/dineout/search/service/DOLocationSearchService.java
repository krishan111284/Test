package com.dineout.search.service;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.DOLocationSearchRequest;
import com.dineout.search.response.DOLocationSearchResult;

public interface DOLocationSearchService {
	public DOLocationSearchResult getSuggestion(DOLocationSearchRequest req, SearchErrors errors);

}
