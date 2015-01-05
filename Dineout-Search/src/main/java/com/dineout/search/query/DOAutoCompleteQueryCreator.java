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
			queryParam.addParam("qf", "location_name_string_ngram^2 loc_alias_string_ngram^2 est_name_string_ngram^2 event_name_string_ngram^2 theater_name_string_ngram^2 movie_name_string_ngram^2  cuisine_name_string_ngram^2 query_name_string_ngram^2");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "3");
		queryParam.addParam("limit", "12");
		queryParam.addParam("fl", "tc_id,loc_name,location_name,est_name,event_name,theater_name,movie_name,movie_name,cuisine_name,guid,address,score,query_name,filter_1,filter_2,entity_type");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		queryParam.addParam("q", req.getSearchname());
		queryParam.addParam("group.sort", "POPULARITY desc,query_pop desc");
		return queryParam;
	}
}
