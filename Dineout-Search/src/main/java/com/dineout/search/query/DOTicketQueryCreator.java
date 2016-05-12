package com.dineout.search.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DOTicketSearchRequest;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DODateUtil;
import com.dineout.search.utils.FacetUtils;

@Component("doTicketQueryCreator")
public class DOTicketQueryCreator extends DOAbstractQueryCreator {
	Logger logger = Logger.getLogger(DOTicketQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	@Autowired
	FacetUtils facetUtils;

	public QueryParam getSearchQuery(DOTicketSearchRequest request, Map<String, ArrayList<String>> nerMap) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		Map<String,String> excludeTagMap = new HashMap<String,String>();
		initializeQueryCreator(request, queryParam, request.getEstfl(),rb.getString("dineout.deals.fl"));
		queryString = !StringUtils.isBlank(request.getSearchname()) ? request.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, request,nerMap);
		applyFilters(queryParam, request, excludeTagMap);
		handleFacetingRequest(queryParam, request, excludeTagMap);
		applyGlobalBoosts(queryParam,request);	
		if(request.isSpatialQuery() || request.isEntitySpatialQuery()){
			handleSpatialSortingRequest(queryParam,request);
		}else{
			handleSortingRequest(queryParam,request);
		}
		return queryParam;
	}

	private void applyGlobalBoosts(QueryParam queryParam, DOTicketSearchRequest req) {
		/*queryParam.addParam("boost", "product(scale(booking_last_7,1,5),0.45)");
		queryParam.addParam("boost", "product(scale(booking_last_90,1,5),0.40)");
		queryParam.addParam("boost", "product(sum(avg_rating,1),0.30)");
		queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,recent_days)))),0.1)");*/
	}

	private void applyFilters(QueryParam queryParam,DOTicketSearchRequest req,Map<String, String> excludeTagMap) throws SearchException{
		handleCityFilter(queryParam,req);
		handleCuisineFilters(queryParam,req,excludeTagMap);
		handleLocationFilters(queryParam,req,excludeTagMap);
		handleAreaFilters(queryParam, req, excludeTagMap);
		handlePriceFilters(queryParam,req,excludeTagMap);
		handleTagsFilters(queryParam, req, excludeTagMap);
		handleAreaLocationFilters(queryParam,req,excludeTagMap);
		handleTicketTypeFilters(queryParam,req,excludeTagMap);
		handleRestaurantNameFilters(queryParam,req,excludeTagMap);
		handleCategoryFilters(queryParam,req,excludeTagMap);
		handleTicketDisplayFilters(queryParam,req);
		handleByDateRangeFilter(queryParam,req);
	}

	private void handleByDateRangeFilter(QueryParam queryParam, DOTicketSearchRequest req) {
		String start=req.getFromDate();
		String end=req.getToDate();
		queryParam.addParam("fq", "(from_date_dt:[* TO " + end + "] AND to_date_dt:[" + start + " TO *])");
		queryParam.addParam("bq", "(from_date_dt:["+start +" TO "+end+"] AND to_date_dt:["+start +" TO "+end+"])^5000");
		queryParam.addParam("bq", "(from_date_dt:[* TO " + DODateUtil.getPreviousDate(start) +"] AND to_date_dt:["+ DODateUtil.getNextDate(end) +" TO *])^3000");
		queryParam.addParam("bq","(from_date_dt:[* TO "+start+ "])^800");
		queryParam.addParam("bq","(to_date_dt:["+end+" TO *])^600");
	}

	private void handleTicketDisplayFilters(QueryParam queryParam, DOTicketSearchRequest req) {
		Map<String, Set<?>>datesDays  = DODateUtil.getBetweenDatesDays(DODateUtil.getStringToDate(req.getFromDate()), DODateUtil.getStringToDate(req.getToDate()));
		handleActiveDaysFilter(queryParam,req, datesDays);
		handleBlockedDatesFilters(queryParam,req, datesDays);
	}

	private void handleBlockedDatesFilters(QueryParam queryParam, DOTicketSearchRequest req, Map<String, Set<?>> datesDays) {
		if(datesDays.containsKey("dates")){
			@SuppressWarnings("unchecked")
			Set<Date> blockedDates = (Set<Date>) datesDays.get("dates");
			String blDatesQrStr=null;
			StringBuilder blDatesFacetQr = new StringBuilder();
			for(Date blockedDay:blockedDates){
				String blocDate = DODateUtil.getDateToString(blockedDay);

				blDatesFacetQr.append("blocked_date:\""+blocDate+"\"").append(" AND ");
			}
			blDatesQrStr = blDatesFacetQr.substring(0,blDatesFacetQr.lastIndexOf(" AND "));
			queryParam.addParam("fq", blDatesQrStr.toString());
		}	
	}

	private void handleActiveDaysFilter(QueryParam queryParam, 	DOTicketSearchRequest req, Map<String, Set<?>> datesDays) {
		if(datesDays.containsKey("days")){
			@SuppressWarnings("unchecked")
			Set<Integer> activeDays = (Set<Integer>) datesDays.get("days");
			String activeDaysQrStr=null;
			StringBuilder activeDaysFacetQr = new StringBuilder();
			for(Integer activeDay:activeDays){
				activeDaysFacetQr.append("dow:\""+activeDay+"\"").append(" OR ");
			}
			activeDaysQrStr = activeDaysFacetQr.substring(0,activeDaysFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", activeDaysQrStr.toString());
		}
	}

	private void handleCityFilter(QueryParam queryParam, DOTicketSearchRequest req) {
		if(!StringUtils.isEmpty(req.getBycity())){
			queryParam.addParam("fq", "city_name:\""+req.getBycity()+"\"");
		}
	}

	private void handleCategoryFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getByCategory()!=null && request.getByCategory().length>0){
			StringBuilder categoryFacetQr = new StringBuilder();
			String categoryFacetQrStr=null;
			for(String category:request.getByCategory()){
				categoryFacetQr.append("category_ft:\""+category+"\"").append(" OR ");
			}
			categoryFacetQrStr = categoryFacetQr.substring(0,categoryFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=category_ft_tag}("+categoryFacetQrStr.toString()+")");
			excludeTagMap.put("category", "{!ex=category_ft_tag}");
		}
	}

	private void handleRestaurantNameFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getByRestaurant()!=null && request.getByRestaurant().length>0){
			StringBuilder restaurantFacetQr = new StringBuilder();
			String restaurantFacetQrStr=null;
			for(String restaurant:request.getByRestaurant()){
				restaurantFacetQr.append("rest_name_ft:\""+restaurant+"\"").append(" OR ");
			}
			restaurantFacetQrStr = restaurantFacetQr.substring(0,restaurantFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=rest_name_ft_tag}("+restaurantFacetQrStr.toString()+")");
			excludeTagMap.put("rest_name", "{!ex=rest_name_ft_tag}");
		}
	}

	private void handleTicketTypeFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getByTicketType()!=null && request.getByTicketType().length>0){
			StringBuilder ticketFacetQr = new StringBuilder();
			String ticketFacetQrStr=null;
			for(String ticket:request.getByTicketType()){
				ticketFacetQr.append("datatype_ft:\""+ticket+"\"").append(" OR ");
			}
			ticketFacetQrStr = ticketFacetQr.substring(0,ticketFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=datatype_ft_tag}("+ticketFacetQrStr.toString()+")");
			excludeTagMap.put("datatype", "{!ex=datatype_ft_tag}");
		}
	}

	private void handleAreaLocationFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getBylocarea()!=null && request.getBylocarea().length>0){
			StringBuilder locationAreaFacetQr = new StringBuilder();
			String locationAreaFacetQrStr=null;
			for(String locationArea:request.getBylocarea()){
				locationAreaFacetQr.append("locality_area_ft:\""+locationArea+"\"").append(" OR ");
			}
			locationAreaFacetQrStr = locationAreaFacetQr.substring(0,locationAreaFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=locality_area_name_ft_tag}("+locationAreaFacetQrStr.toString()+")");
			excludeTagMap.put("locationArea", "{!ex=locality_area_name_ft_tag}");
		}
	}

	private void handleFacetingRequest(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(!Constants.IS_FACET_DISABLED.equals(restSearchReq.getDisableestfacet())){
			Set<String> facetSet = null;
			String facetLimit = restSearchReq.getFacetlimit()!=null ? restSearchReq.getFacetlimit():Constants.DEFAULT_FACET_LIMIT;
			String facetMinCount = restSearchReq.getFacetmincount()!=null ? restSearchReq.getFacetmincount(): Constants.DEFAULT_FACET_MIN_COUNT;
			String sortFlag = "count";
			if(Constants.TC_FACET_SORT_INDEX_TRUE.equals(restSearchReq.getFacetsorttype())){
				sortFlag = "index";
			}
			if(StringUtils.isBlank(restSearchReq.getEstfacetfl())){
				facetSet = facetUtils.getDefaultDealsFacets();
			}else{
				facetSet = new HashSet<String>();
				String[] facets = restSearchReq.getEstfacetfl().split(Constants.SEPERATOR);
				for(String facet:facets){
					facetSet.add(facet);
				}
			}

			queryParam.addParam("facet", "true");
			if(facetSet.contains("locality_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("location")!=null?excludeTagMap.get("location"):"")+"locality_name_ft");}
			if(facetSet.contains("cuisine_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("cuisine")!=null?excludeTagMap.get("cuisine"):"")+"cuisine_ft");}
			if(facetSet.contains("area_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("area")!=null?excludeTagMap.get("area"):"")+"area_name_ft");}
			if(facetSet.contains("tags_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("tags")!=null?excludeTagMap.get("tags"):"")+"tags_ft");}
			if(facetSet.contains("category_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("category")!=null?excludeTagMap.get("category"):"")+"category_ft");}
			if(facetSet.contains("rest_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("rest_name")!=null?excludeTagMap.get("rest_name"):"")+"rest_name_ft");}
			if(facetSet.contains("locality_area_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("locationArea")!=null?excludeTagMap.get("locationArea"):"")+"locality_area_ft");}
			if(facetSet.contains("datatype_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("datatype")!=null?excludeTagMap.get("datatype"):"")+"datatype_ft");}

			if(facetSet.contains("price")){
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "price:[0 TO 500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "price:[501 TO 1000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "price:[1001 TO 1500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "price:[1501 TO 2000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "price:[2001 TO *]");
			}
			queryParam.addParam("facet.limit",facetLimit);
			queryParam.addParam("facet.mincount", ""+facetMinCount);
			queryParam.addParam("facet.sort", sortFlag);
		}
	}

	private void handleSpatialSortingRequest(QueryParam queryParam, DOTicketSearchRequest request) {
		String spatialQuery = "";
		String geoDistance = "";

		if(request.isSpatialQuery() && request.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + request.getElat() + "," + request.getElng() + " d=" + request.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + request.getLat() +","+request.getLng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + request.getLat() +","+request.getLng()+"))))),0.1)");
		}
		else if(request.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + request.getElat() + "," + request.getElng() + " d=" + request.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + request.getElat() +","+request.getElng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + request.getElat() +","+request.getElng()+"))))),0.1)");
		}
		else if(request.isSpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + request.getLat() + "," + request.getLng() + " d=" + request.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + request.getLat() +","+request.getLng()+")";
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + request.getLat() +","+request.getLng()+"))))),0.1)");
		}

		String sortfieldApplied = "";
		String bySort = request.getBysort();
		queryParam.addParam("fq", spatialQuery);
		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,price asc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,price desc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,display_price asc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_FOUR.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,display_price desc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_FIVE.equals(bySort)){
				sortfieldApplied = "price desc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_SIX.equals(bySort)){
				sortfieldApplied = "price asc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_SEVEN.equals(bySort)){
				sortfieldApplied = "display_price desc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_EIGHT.equals(bySort)){
				sortfieldApplied = "display_price asc,"+geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_NINE.equals(bySort)){
				sortfieldApplied = "booking_count desc,"+geoDistance+ " asc";
			}
		}
		else{
			sortfieldApplied = geoDistance+ " asc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handleSortingRequest(QueryParam queryParam, DOTicketSearchRequest restSearchReq) {
		String bySort = restSearchReq.getBysort();
		String sortfieldApplied = "";
		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,price asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,price desc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,display_price asc";
			}
			if(Constants.SORT_OPTION_FOUR.equals(bySort)){
				sortfieldApplied = "fullfillment desc,is_pf desc,display_price desc";
			}
			if(Constants.SORT_OPTION_FIVE.equals(bySort)){
				sortfieldApplied = "price desc";
			}
			if(Constants.SORT_OPTION_SIX.equals(bySort)){
				sortfieldApplied = "price asc";
			}
			if(Constants.SORT_OPTION_SEVEN.equals(bySort)){
				sortfieldApplied = "display_price desc";
			}
			if(Constants.SORT_OPTION_EIGHT.equals(bySort)){
				sortfieldApplied = "display_price asc";
			}
			if(Constants.SORT_OPTION_NINE.equals(bySort)){
				sortfieldApplied = "booking_count desc";
			}
		}else{
			//NEAREST TIME FIRST
			sortfieldApplied = "score desc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handlePriceFilters(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) throws SearchException {
		if(restSearchReq.getByprice()!=null && restSearchReq.getByprice().length>0){
			StringBuilder priceFacetQr = new StringBuilder();
			String priceFacetQrStr = null;
			for(String price:restSearchReq.getByprice()){
				try {
					price = URLDecoder.decode(price, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(),e);
					throw new SearchException(e.getMessage(),e.getCause(),ErrorCode.URL_DECODE_ERROR);
				}
				String[] priceRange = price.split("-");
				priceFacetQr.append("price:["+priceRange[0]+" TO "+priceRange[1]+"]").append(" OR ");
			}
			priceFacetQrStr = priceFacetQr.substring(0,priceFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=price}("+priceFacetQrStr+")");
			excludeTagMap.put("price", "{!ex=price}");
		}
	}

	private void handleTagsFilters(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBytags()!=null && restSearchReq.getBytags().length>0){
			StringBuilder facilityQr = new StringBuilder();
			String facilityQrStr = null;
			for(String facility:restSearchReq.getBytags()){
				facilityQr.append("tags_ft:\""+facility+"\"").append(" AND ");
			}
			facilityQrStr = facilityQr.substring(0,facilityQr.lastIndexOf(" AND "));

			queryParam.addParam("fq", "{!tag=tags_ft_tag}("+facilityQrStr+")");
			excludeTagMap.put("tags", "{!ex=tags_ft_tag}");
		}
	}

	private void handleAreaFilters(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getByarea()!=null && restSearchReq.getByarea().length>0){
			StringBuilder zoneFacetQr = new StringBuilder();
			String zoneFacetQrStr = null;
			for(String zone:restSearchReq.getByarea()){
				zoneFacetQr.append("area_name_ft:\""+zone+"\"").append(" OR ");
			}
			zoneFacetQrStr = zoneFacetQr.substring(0,zoneFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=area_name_ft_tag}("+zoneFacetQrStr+")");
			excludeTagMap.put("area", "{!ex=area_name_ft_tag}");
		}
	}

	private void handleLocationFilters(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBylocation()!=null && restSearchReq.getBylocation().length>0){
			StringBuilder locationFacetQr = new StringBuilder();
			String locationFacetQrStr=null;
			for(String location:restSearchReq.getBylocation()){
				locationFacetQr.append("locality_name_ft:\""+location+"\"").append(" OR ");
			}
			locationFacetQrStr = locationFacetQr.substring(0,locationFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=locality_name_ft_tag}("+locationFacetQrStr.toString()+")");
			excludeTagMap.put("location", "{!ex=locality_name_ft_tag}");
		}
	}

	private void handleCuisineFilters(QueryParam queryParam, DOTicketSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBycuisine()!=null && restSearchReq.getBycuisine().length>0){
			StringBuilder cuisineFacetQr = new StringBuilder();
			StringBuilder primaryCuisineFacetQr = new StringBuilder();
			StringBuilder secondaryCuisineFacetQr = new StringBuilder();
			String cuisineFacetQrStr = null;
			String primaryCuisineFacetQrStr = null;
			for(String cuisine:restSearchReq.getBycuisine()){
				cuisine.replaceAll("~","/");
				cuisineFacetQr.append("cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
				primaryCuisineFacetQr.append("primary_cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
				secondaryCuisineFacetQr.append("secondary_cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
			}
			cuisineFacetQrStr = cuisineFacetQr.substring(0,cuisineFacetQr.lastIndexOf(" OR "));
			primaryCuisineFacetQrStr = primaryCuisineFacetQr.substring(0,primaryCuisineFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("bq", "("+primaryCuisineFacetQrStr+")^40000");
			queryParam.addParam("fq", "{!tag=cuisine_ft_tag}("+cuisineFacetQrStr.toString()+")");
			excludeTagMap.put("cuisine", "{!ex=cuisine_ft_tag}");
		}
	}

	private void setQueryParser(QueryParam queryParam,DOTicketSearchRequest req, Map<String, ArrayList<String>> nerMap){
		queryParam.addParam("defType","edismax");
		if(req.isSearchExecuted()){
			String query = nerMap.size()>0?nerMap.get(Constants.PROCESSED_QUERY).get(0).trim():req.getSearchname();
			String[] tokens = query.split(" ");
			queryParam.addParam("mm", tokens.length+"");
		}
		if(!StringUtils.isEmpty(req.getSearchname())){
			setQfParams(queryParam);
			setPfParams(queryParam);
		}
		setResponseNumLimit(queryParam,req);
	}

	private void setQfParams(QueryParam queryParam) {
		queryParam.addParam("qf", rb.getString("dineout.deals.qf.param"));
	}

	private void setPfParams(QueryParam queryParam) {
		queryParam.addParam("pf",rb.getString("dineout.deals.pf.param"));
		queryParam.addParam("pf2",rb.getString("dineout.deals.pf2.param"));
	}

}
