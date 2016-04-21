package com.dineout.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.DOFeaturedQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DOResponseUtils;

@Service("featuredRestServiceImpl")
public class FeaturedRestServiceImpl{
	Logger logger = Logger.getLogger(FeaturedRestServiceImpl.class);
	@Autowired
	DOFeaturedQueryCreator featuredQueryCreator;
	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	public List<DOSearchResult> getSearchResults(DORestSearchRequest request,SearchErrors errors) {
		List<DOSearchResult> result = new ArrayList<DOSearchResult>();
		try {
			int limit = request.getLimit()!=null?Integer.parseInt(request.getLimit()):10;
			SolrServer server = solrConnectionUtils.getFeaturedSolrServer();
			DOSearchResult mainResult = new DOSearchResult();
			if(request.getBylocation()!=null && request.getBylocation().length>0){
				mainResult = getLocationResult(server,request,errors);
				if(mainResult.getDocs().size()<limit){
					DOSearchResult areaResult = getAreaResult(server,request,errors);
					mainResult.getDocs().addAll(areaResult.getDocs());
					if(mainResult.getDocs().size() < limit){
						DOSearchResult cityResult = getCityResult(server,request,errors);
						mainResult.getDocs().addAll(cityResult.getDocs());
					}	
				}
			}
			else if (request.getByarea()!=null && request.getByarea().length>0){
				mainResult = getAreaResult(server,request,errors);
				if(mainResult.getDocs().size() < limit){
					DOSearchResult cityResult = getCityResult(server,request,errors);
					mainResult.getDocs().addAll(cityResult.getDocs());
				}
			}
			else{
				mainResult = getCityResult(server,request,errors);
			}

			if(mainResult.getDocs().size()>limit){
				List<Map<Object, Object>> docs = mainResult.getDocs().subList(0, limit);
				mainResult.setDocs(docs);
			}
			mainResult.setMatches(mainResult.getDocs().size());
			result.add(mainResult);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return result;
	}

	private DOSearchResult getLocationResult(SolrServer server, DORestSearchRequest request, SearchErrors errors) {
		QueryParam doqp = null;
		QueryResponse qres = null;
		DOSearchResult searchRes = null;
		try {
			doqp = featuredQueryCreator.getLocationFeaturedQuery(request);
			qres = server.query(doqp);
			if(qres!=null){
				searchRes = DOResponseUtils.processFeaturedQueryResponse(qres,Constants.RESPONSE_TYPE_FEATURED,request.isSpellcheckApplied());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return searchRes;
	}

	private DOSearchResult getAreaResult(SolrServer server, DORestSearchRequest request, SearchErrors errors) {
		QueryParam doqp = null;
		QueryResponse qres = null;
		DOSearchResult searchRes = null;
		try {
			doqp = featuredQueryCreator.getAreaFeaturedQuery(request);
			qres = server.query(doqp);
			if(qres!=null){
				searchRes = DOResponseUtils.processFeaturedQueryResponse(qres,Constants.RESPONSE_TYPE_FEATURED,request.isSpellcheckApplied());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return searchRes;
	}

	private DOSearchResult getCityResult(SolrServer server, DORestSearchRequest request, SearchErrors errors) {
		QueryParam doqp = null;
		QueryResponse qres = null;
		DOSearchResult searchRes = null;
		try {
			doqp = featuredQueryCreator.getCityFeaturedQuery(request);
			qres = server.query(doqp);
			if(qres!=null){
				searchRes = DOResponseUtils.processFeaturedQueryResponse(qres,Constants.RESPONSE_TYPE_FEATURED,request.isSpellcheckApplied());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return searchRes;
	}
}
