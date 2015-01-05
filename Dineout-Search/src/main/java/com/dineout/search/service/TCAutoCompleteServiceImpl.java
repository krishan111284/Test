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
import com.dineout.search.query.AutoCompleteQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.AutocompleteSearchRequest;
import com.dineout.search.response.AutoCompleteSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.AutoCompleteResponseUtils;
import com.dineout.search.utils.Constants;

@Service("tcAutoCompleteService")
public class TCAutoCompleteServiceImpl implements AutoCompleteService{
	
	Logger logger = Logger.getLogger(TCAutoCompleteServiceImpl.class);
	
	@Autowired
	AutoCompleteQueryCreator tcAutoCompleteQueryCreator;
	
	@Autowired
	SolrConnectionUtils solrConnectionUtils;
	
	@Override
	public AutoCompleteSearchResult getSuggestion(AutocompleteSearchRequest req, SearchErrors errors) {
		QueryParam query = null;
		AutoCompleteSearchResult result = null;
		query = tcAutoCompleteQueryCreator.getAutoSuggestQuery(req);
		SolrServer server = solrConnectionUtils.getAutoSolrServer();
		QueryResponse qres = null;
		try {
			qres = server.query(query);
			if(qres!=null){
				result = AutoCompleteResponseUtils.processGroupQueryResponse(qres);
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

}
