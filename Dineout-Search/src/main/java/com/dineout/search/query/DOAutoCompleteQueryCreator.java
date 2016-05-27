package com.dineout.search.query;

import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.dineout.search.request.DOAutoSearchRequest;
import com.dineout.search.utils.Constants;

@Component("autoCompleteQueryCreator")
public class DOAutoCompleteQueryCreator {

	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getAutoSuggestQuery(DOAutoSearchRequest req){
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "100%");
		if(req.getSearchname().split(" ").length>1)
			queryParam.addParam("qf", "area_name^50 area_name_string_ngram^100 area_alias^50 location_name^50 location_name_string_ngram^100 location_alias^50 profile_name^50 profile_name_string_ngram^100 cuisine_name^50 cuisine_name_string_ngram^100 tag_name^50 tag_name_string_ngram^100 ticket_name^50 ticket_name_string_ngram^100 dc_name^50 dc_name_string_ngram^100 title_specialchars^10 title_bm_gram");
		else
			queryParam.addParam("qf", "area_name^50 area_name_string_ngram^100 area_alias^50 location_name^50 location_name_string_ngram^100 location_alias^50 profile_name^50 profile_name_string_ngram^100 cuisine_name^50 cuisine_name_string_ngram^100 tag_name^50 tag_name_string_ngram^100 ticket_name^50 ticket_name_string_ngram^100  dc_name^50 dc_name_string_ngram^100 title_specialchars^10");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "5");
		queryParam.addParam("fl", "r_id,uid,area_name,location_name,loc_area_name,profile_name,profile_location_name,cuisine_name,guid,score,tag_name,fullfillment,popularity_count,lat_lng,suggestion,dc_name,tg_id,tl_id,ticket_name,from_date,to_date");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		if(req.getGroup()!=null && req.getGroup().equalsIgnoreCase(Constants.GROUP_TRUE))
			queryParam.addParam("fq", "-data_type:Restaurant");
		queryParam.addParam("fq", "-data_type:City");
		queryParam.addParam("q", req.getSearchname());
		queryParam.addParam("group.sort", "fullfillment desc,popularity_count desc,score desc");
		return queryParam;
	}

	public QueryParam getAutoSuggestRestGroupQuery(DOAutoSearchRequest req){
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "100%");
		if(req.getSearchname().split(" ").length>1)
			queryParam.addParam("qf", "profile_name^50 profile_name_string_ngram^100 title_bm_gram");
		else
			queryParam.addParam("qf", "profile_name^50 profile_name_string_ngram^100 title_specialchars^10");
		queryParam.addParam("group", "true");
		queryParam.addParam("group.ngroups", "true");
		queryParam.addParam("group.field", "chain_name");
		queryParam.addParam("group.limit", "10");
		queryParam.addParam("fl", "r_id,uid,profile_name,chain_name,chain_id,profile_location_name,guid,score,fullfillment,popularity_count,lat_lng,suggestion");
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+req.getBycity()+")");
		queryParam.addParam("fq", "data_type:Restaurant");
		queryParam.addParam("fq", "-data_type:City");
		queryParam.addParam("q", req.getSearchname());
		queryParam.addParam("sort", "fullfillment desc,popularity_count desc,score desc");		
		return queryParam;
	}
}
