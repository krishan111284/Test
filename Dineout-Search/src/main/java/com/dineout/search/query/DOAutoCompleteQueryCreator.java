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
		queryParam.addParam("qf", "area_name^2 area_name_string_ngram^2 location_name^2 location_name_string_ngram^2 profile_name^2 profile_name_string_ngram^2 cuisine_name^2 cuisine_name_string_ngram^2 tag_name^2 tag_name_string_ngram^2 title_bm_gram^2");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "5");
		queryParam.addParam("limit", "12");
		queryParam.addParam("fl", "uid,area_name,location_name,profile_name,cuisine_name,guid,score,tag_name");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		queryParam.addParam("q", req.getSearchname());
		//queryParam.addParam("group.sort", "POPULARITY desc,query_pop desc");
		return queryParam;
	}
}
