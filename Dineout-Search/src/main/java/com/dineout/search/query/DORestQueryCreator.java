package com.dineout.search.query;

import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.dineout.search.request.DORestSearchRequest;

@Component("restQueryCreator")
public class DORestQueryCreator {
	
	ResourceBundle rb = ResourceBundle.getBundle("search");
	
	/**
	 * 
	 * @param req
	 * @return
	 */


	public QueryParam getSearchQuery(DORestSearchRequest request,
			Map<String, String> nerMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
