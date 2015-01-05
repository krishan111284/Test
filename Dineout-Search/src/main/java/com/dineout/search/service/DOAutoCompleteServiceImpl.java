package com.dineout.search.service;

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
		query = doAutoCompleteQueryCreator.getAutoSuggestQuery(req);
		SolrServer server = solrConnectionUtils.getAutoSolrServer();
		QueryResponse qres = null;
		try {
			qres = server.query(query);
			if(qres!=null){
				result = DOAutoCompleteResponseUtils.processGroupQueryResponse(qres);
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

}
