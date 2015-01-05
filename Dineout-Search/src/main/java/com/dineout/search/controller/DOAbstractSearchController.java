package com.dineout.search.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;

import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.GenericDOSearchRequest;
import com.dineout.search.request.NerRequest;
import com.dineout.search.response.Header;
import com.dineout.search.response.DOResponseBody;
import com.dineout.search.response.DOSearchResponse;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.service.NerService;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.GsonUtil;
import com.dineout.search.utils.DORequestUtils;

public abstract class DOAbstractSearchController {
	@Autowired
	GsonUtil gsonUtil;
	@Autowired
	NerService nerServiceImpl;
	
	Logger logger = Logger.getLogger(DOAbstractSearchController.class);
	
	/**
	 * Processes the request:
	 * - Sets isSearchExecuted as true if query string is not empty.
	 * - Sets isSpatial to true if request has lat,lng,radius set. Processing of spatial queries depends on this flag.
	 * 
	 * @param request
	 */
	protected void processDOSearchRequest(GenericDOSearchRequest request){
		if(!StringUtils.isEmpty(request.getSearchname())){
			request.setSearchExecuted(true);
		}
		if (DORequestUtils.isSpatial(request)){
			request.setSpatialQuery(true);
		}
		if(!StringUtils.isEmpty(request.getSpellcheck()) && Constants.TC_SPELL_CHECK_TRUE.equals(request.getSpellcheck())){
			request.setSpellcheckApplied(true);
		}
		if(!StringUtils.isBlank(request.getNer()) &&  Constants.TC_NER_TRUE.equals(request.getNer())){
			request.setNerApplied(true);
		}
		
	}
	
	protected Map<String,String> getNerMap(GenericDOSearchRequest request){
		Map<String,String> nerMap = null;
		if(!StringUtils.isBlank(request.getSearchname()) && request.isNerApplied()){
			NerRequest nerReq = new NerRequest();
			nerReq.setQuery(request.getSearchname());
			nerReq.setCity(request.getBycity());
			nerMap = nerServiceImpl.extactNamedEntities(nerReq);
		}
		return nerMap;
	}
	
	/**
	 * Process the doclist returned by the service and get the corresponding JSON
	 * 
	 * @param results
	 * @param domain
	 * @return
	 */
	
	protected String processJSONResponse(List<DOSearchResult> results, String domain,SearchErrors errors){
		
		String jsonresp = null;
		DOSearchResponse resp = new DOSearchResponse();
		Header resheader = new Header(); 
		DOResponseBody body = new DOResponseBody();
		resp.setHeader(resheader);
		if(errors.hasErrors()){
			resheader.setErrors(errors);
			resheader.setStatus(Constants.RESPONSE_STATUS_ERROR);
		}else{
			resheader.setStatus(Constants.RESPONSE_STATUS_OK);
			resp.setBody(body);
			int numFound = 0;
			for(DOSearchResult tcSearchResult:results){
				body.getSearchresult().put(tcSearchResult.getDomain(), tcSearchResult);
				numFound+= tcSearchResult.getMatches();
			}
			body.setNumFound(numFound);
		}
		resheader.setResponseType(domain);
		jsonresp = gsonUtil.getGson().toJson(resp);
		return jsonresp;		
	}
	
	protected DOSearchResponse getDOSearchResponse(List<DOSearchResult> results, String domain,SearchErrors errors,Map<String,String> nerMap){
		DOSearchResponse resp = new DOSearchResponse();
		Header resheader = new Header(); 
		DOResponseBody body = new DOResponseBody();
		resp.setHeader(resheader);
		if(errors.hasErrors()){
			resheader.setErrors(errors);
			resheader.setStatus(Constants.RESPONSE_STATUS_ERROR);
		}else{
			resheader.setStatus(Constants.RESPONSE_STATUS_OK);
			resheader.setNerEntities(nerMap);
			resp.setBody(body);
			int numFound = 0;
			for(DOSearchResult tcSearchResult:results){
				body.getSearchresult().put(tcSearchResult.getDomain(), tcSearchResult);
				numFound+= tcSearchResult.getMatches();
			}
			body.setNumFound(numFound);
		}
		resheader.setResponseType(domain);
		return resp;
	}
	
	protected String getJSON(Object object){
		return gsonUtil.getGson().toJson(object);
	}

	protected void processValidationErrors(List<ObjectError> allErrors,
			SearchErrors errors) {
		for(ObjectError valError:allErrors){
			SearchError error=new SearchError(valError.getCode(), valError.getDefaultMessage());
			errors.add(error);
		}
	}
	
	
}
