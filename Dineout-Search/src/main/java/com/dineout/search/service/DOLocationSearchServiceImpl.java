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
import com.dineout.search.query.DOLocationQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DOLocationSearchRequest;
import com.dineout.search.response.DOLocationSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.DOAutoCompleteResponseUtils;

@Service("doLocationSearchServiceImpl")
public class DOLocationSearchServiceImpl implements DOLocationSearchService{

	Logger logger = Logger.getLogger(DOLocationSearchServiceImpl.class);

	@Autowired
	DOLocationQueryCreator locationQueryCreator;

	@Autowired
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public DOLocationSearchResult getSuggestion(DOLocationSearchRequest req, SearchErrors errors) {
		SolrServer server = solrConnectionUtils.getAutoSolrServer();
		DOLocationSearchResult result =null;
		if(req.isGPSQuery()){
			QueryParam query = null;
			query = locationQueryCreator.getGPSLocationSearchQuery(req,errors);
			QueryResponse qres = null;
			try {
				qres = server.query(query);
				if(qres!=null){
					result = DOAutoCompleteResponseUtils.processLocationGroupQueryResponse(qres);
				}
			} catch (SolrServerException e) {
				logger.error(e.getMessage(),e);
				SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
				errors.add(error);
			}
		}
		else{
			result = getLocationAreaCitySuggestion(req, errors,server);
		}
		return result;
	}

	public DOLocationSearchResult getLocationAreaCitySuggestion(DOLocationSearchRequest req, SearchErrors errors, SolrServer server) {
		QueryParam query = null;
		DOLocationSearchResult result = null,areaCityResult = null;
		query = locationQueryCreator.getAreaCitySearchQuery(req,errors);
		QueryResponse qres = null;
		try {
			qres = server.query(query);
			if(qres!=null){
				areaCityResult = DOAutoCompleteResponseUtils.processLocationGroupQueryResponse(qres);
				DOLocationSearchResult locationResult = getLocationSuggestion(req, errors, server);
				result = DOAutoCompleteResponseUtils.processMultipleResponse(areaCityResult, locationResult);

			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

	public DOLocationSearchResult getLocationSuggestion(DOLocationSearchRequest req, SearchErrors errors, SolrServer server) {
		QueryParam query = null;
		DOLocationSearchResult result = null;
		query = locationQueryCreator.getLocationSearchQuery(req,errors);
		QueryResponse qres = null;
		try {
			qres = server.query(query);
			if(qres!=null){
				result = DOAutoCompleteResponseUtils.processLocationGroupQueryResponse(qres);
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

}
