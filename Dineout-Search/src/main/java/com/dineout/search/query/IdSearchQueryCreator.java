package com.dineout.search.query;

import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.utils.Constants;

@Component("idSearchQueryCreator")
public class IdSearchQueryCreator {
	Logger logger = Logger.getLogger(IdSearchQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getSearchQuery(DORestSearchRequest req,
			Map<String, ArrayList<String>> nerMap) throws SearchException {

		QueryParam queryParam = new QueryParam();
		String[] ids = req.getRestIds();
		if(ids!=null && ids.length>0){
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for(String id:ids){
				sb.append(id +" OR ");
			}
			String idFilter = sb.substring(0, sb.lastIndexOf(" OR ")) + ")";
			queryParam.addParam("fq", "r_id:"+idFilter);
			queryParam.addParam("defType","edismax");
			queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
			addFlParams(req, queryParam);	
			addLimit(req, queryParam);
		}
		return queryParam;
	}

	private void addLimit(DORestSearchRequest req, QueryParam queryParam) {
		String start = !StringUtils.isEmpty(req.getStart())? req.getStart():Constants.DEFAULT_START_INDEX;
		String rows = !StringUtils.isEmpty(req.getLimit())? req.getLimit():Constants.MAX_NUM_ROWS;
		queryParam.addParam("start", start);
		queryParam.addParam("rows", rows);
	}

	protected void addFlParams(DORestSearchRequest request, QueryParam queryParam) {
		StringBuilder sb = new StringBuilder(rb.getString("dineout.search.fl"));
		if(request.getLat()!=null && request.getLng()!=null){
			String geoDistance = "geodist(lat_lng," + request.getLat() +","+request.getLng()+")";
			sb.append(",").append("geo_distance:"+geoDistance);
		}
		queryParam.addParam("fl",sb.toString());
	}

}
