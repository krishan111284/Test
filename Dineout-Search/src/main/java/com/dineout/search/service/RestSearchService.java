package com.dineout.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DOSearchResult;


public interface RestSearchService {
	public List<DOSearchResult> getSearchResults(DORestSearchRequest tcMainSearchReq,SearchErrors errors,Map<String, ArrayList<String>> nerMap);
}
