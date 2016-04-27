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
			if(req.isSpatialQuery() || req.isEntitySpatialQuery()){
				handleSpatialSortingRequest(queryParam,req);
			}else{
				handleSortingRequest(queryParam,req);
			}
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

	private void handleSpatialSortingRequest(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		String spatialQuery = "";
		String geoDistance = "";

		if(restSearchReq.isSpatialQuery() && restSearchReq.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getElat() + "," + restSearchReq.getElng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+"))))),0.1)");
		}
		else if(restSearchReq.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getElat() + "," + restSearchReq.getElng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getElat() +","+restSearchReq.getElng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + restSearchReq.getElat() +","+restSearchReq.getElng()+"))))),0.1)");
		}
		else if(restSearchReq.isSpatialQuery()){
			if(restSearchReq.getSearchType()==null || !restSearchReq.getSearchType().equalsIgnoreCase(rb.getString("dineout.search.type.explicit")))
				spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getLat() + "," + restSearchReq.getLng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+"))))),0.1)");
		}

		String sortfieldApplied = "";
		String bySort = restSearchReq.getBysort();

		queryParam.addParam("fq", spatialQuery);

		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count asc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count desc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,costFor2 asc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_FOUR.equals(bySort)){
				sortfieldApplied = "fullfillment desc,costFor2 desc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_FIVE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating asc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_SIX.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating desc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_SEVEN.equals(bySort)){
				sortfieldApplied = "fullfillment desc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_EIGHT.equals(bySort)){
				sortfieldApplied = geoDistance + " asc";
			}
		}else{
			sortfieldApplied = "fullfillment desc,"  + geoDistance + " asc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handleSortingRequest(QueryParam queryParam,
			DORestSearchRequest restSearchReq) {
		String bySort = restSearchReq.getBysort();
		String sortfieldApplied = "";
		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count desc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,costFor2 asc";
			}
			if(Constants.SORT_OPTION_FOUR.equals(bySort)){
				sortfieldApplied = "fullfillment desc,costFor2 desc";
			}
			if(Constants.SORT_OPTION_FIVE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating asc";
			}
			if(Constants.SORT_OPTION_SIX.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating desc";
			}
		}else{
			sortfieldApplied = "fullfillment desc, score desc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

}
