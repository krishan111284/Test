package com.dineout.search.controller;

import java.util.Date;
import java.util.List;

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
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.request.DOSearchHeader;
import com.dineout.search.response.DORecoResponseBody;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.response.DOSearchResponse;
import com.dineout.search.service.RecoSearchService;
import com.dineout.search.utils.Constants;
import com.dineout.search.validation.DORequestValidator;

@Controller
@RequestMapping(value="/similar/")
public class SimilarRestController extends DOAbstractSearchController{

	Logger logger = Logger.getLogger(SimilarRestController.class);
	@Autowired
	RecoSearchService similarRecoSearchService;
	@Autowired
	DORequestValidator doRequestValidator;

	@RequestMapping(value="/getresult",method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getKeywordresults(@ModelAttribute("searchHeader")DOSearchHeader header,
			@ModelAttribute("restSearchRequest")DORestSearchRequest request, BindingResult bindingResult,
			HttpServletResponse response, HttpSession session,HttpServletRequest httpReq){
		String jsonresp = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		List<DORecoResult> searchResultList = null;
		responseHeaders.setContentType(Constants.JSON_MEDIA_TYPE);
		SearchErrors errors = new SearchErrors();
		doRequestValidator.validatorResourceData(request,bindingResult,new String[]{"restId"});
		if(bindingResult.hasErrors()){
			processValidationErrors(bindingResult.getAllErrors(),errors);
			jsonresp = processJSONResponse(null, null, errors);
		}else{
			long start = new Date().getTime();
			searchResultList = similarRecoSearchService.getSearchResults(request,errors);
			long responseTime = new Date().getTime() - start;
			DOSearchResponse resp = getRecoResponse(searchResultList,errors,"Similar",responseTime);
			if(!errors.hasErrors() && ((DORecoResponseBody)resp.getBody()).getMatches() == 0){
				logger.error(request.getSearchname());
			}
			jsonresp = getJSON(resp);
		}
		return new ResponseEntity<String>(jsonresp, responseHeaders, HttpStatus.CREATED);

	}
}
