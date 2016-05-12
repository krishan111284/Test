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
import com.dineout.search.query.DOTicketQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.DOTicketSearchRequest;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DOResponseUtils;

@Service("ticketServiceImpl")
public class TicketServiceImpl{
	Logger logger = Logger.getLogger(TicketServiceImpl.class);
	@Autowired
	DOTicketQueryCreator doTicketQueryCreator;

	@Autowired 
	SolrConnectionUtils solrConnectionUtils;

	public List<DOSearchResult> getSearchResults(DOTicketSearchRequest request, SearchErrors errors, Map<String,ArrayList<String>> nerMap) {
		List<DOSearchResult> result = new ArrayList<DOSearchResult>();
		QueryParam doqp = null;
		QueryResponse qres = null;
		try {
			SolrServer server = solrConnectionUtils.getEventDealsSolrServer();

			doqp = doTicketQueryCreator.getSearchQuery(request, nerMap);
			qres = server.query(doqp);
			if(qres!=null){
				DOSearchResult serachRes = null;
				if(Constants.GROUP_TRUE.equals(request.getGroup())){
					serachRes = DOResponseUtils.processTicketGroupQueryResponse(qres,Constants.RESPONSE_TYPE_TICKET,request);
				}else{
					serachRes = DOResponseUtils.processQueryResponse(qres,Constants.RESPONSE_TYPE_TICKET,request.isSpellcheckApplied());
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
