package com.dineout.search.query;

import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.dineout.search.request.RestSearchRequest;

@Component("restQueryCreator")
public class RestQueryCreator {
	
	ResourceBundle rb = ResourceBundle.getBundle("search");
	
	/**
	 * 
	 * @param req
	 * @return
	 */


	public QueryParam getSearchQuery(RestSearchRequest request,
			Map<String, String> nerMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
