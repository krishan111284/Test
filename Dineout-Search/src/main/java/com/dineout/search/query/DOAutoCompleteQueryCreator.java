package com.dineout.search.query;

import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.dineout.search.request.DOAutoSearchRequest;

@Component("autoCompleteQueryCreator")
public class DOAutoCompleteQueryCreator {
	
	ResourceBundle rb = ResourceBundle.getBundle("search");
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	public QueryParam getAutoSuggestQuery(DOAutoSearchRequest req){
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "100%");
		queryParam.addParam("qf", "location_name_string_ngram^2 profile_name_string_ngram^2 cuisine_name_string_ngram^2 title_bm_gram^2");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "3");
		queryParam.addParam("limit", "12");
		queryParam.addParam("fl", "uid,location_name,profile_name,cuisine_name,guid,score");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		queryParam.addParam("q", req.getSearchname());
		//queryParam.addParam("group.sort", "POPULARITY desc,query_pop desc");
		return queryParam;
	}
}
