package com.dineout.search.service;

import java.util.ArrayList;
import java.util.List;

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
import com.dineout.search.query.QueryParam;
import com.dineout.search.query.RecoQueryCreator;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.DOResponseUtils;

@Service("similarRecoSearchService")
public class SimilarRecoSearchService implements RecoSearchService{

	Logger logger = Logger.getLogger(SimilarRecoSearchService.class);
	@Autowired
	RecoQueryCreator recoQueryCreator;

	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	@Override
	public List<DORecoResult> getSearchResults(DORestSearchRequest request, SearchErrors errors) {

		List<DORecoResult> result = new ArrayList<DORecoResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getRestSolrServer();
			doqp = recoQueryCreator.getIdSearchQuery(request);
			qres = server.query(doqp);
			if(qres!=null && qres.getResults().iterator().hasNext()){
				SolrDocument restDoc =	qres.getResults().iterator().next();
				request.setByprice(restDoc.get("costFor2")!=null?new String[] {Float.toString((Float)restDoc.get("costFor2"))}:null);
				request.setAvg_rating(restDoc.get("avg_rating")!=null?Float.toString((Float)restDoc.get("avg_rating")):null);
				request.setBycuisine(restDoc.get("cuisine")!=null?DOResponseUtils.getData(restDoc.get("cuisine")):null);
				request.setBytags(restDoc.get("tags")!=null?new String[]{(String)restDoc.get("tags")}:null);
				request.setBycity(restDoc.get("city_name")!=null?(String)restDoc.get("city_name"):null);

				if(restDoc.get("lat_lng")!=null)
				{
					String[] lat_lng = ((String)restDoc.get("lat_lng")).split(",");
					request.setLat(lat_lng[0]);
					request.setLng(lat_lng[1]);
				}

				doqp = recoQueryCreator.getSimilarRestaurantQuery(request);
				QueryResponse qresp = server.query(doqp);

				if(qresp!=null){
					DORecoResult serachRes = null;
					serachRes = DOResponseUtils.processRecoQueryResponse(qresp);
					result.add(serachRes);
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

		return result;
	}

}
