package com.dineout.search.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.RestSearchRequest;
import com.dineout.search.request.SearchHeader;
import com.dineout.search.response.DOResponseBody;
import com.dineout.search.response.SearchResponse;
import com.dineout.search.response.SearchResult;
import com.dineout.search.service.RestSearchService;
import com.dineout.search.utils.Constants;
import com.dineout.search.validation.TCEstRequestValidator;

@Controller
@RequestMapping(value="/unifiedsearch/est")
public class TCEstSearchController extends AbstractSearchController{

	Logger dbLogger = Logger.getLogger("NULL_QUERY_LOGGER");
	Logger logger = Logger.getLogger(TCEstSearchController.class);
	@Autowired
	RestSearchService restSearchService;
	@Autowired
	TCEstRequestValidator tcEstRequestValidator;
	@RequestMapping(value="/getresult",method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getKeywordresults(@ModelAttribute("searchHeader")SearchHeader header,
			@ModelAttribute("restSearchRequest")RestSearchRequest request, BindingResult bindingResult,
			HttpServletResponse response,
			HttpSession session,HttpServletRequest httpReq){
		String jsonresp = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		List<SearchResult> tcSearchResultList = null;
		responseHeaders.setContentType(Constants.JSON_MEDIA_TYPE);
		SearchErrors errors = new SearchErrors();
		tcEstRequestValidator.validatorResourceData(request,bindingResult,new String[]{"bycity"});
		if(bindingResult.hasErrors()){
			processValidationErrors(bindingResult.getAllErrors(),errors);
			jsonresp = processJSONResponse(null, null, errors);
		}else{
			processTCSearchRequest(request);
			Map<String, String> nerMap = getNerMap(request);
			tcSearchResultList = restSearchService.getSearchResults(request,errors,nerMap);
			SearchResponse resp = getTCSearchResponse(tcSearchResultList, null,errors,nerMap);
			if(!errors.hasErrors() && ((DOResponseBody)resp.getBody()).getNumFound() == 0){
				dbLogger.error(httpReq.getQueryString());
				logger.error(request.getSearchname());
			}
			jsonresp = getJSON(resp);
		}
		return new ResponseEntity<String>(jsonresp, responseHeaders, HttpStatus.CREATED);

	}
}
