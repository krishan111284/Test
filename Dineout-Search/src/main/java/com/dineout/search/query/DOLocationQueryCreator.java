package com.dineout.search.query;


import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchErrors;
import com.dineout.search.request.DOLocationSearchRequest;
import com.dineout.search.utils.Constants;

@Component("locationQueryCreator")
public class DOLocationQueryCreator extends DOAbstractQueryCreator {
	Logger logger = Logger.getLogger(DOLocationQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getAreaCitySearchQuery(DOLocationSearchRequest req, SearchErrors errors) {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("q", !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "100%");
		queryParam.addParam("fl", rb.getString("dineout.location.cityarea.search.fl"));
		queryParam.addParam("qf", rb.getString("dineout.location.cityarea.search.qf.param"));
		handleGroupRequest(queryParam,req);
		handleCityAreaGroupSort(queryParam,req);
		handleCityAreaFilters(queryParam,req);
		return queryParam;
	}

	public QueryParam getLocationSearchQuery(DOLocationSearchRequest req, SearchErrors errors) {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("q", !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "50%");
		queryParam.addParam("fl", rb.getString("dineout.location.search.fl"));
		queryParam.addParam("qf", rb.getString("dineout.location.search.qf.param"));
		handleGroupRequest(queryParam,req);
		handleFilters(queryParam,req);
		handleExecutionType(queryParam,req);

		return queryParam;
	}

	public QueryParam getGPSLocationSearchQuery(DOLocationSearchRequest req, SearchErrors errors) {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("q", !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "100%");
		queryParam.addParam("fl", rb.getString("dineout.gps.location.search.fl"));
		queryParam.addParam("qf", rb.getString("dineout.gps.location.search.qf.param"));
		handleGroupRequest(queryParam,req);
		handleFilters(queryParam,req);
		handleExecutionType(queryParam,req);

		return queryParam;
	}

	private void handleExecutionType(QueryParam queryParam, DOLocationSearchRequest req) {
		if(req.isDistanceSearchQuery()){
			handleSpatialSortingRequest(queryParam, req);
			queryParam.addParam("group.limit", "25");	
		}

		if(req.isGPSQuery())
		{
			handleLimit(queryParam,req);
			String radius = req.getRadius()!=null?req.getRadius():"100";
			String spatialQuery = "{!geofilt sfield=lat_lng pt=" + req.getLat() + "," + req.getLng() + " d=" + radius + "}";
			queryParam.addParam("fq", spatialQuery);
			handleSpatialSortingRequest(queryParam, req);
			queryParam.addParam("group.limit", "1");	
		}
		if(req.isSearchQuery()){
			queryParam.addParam("group.limit", "25");
			queryParam.addParam("sort", "city_name asc");
			queryParam.addParam("group.sort", "score desc");

		}

	}

	private void handleFilters(QueryParam queryParam, DOLocationSearchRequest req) {
		queryParam.addParam("fq", "data_type:Locality OR data_type:Area");
	}

	private void handleCityAreaFilters(QueryParam queryParam, DOLocationSearchRequest req) {
		queryParam.addParam("fq", "data_type:Area OR data_type:City OR data_type:Locality");
	}

	private void handleLimit(QueryParam queryParam, DOLocationSearchRequest req) {
		String rows = !StringUtils.isEmpty(req.getLimit())? req.getLimit():"1";
		queryParam.addParam("rows", rows);	
	}

	private void handleGroupRequest(QueryParam queryParam, DOLocationSearchRequest req) {
		queryParam.addParam("group","true");
		queryParam.addParam("group.field","city_name"); 
	}

	private void handleSpatialSortingRequest(QueryParam queryParam, DOLocationSearchRequest req) {
		String geoDistance = "geodist(lat_lng," + req.getLat() +","+req.getLng()+")";
		queryParam.addParam("group.sort", geoDistance + " asc");
		queryParam.addParam("sort", geoDistance + " asc");	
	}

	private void handleCityAreaGroupSort(QueryParam queryParam, DOLocationSearchRequest req) {
		if(req.isDistanceSearchQuery()){
			queryParam.addParam("group.limit", "10");
			String geoDistance = "geodist(lat_lng," + req.getLat() +","+req.getLng()+")";
			queryParam.addParam("sort", geoDistance + " asc");	
			queryParam.addParam("group.sort", "score desc," +geoDistance + " asc");
			
		}

	}
}
