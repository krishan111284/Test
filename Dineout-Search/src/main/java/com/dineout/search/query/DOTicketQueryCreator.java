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
		handleGroup(queryParam,request);
		return queryParam;
	}

	private void handleGroup(QueryParam queryParam, DOTicketSearchRequest request) {
		if(!Constants.GROUP_FALSE.equals(request.getGroup())){
			queryParam.addParam("group","true");
			queryParam.addParam("group.field","group_id");
			queryParam.addParam("group.limit", "50");
			queryParam.addParam("group.sort", "ranking_priority desc");
		}
	}

	private void applyGlobalBoosts(QueryParam queryParam, DOTicketSearchRequest request) {
		/*queryParam.addParam("boost", "product(scale(booking_last_7,1,5),0.45)");
		queryParam.addParam("boost", "product(scale(booking_last_90,1,5),0.40)");
		queryParam.addParam("boost", "product(sum(avg_rating,1),0.30)");
		queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,recent_days)))),0.1)");*/
	}

	private void applyFilters(QueryParam queryParam,DOTicketSearchRequest request,Map<String, String> excludeTagMap) throws SearchException{
		handleCityFilter(queryParam,request);
		handleCuisineFilters(queryParam,request,excludeTagMap);
		handleLocationFilters(queryParam,request,excludeTagMap);
		handleAreaFilters(queryParam, request, excludeTagMap);
		handlePriceFilters(queryParam,request,excludeTagMap);
		handleTagsFilters(queryParam, request, excludeTagMap);
		handleAreaLocationFilters(queryParam,request,excludeTagMap);
		handleTicketTypeFilters(queryParam,request,excludeTagMap);
		handleRestaurantNameFilters(queryParam,request,excludeTagMap);
		handleCategoryFilters(queryParam,request,excludeTagMap);
		handleActiveBlockedFilters(queryParam,request);
		handleByDateRangeFilter(queryParam,request);
		handleNumberOfDinersFilters(queryParam, request, excludeTagMap);
	}

	private void handleByDateRangeFilter(QueryParam queryParam, DOTicketSearchRequest request){
		String start=request.getFromDate(); 
		String end=request.getToDate();
		queryParam.addParam("fq", "(from_date_dt:[* TO " + end + "] AND to_date_dt:[" + start + " TO *])");
		queryParam.addParam("bq", "(from_date_dt:["+start +" TO "+end+"] AND to_date_dt:["+start +" TO "+end+"])^5000");
		queryParam.addParam("bq", "(from_date_dt:[* TO " + DODateUtil.getPreviousDate(start) +"] AND to_date_dt:["+ DODateUtil.getNextDate(end) +" TO *])^1500");
		queryParam.addParam("bq","(to_date_dt:["+end+" TO *])^2000");
		queryParam.addParam("bq","(from_date_dt:[* TO "+start+ "])^1500");
		if(DODateUtil.getStringToDate(start).compareTo(DODateUtil.getStringToDate(DODateUtil.getTodaysDate()))==0){

		}
	}

	private void handleActiveBlockedFilters(QueryParam queryParam, DOTicketSearchRequest req) {
		Map<String, Set<?>>datesDays  = DODateUtil.getBetweenDatesDays(DODateUtil.getStringToDate(req.getFromDate()), DODateUtil.getStringToDate(req.getToDate()));
		handleActiveDaysFilter(queryParam,req, datesDays);
		handleBlockedDatesFilters(queryParam,req, datesDays);
	}

	private void handleBlockedDatesFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, Set<?>> datesDays) {
		if(datesDays.containsKey("dates")){
			@SuppressWarnings("unchecked")
			Set<Date> blockedDates = (Set<Date>) datesDays.get("dates");
			String blDatesQrStr=null;
			StringBuilder blDatesFacetQr = new StringBuilder();
			for(Date blockedDay:blockedDates){
				String blocDate = DODateUtil.getDateToString(blockedDay);

				blDatesFacetQr.append("-blocked_date:\""+blocDate+"\"").append(" AND ");
			}
			blDatesQrStr = blDatesFacetQr.substring(0,blDatesFacetQr.lastIndexOf(" AND "));
			queryParam.addParam("fq", blDatesQrStr.toString());
		}	
	}

	private void handleActiveDaysFilter(QueryParam queryParam, 	DOTicketSearchRequest request, Map<String, Set<?>> datesDays) {
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

	private void handleFacetingRequest(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(!Constants.IS_FACET_DISABLED.equals(request.getDisableestfacet())){
			Set<String> facetSet = null;
			String facetLimit = request.getFacetlimit()!=null ? request.getFacetlimit():Constants.DEFAULT_FACET_LIMIT;
			String facetMinCount = request.getFacetmincount()!=null ? request.getFacetmincount(): Constants.DEFAULT_FACET_MIN_COUNT;
			String sortFlag = "count";
			if(Constants.TC_FACET_SORT_INDEX_TRUE.equals(request.getFacetsorttype())){
				sortFlag = "index";
			}
			if(StringUtils.isBlank(request.getEstfacetfl())){
				facetSet = facetUtils.getDefaultDealsFacets();
			}else{
				facetSet = new HashSet<String>();
				String[] facets = request.getEstfacetfl().split(Constants.SEPERATOR);
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
			if(facetSet.contains("number_of_diners")){
				queryParam.addParam("facet.query", (excludeTagMap.get("dinercount")!=null?excludeTagMap.get("dinercount"):"") + "number_of_diners:[1 TO 1]");
				queryParam.addParam("facet.query", (excludeTagMap.get("dinercount")!=null?excludeTagMap.get("dinercount"):"") + "number_of_diners:[2 TO 2]");
				queryParam.addParam("facet.query", (excludeTagMap.get("dinercount")!=null?excludeTagMap.get("dinercount"):"") + "number_of_diners:[3 TO *]");
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

		String sortfieldApplied = "score desc";
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
			if(Constants.SORT_OPTION_TEN.equals(bySort)){
				sortfieldApplied = geoDistance+ " asc";
			}
			if(Constants.SORT_OPTION_ELEVEN.equals(bySort)){
				sortfieldApplied = "avg_rating,"+geoDistance+ " asc";
			}
		}
		else{
			sortfieldApplied = handleDayWiseSorting(queryParam,request);
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handleSortingRequest(QueryParam queryParam, DOTicketSearchRequest request) {
		String bySort = request.getBysort();
		String sortfieldApplied = "score desc";
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
			if(Constants.SORT_OPTION_ELEVEN.equals(bySort)){
				sortfieldApplied = "avg_rating desc";
			}
		}else{
			sortfieldApplied = handleDayWiseSorting(queryParam,request);
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private String handleDayWiseSorting(QueryParam queryParam, DOTicketSearchRequest request){
		String sortApplied = "score desc";
		Date toDate = DODateUtil.getStringToDate(request.getToDate());
		Date fromDate = DODateUtil.getStringToDate(request.getFromDate());
		if(toDate.compareTo(fromDate)==0){
			//same date - 1 date
			String day = DODateUtil.getDay(request.getToDate());
			String solrDayEndTimeField = "day_"+day+"_end";
			if(request.getToDate().compareTo(DODateUtil.getTodaysDate())==0){
				long timeInMinutes = DODateUtil.getCurrentTimeInMinutes();
				sortApplied = "sum(product(max(min(sum(sub("+solrDayEndTimeField+","+timeInMinutes+"),1),1),0),sub("+solrDayEndTimeField+","+timeInMinutes+")),product(max(min(product(-1,sub("+solrDayEndTimeField+","+timeInMinutes+")),1),0),720)) asc";
			}
			else if(request.getToDate().compareTo(DODateUtil.getTomorrowsDate())==0){
				sortApplied=solrDayEndTimeField+" asc"; 
			}
		}
		return sortApplied;
	}
	
	private void handleNumberOfDinersFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) throws SearchException {
		if(request.getBydinerCount()!=null && request.getBydinerCount().length>0){
			StringBuilder dinerCountFacetQr = new StringBuilder();
			String dinerCountFacetQrStr = null;
			for(String dinerCount:request.getBydinerCount()){
				try {
					dinerCount = URLDecoder.decode(dinerCount, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(),e);
					throw new SearchException(e.getMessage(),e.getCause(),ErrorCode.URL_DECODE_ERROR);
				}
				String[] dinerCountRange = dinerCount.split("-");
				dinerCountFacetQr.append("number_of_diners:["+dinerCountRange[0]+" TO "+dinerCountRange[1]+"]").append(" OR ");
			}
			dinerCountFacetQrStr = dinerCountFacetQr.substring(0,dinerCountFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "{!tag=dinercount}("+dinerCountFacetQrStr+")");
			excludeTagMap.put("price", "{!ex=dinercount}");
		}
	}

	private void handlePriceFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) throws SearchException {
		if(request.getByprice()!=null && request.getByprice().length>0){
			StringBuilder priceFacetQr = new StringBuilder();
			String priceFacetQrStr = null;
			for(String price:request.getByprice()){
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

	private void handleTagsFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getBytags()!=null && request.getBytags().length>0){
			StringBuilder facilityQr = new StringBuilder();
			String facilityQrStr = null;
			for(String facility:request.getBytags()){
				facilityQr.append("tags_ft:\""+facility+"\"").append(" AND ");
			}
			facilityQrStr = facilityQr.substring(0,facilityQr.lastIndexOf(" AND "));

			queryParam.addParam("fq", "{!tag=tags_ft_tag}("+facilityQrStr+")");
			excludeTagMap.put("tags", "{!ex=tags_ft_tag}");
		}
	}

	private void handleAreaFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getByarea()!=null && request.getByarea().length>0){
			StringBuilder zoneFacetQr = new StringBuilder();
			String zoneFacetQrStr = null;
			for(String zone:request.getByarea()){
				zoneFacetQr.append("area_name_ft:\""+zone+"\"").append(" OR ");
			}
			zoneFacetQrStr = zoneFacetQr.substring(0,zoneFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=area_name_ft_tag}("+zoneFacetQrStr+")");
			excludeTagMap.put("area", "{!ex=area_name_ft_tag}");
		}
	}

	private void handleLocationFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getBylocation()!=null && request.getBylocation().length>0){
			StringBuilder locationFacetQr = new StringBuilder();
			String locationFacetQrStr=null;
			for(String location:request.getBylocation()){
				locationFacetQr.append("locality_name_ft:\""+location+"\"").append(" OR ");
			}
			locationFacetQrStr = locationFacetQr.substring(0,locationFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=locality_name_ft_tag}("+locationFacetQrStr.toString()+")");
			excludeTagMap.put("location", "{!ex=locality_name_ft_tag}");
		}
	}

	private void handleCuisineFilters(QueryParam queryParam, DOTicketSearchRequest request, Map<String, String> excludeTagMap) {
		if(request.getBycuisine()!=null && request.getBycuisine().length>0){
			StringBuilder cuisineFacetQr = new StringBuilder();
			StringBuilder primaryCuisineFacetQr = new StringBuilder();
			StringBuilder secondaryCuisineFacetQr = new StringBuilder();
			String cuisineFacetQrStr = null;
			String primaryCuisineFacetQrStr = null;
			for(String cuisine:request.getBycuisine()){
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
