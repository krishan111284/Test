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
import com.dineout.search.request.AutocompleteSearchRequest;
import com.dineout.search.request.SearchHeader;
import com.dineout.search.response.AutoCompleteSearchResult;
import com.dineout.search.response.Header;
import com.dineout.search.response.AutoSuggestResponseBody;
import com.dineout.search.response.SearchResponse;
import com.dineout.search.service.AutoCompleteService;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.GsonUtil;

@Controller
@RequestMapping(value="/search/autocomplete")
public class AutoSuggestController extends AbstractSearchController{
	@Autowired
	GsonUtil gsonUtil;
	
	@Autowired
	AutoCompleteService autoCompleteService;
	
	@RequestMapping(value="/getresult",method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getKeywordresults(@ModelAttribute("searchHeader")SearchHeader header,
											    @ModelAttribute("autocompleteSearchRequest")AutocompleteSearchRequest tcAutocompleteSearchRequest, 
											    BindingResult bindingResult,
												 HttpServletResponse response){
		Logger logger = Logger.getLogger(AutoSuggestController.class);
		String jsonresp = null;
		if(jsonresp == null){
			SearchErrors errors = new SearchErrors();
			processTCSearchRequest(tcAutocompleteSearchRequest);
			SearchResponse resp = new SearchResponse();
			Header resheader = new Header(); 
			AutoSuggestResponseBody body = new AutoSuggestResponseBody();
			resp.setHeader(resheader);
			resp.setBody(body);
			AutoCompleteSearchResult result = null;
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
