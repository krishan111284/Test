package com.dineout.search.service;

import java.util.List;
import java.util.Map;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.RestSearchRequest;
import com.dineout.search.response.SearchResult;


public interface RestSearchService {
	public List<SearchResult> getSearchResults(RestSearchRequest tcMainSearchReq,SearchErrors errors,Map<String,String> nerMap);
}
