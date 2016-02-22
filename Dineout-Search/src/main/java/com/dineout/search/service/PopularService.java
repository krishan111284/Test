package com.dineout.search.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.ESQueryCreator;
import com.dineout.search.request.PopularTrendingRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.server.HttpESConnectionFactory;

@Service("popularService")
public class PopularService {

	@Autowired
	HttpESConnectionFactory httpESConnectionFactory;

	@Autowired
	ESQueryCreator esQueryCreator;

	@Autowired
	IdSearchServiceImpl idSearchServiceImpl;

	public List<DORecoResult> getPopularRestaurants(PopularTrendingRequest request, SearchErrors errors){
		LinkedHashMap<String, Long> facetMap = new LinkedHashMap<String, Long>();
		Client client = httpESConnectionFactory.getESSolrServer();

		SearchRequestBuilder srb = esQueryCreator.getSearchQuery(request, client);
		SearchResponse response = srb.execute().actionGet();
		Terms terms = response.getAggregations().get("popularRestaurantsFacet");
		List<Bucket>buckets = (List<Bucket>) terms.getBuckets();
		for(Bucket bucket:buckets)
			facetMap.put(bucket.getKey(), bucket.getDocCount());
		List<DORecoResult> result =  fetchRestaurantsFromSolr(facetMap,request,errors);
		return result;
	}

	public List<DORecoResult> fetchRestaurantsFromSolr(LinkedHashMap<String, Long> facetMap, PopularTrendingRequest request, SearchErrors errors){
		Set<String> keys = facetMap.keySet();
		String[] restIds = keys.toArray(new String[keys.size()]);
		request.setRestIds(restIds);
		List<DORecoResult> result = idSearchServiceImpl.getPopularTrendingSearchResults(request, errors, facetMap);
		return result;
	}
}
