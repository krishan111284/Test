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
import com.dineout.search.request.DOTicketSearchRequest;
import com.dineout.search.response.DOResponseBody;
import com.dineout.search.response.DOSearchResponse;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.service.TicketServiceImpl;
import com.dineout.search.utils.Constants;
import com.dineout.search.validation.DoRestRequestValidator;

@Controller
@RequestMapping(value="/ticket")
public class DOTicketSearchController extends DOAbstractSearchController{

	Logger logger = Logger.getLogger(DOTicketSearchController.class);
	@Autowired
	TicketServiceImpl ticketServiceImpl;
	@Autowired
	DoRestRequestValidator doRestRequestValidator;
	@RequestMapping(value="/getresult",method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getKeywordresults(@ModelAttribute("ticketRequest")DOTicketSearchRequest request, 
			BindingResult bindingResult, HttpServletResponse response, HttpSession session, HttpServletRequest httpReq){
		long start = new Date().getTime();
		String jsonresp = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		List<DOSearchResult> searchResultList = null;
		responseHeaders.setContentType(Constants.JSON_MEDIA_TYPE);
		SearchErrors errors = new SearchErrors();
		doRestRequestValidator.validatorResourceData(request, bindingResult, new String[]{"bycity"});
		if(bindingResult.hasErrors()){
			processValidationErrors(bindingResult.getAllErrors(),errors);
			jsonresp = processJSONResponse(null, null, errors);
		}else{
			processDOSearchRequest(request);
			searchResultList = ticketServiceImpl.getSearchResults(request,errors,null);
			long responseTime = new Date().getTime() - start;
			DOSearchResponse resp = getDOSearchResponse(searchResultList, null, errors, null, responseTime);
			if(!errors.hasErrors() && ((DOResponseBody)resp.getBody()).getNumFound() == 0){
				logger.info("Null Ticket Query: "+httpReq.getRequestURL()+"?" + httpReq.getQueryString());
			}
			jsonresp = getJSON(resp);
		}
		return new ResponseEntity<String>(jsonresp, responseHeaders, HttpStatus.CREATED);
	}
}
