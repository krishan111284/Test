package com.dineout.search.query;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.request.GenericDOSearchRequest;
import com.dineout.search.request.RecommendationRequest;
import com.dineout.search.utils.Constants;

@Component("recommendationQueryCreator")
public class RecommendationQueryCreator {

	Logger logger = Logger.getLogger(RecommendationQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getRestIdSearchQuery(RecommendationRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("fq", "r_id:"+req.getRestId());
		queryParam.addParam("defType","edismax");
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("fl", "feature1,feature2,feature3,feature4,feature5,feature6,feature7,feature8,feature9");
		return queryParam;
	}
	
	public QueryParam getDinerIdSearchQuery(RecommendationRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("fq", "diner_id:"+req.getDinerId());
		queryParam.addParam("defType","edismax");
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("fl", "feature1,feature2,feature3,feature4,feature5,feature6,feature7,feature8,feature9");
		return queryParam;
	}

	public QueryParam getDinerRestaurantsQuery(DORestSearchRequest req, ArrayList<String> restIds) throws SearchException {
		QueryParam queryParam = new QueryParam();
		if(restIds!=null && restIds.size()>0){
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for(String id:restIds){
				sb.append(id +" OR ");
			}
			String idFilter = sb.substring(0, sb.lastIndexOf(" OR ")) + ")";
			queryParam.addParam("fq", "r_id:"+idFilter);
			applyCityRestFilter(queryParam, req);
			queryParam.addParam("defType","edismax");
			queryParam.addParam("fq", "fullfillment:true");
			queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
			queryParam.addParam("fl", rb.getString("dineout.search.fl"));			
		}
		return queryParam;
	}

	private void applyCityRestFilter(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("fq", "city_name:"+req.getBycity());
	}

	public void setResponseNumLimit(QueryParam queryParam, GenericDOSearchRequest req) {
		String start = !StringUtils.isEmpty(req.getStart())? req.getStart():Constants.DEFAULT_START_INDEX;
		String rows = !StringUtils.isEmpty(req.getLimit())? req.getLimit():Constants.DEFAULT_NUM_ROWS;
		queryParam.addParam("start", start);
		queryParam.addParam("rows", rows);
	}

}
