package com.dineout.search.service;

import java.util.List;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.RecommendationRequest;
import com.dineout.search.response.DORecoResult;

public interface RecommendationService {
	public List<DORecoResult> getRecommendedResults(RecommendationRequest req,SearchErrors errors);
}
