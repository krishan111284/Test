package com.dineout.search.service;

import java.util.List;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DORecoResult;

public interface RecoSearchService {
	public List<DORecoResult> getSearchResults(DORestSearchRequest req,SearchErrors errors);
}
