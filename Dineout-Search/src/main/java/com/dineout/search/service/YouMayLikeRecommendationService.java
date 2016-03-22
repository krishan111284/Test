package com.dineout.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.DORestQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.query.RecommendationQueryCreator;
import com.dineout.search.request.RecommendationRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.DOResponseUtils;

@Service("youMayLikeRecommendationService")
public class YouMayLikeRecommendationService implements RecommendationService{
	Logger logger = Logger.getLogger(YouMayLikeRecommendationService.class);

	@Autowired
	RecommendationQueryCreator recommendationQueryCreator;
	@Autowired
	DORestQueryCreator restQueryCreator;
	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public List<DORecoResult> getRecommendedResults(RecommendationRequest request, SearchErrors errors) {
		List<DORecoResult> resultList = new ArrayList<DORecoResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getDinerSolrServer();
			doqp = recommendationQueryCreator.getDinerIdSearchQuery(request);
			qres = server.query(doqp);
			Map<String, Double> featureMap = new HashMap<String, Double>();
			if(qres!=null){
				Iterator<SolrDocument> resultIterator = qres.getResults().iterator();
				while(resultIterator.hasNext()){
					SolrDocument solrDoc = resultIterator.next();
					Iterator<String>fieldIterator = solrDoc.keySet().iterator();
					while(fieldIterator.hasNext()){
						String fieldName = fieldIterator.next();
						featureMap.put((String)fieldName, (Double) solrDoc.get(fieldName));
					}
				}
			}

			if(!featureMap.isEmpty()){
				server = solrConnectionUtils.getRestSolrServer();
				doqp = restQueryCreator.getDinerRecommendedRestaurants(request,featureMap);
				qres = server.query(doqp);
				if(qres!=null){
					DORecoResult result = DOResponseUtils.processRecommendationQueryResponse(qres);
					resultList.add(result);
				}

			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}

		return resultList;

	}
}
