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
		if(req.getSearchname().split(" ").length>1)
			queryParam.addParam("qf", "area_name^50 area_name_string_ngram^100 location_name^50 location_name_string_ngram^100 profile_name^50 profile_name_string_ngram^100 cuisine_name^50 cuisine_name_string_ngram^100 tag_name^50 tag_name_string_ngram^100 title_specialchars^10 title_bm_gram");
		else
			queryParam.addParam("qf", "area_name^50 area_name_string_ngram^100 location_name^50 location_name_string_ngram^100 profile_name^50 profile_name_string_ngram^100 cuisine_name^50 cuisine_name_string_ngram^100 tag_name^50 tag_name_string_ngram^100 title_specialchars^10");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "10");
		queryParam.addParam("limit", "40");
		queryParam.addParam("fl", "r_id,uid,area_name,location_name,loc_area_name,profile_name,profile_location_name,cuisine_name,guid,score,tag_name,fullfillment,booking_count");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		queryParam.addParam("q", req.getSearchname());
		queryParam.addParam("group.sort", "fullfillment desc,booking_count desc,score desc");
		return queryParam;
	}
}
