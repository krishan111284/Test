package com.dineout.search.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.DOAutoCompleteQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DOAutoSearchRequest;
import com.dineout.search.response.DOAutoCompleteSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DOAutoCompleteResponseUtils;

@Service("doAutoCompleteService")
public class DOAutoCompleteServiceImpl implements DOAutoCompleteService{

	Logger logger = Logger.getLogger(DOAutoCompleteServiceImpl.class);

	@Autowired
	DOAutoCompleteQueryCreator doAutoCompleteQueryCreator;

	@Autowired
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public DOAutoCompleteSearchResult getSuggestion(DOAutoSearchRequest req, SearchErrors errors) {
		QueryParam query = null;
		DOAutoCompleteSearchResult result = null;
		try {
			SolrServer server = solrConnectionUtils.getAutoSolrServer();
			query = doAutoCompleteQueryCreator.getAutoSuggestQuery(req);
			QueryResponse qres = null;
			qres = server.query(query);
			if(qres!=null){
				result = DOAutoCompleteResponseUtils.processGroupQueryResponse(qres);
				if(req.getGroup()!=null && req.getGroup().equalsIgnoreCase(Constants.GROUP_TRUE)){
					Map<Object, Object> grRest = getGroupedRestaurants(req,errors,server);
					result.getSuggestionsMap().put("Restaurant", grRest);
				}
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

	private Map<Object, Object> getGroupedRestaurants(DOAutoSearchRequest req, SearchErrors errors, SolrServer server) {
		QueryParam query = null;
		QueryResponse qres = null;
		Map<Object, Object> grRest = new HashMap<Object, Object>();
		if (req.getGroup()!=null && req.getGroup().equalsIgnoreCase(Constants.GROUP_TRUE)){
			try {
				query = doAutoCompleteQueryCreator.getAutoSuggestRestGroupQuery(req);
				qres = server.query(query);
				grRest = DOAutoCompleteResponseUtils.processRestGroupQueryResponse(qres);

			} catch (SolrServerException e) {
				logger.error(e.getMessage(),e);
				SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
				errors.add(error);
			}
		}
		return grRest;
	}

}
