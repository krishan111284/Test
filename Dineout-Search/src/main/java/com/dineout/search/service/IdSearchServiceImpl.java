package com.dineout.search.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import com.dineout.search.query.IdSearchQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DOResponseUtils;

@Service("idSearchServiceImpl")
public class IdSearchServiceImpl implements RestSearchService {
	Logger logger = Logger.getLogger(IdSearchServiceImpl.class);

	@Autowired
	IdSearchQueryCreator idSearchQueryCreator;

	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public List<DOSearchResult> getSearchResults(DORestSearchRequest request, SearchErrors errors,
			Map<String, ArrayList<String>> nerMap) {
		List<DOSearchResult> result = new ArrayList<DOSearchResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getRestSolrServer();

			doqp = idSearchQueryCreator.getSearchQuery(request,nerMap);
			qres = server.query(doqp);
			if(qres!=null){
				DOSearchResult serachRes = null;
				serachRes = DOResponseUtils.processIdQueryResponse(qres,Constants.RESPONSE_TYPE_REST);
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

	public List<DORecoResult> getPopularTrendingSearchResults(DORestSearchRequest request, SearchErrors errors,
			LinkedHashMap<String, Long> facetMap) {
		List<DORecoResult> result = new ArrayList<DORecoResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getRestSolrServer();

			doqp = idSearchQueryCreator.getSearchQuery(request,null);
			qres = server.query(doqp);
			if(qres!=null){
				DORecoResult doRecoResult = null;
				doRecoResult = DOResponseUtils.processPopularTrendingResponse(qres,Constants.RESPONSE_TYPE_REST,facetMap);
				result.add(doRecoResult);
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
