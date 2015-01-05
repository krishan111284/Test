package com.dineout.search.controller;

import javax.servlet.http.HttpServletResponse;

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
import com.dineout.search.request.DOAutoSearchRequest;
import com.dineout.search.request.DOSearchHeader;
import com.dineout.search.response.DOAutoCompleteSearchResult;
import com.dineout.search.response.Header;
import com.dineout.search.response.DOAutoSuggestResponseBody;
import com.dineout.search.response.DOSearchResponse;
import com.dineout.search.service.DOAutoCompleteService;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.GsonUtil;

@Controller
@RequestMapping(value="/auto")
public class DOAutoSuggestController extends DOAbstractSearchController{
	@Autowired
	GsonUtil gsonUtil;
	
	@Autowired
	DOAutoCompleteService autoCompleteService;
	
	@RequestMapping(value="/getresult",method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getKeywordresults(@ModelAttribute("searchHeader")DOSearchHeader header,
											    @ModelAttribute("autocompleteSearchRequest")DOAutoSearchRequest tcAutocompleteSearchRequest, 
											    BindingResult bindingResult,
												 HttpServletResponse response){
		Logger logger = Logger.getLogger(DOAutoSuggestController.class);
		String jsonresp = null;
		if(jsonresp == null){
			SearchErrors errors = new SearchErrors();
			processDOSearchRequest(tcAutocompleteSearchRequest);
			DOSearchResponse resp = new DOSearchResponse();
			Header resheader = new Header(); 
			DOAutoSuggestResponseBody body = new DOAutoSuggestResponseBody();
			resp.setHeader(resheader);
			resp.setBody(body);
			DOAutoCompleteSearchResult result = null;
			result = autoCompleteService.getSuggestion(tcAutocompleteSearchRequest,errors);
			if(errors.hasErrors()){
				resheader.setErrors(errors);
				resheader.setStatus(Constants.RESPONSE_STATUS_ERROR);
				logger.error("Errors Encountered While Processing AutoCompletion Response");
			}else{
				body.setResult(result);
				resheader.setStatus(Constants.RESPONSE_STATUS_OK);
			}
			jsonresp=gsonUtil.getGson().toJson(resp);
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(Constants.JSON_MEDIA_TYPE);
		return new ResponseEntity<String>(jsonresp, responseHeaders, HttpStatus.CREATED);
		
	}
}
