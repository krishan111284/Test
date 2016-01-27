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
import com.dineout.search.query.RecoQueryCreator;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.DOResponseUtils;

@Service("userRecoSearchService")
public class UserRecoSearchService {

	Logger logger = Logger.getLogger(UserRecoSearchService.class);
	@Autowired
	RecoQueryCreator recoQueryCreator;

	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	public List<DOSearchResult> getSearchResults(DORestSearchRequest request, SearchErrors errors) {

		List<DOSearchResult> result = new ArrayList<DOSearchResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			List<Map<Object, Object>> docList = null;
			SolrServer server = solrConnectionUtils.getCollaborativeSolrServerSolrServer();
			doqp = recoQueryCreator.getUserIdSearchQuery(request);
			qres = server.query(doqp);
			if(qres!=null ){
				docList = DOResponseUtils.getTCDocList(qres);
			}
			doqp = recoQueryCreator.getUserRecoQuery(docList);
			server = solrConnectionUtils.getRestSolrServer();
			QueryResponse qresp = server.query(doqp);

			if(qresp!=null){
				DOSearchResult serachRes = null;
				serachRes = DOResponseUtils.processOptQueryResponse(qresp, "UserReco",docList);
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
