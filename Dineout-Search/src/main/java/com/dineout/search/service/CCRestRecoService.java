package com.dineout.search.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.CCRestRecoQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.DOResponseUtils;

@Service("ccRestRecoService")
public class CCRestRecoService implements RecoSearchService{

	Logger logger = Logger.getLogger(CCRestRecoService.class);
	@Autowired
	CCRestRecoQueryCreator ccRestRecoQueryCreator;

	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public List<DORecoResult> getSearchResults(DORestSearchRequest request, SearchErrors errors) {
		List<DORecoResult> result = new ArrayList<DORecoResult>();
		QueryParam doqp = null;
		QueryResponse qresp = null;
		try {
			SolrServer server = solrConnectionUtils.getCCRecoSolrServer();
			doqp = ccRestRecoQueryCreator.getSearchQuery(request, null);
			qresp = server.query(doqp);

			if(qresp!=null){
				DORecoResult serachRes = null;
				serachRes = DOResponseUtils.processCCRecoQueryResponse(qresp);
				result.add(serachRes);
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}

		return result;
	}

}
