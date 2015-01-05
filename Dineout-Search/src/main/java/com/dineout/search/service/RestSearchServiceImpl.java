package com.dineout.search.service;

import java.util.ArrayList;
import java.util.List;
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
import com.dineout.search.query.QueryParam;
import com.dineout.search.query.RestQueryCreator;
import com.dineout.search.request.RestSearchRequest;
import com.dineout.search.response.SearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.ResponseUtils;

@Service("restSearchService")
public class RestSearchServiceImpl implements RestSearchService{
	Logger logger = Logger.getLogger(RestSearchServiceImpl.class);
	@Autowired
	RestQueryCreator restQueryCreator;
	
	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	public List<SearchResult> getSearchResults(RestSearchRequest request,SearchErrors errors,Map<String,String> nerMap) {
		List<SearchResult> result = new ArrayList<SearchResult>();
		QueryParam tcqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getRestSolrServer();
			
			tcqp = restQueryCreator.getSearchQuery(request,nerMap);
			qres = server.query(tcqp);
			if(qres!=null){
				SearchResult serachRes = null;
				if(request.isGrouprequest()){
					serachRes = ResponseUtils.processGroupQueryResponse(qres,null,request.isSpellcheckApplied());
				}else{
					serachRes = ResponseUtils.processQueryResponse(qres,null,request.isSpellcheckApplied());
				}
				result.add(serachRes);
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
		
		return result;
	}
}
