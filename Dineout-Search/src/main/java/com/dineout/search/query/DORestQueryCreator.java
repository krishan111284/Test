package com.dineout.search.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.FacetUtils;

@Component("restQueryCreator")
public class DORestQueryCreator extends DOAbstractQueryCreator {
	Logger logger = Logger.getLogger(DORestQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");
	
	@Autowired
	FacetUtils facetUtils;

	public QueryParam getSearchQuery(DORestSearchRequest req,
			Map<String, String> nerMap) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		Map<String,String> excludeTagMap = new HashMap<String,String>();
		initializeQueryCreator(req, queryParam, req.getEstfl());
		queryString = !StringUtils.isEmpty(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, req);
		//handleGroupRequest(queryParam,req); TODO: Field to be shared
		applyFilters(queryParam, req, excludeTagMap);
		handleNerEntity(queryParam, nerMap, req);
		handleFacetingRequest(queryParam, req, excludeTagMap);
		applyGlobalBoosts(queryParam,req);
		if(req.isSpatialQuery()){
			handleSpatialSortingRequest(queryParam,req);
		}else{
			handleSortingRequest(queryParam,req);
		}
		if(Constants.IS_HL_TRUE.equals(req.getEsthl())){
			setHlParams(queryParam, req.getEsthlfl(),req.getSearchname());
		}
		return queryParam;
	}
	
	private void applyGlobalBoosts(QueryParam queryParam,
			DORestSearchRequest req) {
		queryParam.addParam("boost", "product(booking_count,0.5)");
		queryParam.addParam("boost", "product(avg_rating,0.25)");
		queryParam.addParam("boost", "product(rank,0.25)");
		
	}

	private void applyFilters(QueryParam queryParam,DORestSearchRequest req,Map<String, String> excludeTagMap) throws SearchException{
		if(!StringUtils.isEmpty(req.getBycity())){
			queryParam.addParam("fq", "city_name:\""+req.getBycity()+"\"");
		}
		handleCuisineFilters(queryParam,req,excludeTagMap);
		handleLocationFilters(queryParam,req,excludeTagMap);
		handleLandmarkFilters(queryParam,req,excludeTagMap);
		handleAreaFilters(queryParam, req, excludeTagMap);
		handlePriceFilters(queryParam,req,excludeTagMap);
		handleTagsFilters(queryParam, req, excludeTagMap);
		handleRatingsFilters(queryParam, req,excludeTagMap);
		
	}
	private void handleNerEntity(QueryParam queryParam,
			Map<String, String> nerMap,DORestSearchRequest req) {
		if(nerMap!=null && nerMap.size()>0){
			
			if(nerMap.containsKey(Constants.NER_CUISINE_KEY)){
				handleNerCuisine(queryParam,nerMap);
			}
		}
	}

	private void handleNerCuisine(QueryParam queryParam, Map<String, String> nerMap) {
		applyFamilyFilter(queryParam,nerMap);
		applyCuisineBoosts(queryParam,nerMap);
		changeQueryString(queryParam,nerMap);
	}

	
	private void changeQueryString(QueryParam queryParam,
			Map<String, String> nerMap) {
		queryParam.updateParam("q", nerMap.get(Constants.PROCESSED_QUERY));
	}

	private void applyCuisineBoosts(QueryParam queryParam,
			Map<String, String> nerMap) {
		queryParam.addParam("bq", "(primary_cuisine_ft:"+nerMap.get(Constants.NER_CUISINE_KEY)+")^10000");
		queryParam.addParam("bq", "(secondary_cuisine_ft:"+nerMap.get(Constants.NER_CUISINE_KEY)+")^5000");
		
	}

	private void applyFamilyFilter(QueryParam queryParam,
			Map<String, String> nerMap) {
		queryParam.addParam("fq", "((primary_family_ft:"+nerMap.get(Constants.NER_CUISINE_FAMILY_KEY)+") OR (secondary_family_ft:"+nerMap.get(Constants.NER_CUISINE_FAMILY_KEY)+"))");
		
	}

	private void handleRatingsFilters(QueryParam queryParam, DORestSearchRequest req,Map<String, String> excludeTagMap){
		
		if(req.getByrate()!=null && req.getByrate().length>0){StringBuilder rateFacetQr = new StringBuilder();
		String rateFacetQrStr = null;
		for(String rate:req.getByrate()){
			String[] rateRange = rate.split("-");
			rateFacetQr.append("avg_rating:["+rateRange[0]+" TO "+rateRange[1]+"]").append(" OR ");
		}
		rateFacetQrStr = rateFacetQr.substring(0,rateFacetQr.lastIndexOf(" OR "));

		queryParam.addParam("fq", "{!tag=avg_rating_tag}("+rateFacetQrStr+")");
		excludeTagMap.put("rate", "{!ex=avg_rating_tag}");
	}
	}
	
	
	//NEED TO GET FIELD FOR GROUPING!!
	private void handleGroupRequest(QueryParam queryParam,
			DORestSearchRequest req) {
		if(!StringUtils.isEmpty(req.getBygroup())){
			queryParam.addParam("fq", "est_group:"+req.getBygroup());
		}
		if(StringUtils.isEmpty(req.getBygroup()) && Constants.GROUP_TRUE.equals(req.getGroup())){
			queryParam.addParam("group","true");
			queryParam.addParam("group.field","est_group"); //GET NAME OF FIELD
			queryParam.addParam("group.limit", "1");
			queryParam.addParam("group.ngroups", "true");
		}
	}

	private void handleFacetingRequest(QueryParam queryParam,
			DORestSearchRequest restSearchReq,
			Map<String, String> excludeTagMap) {
		if(!Constants.IS_FACET_DISABLED.equals(restSearchReq.getDisableestfacet())){
			Set<String> facetSet = null;
			String facetLimit = restSearchReq.getFacetlimit()!=null ? restSearchReq.getFacetlimit():Constants.DEFAULT_FACET_LIMIT;
			String facetMinCount = restSearchReq.getFacetmincount()!=null ? restSearchReq.getFacetmincount(): Constants.DEFAULT_FACET_MIN_COUNT;
			String sortFlag = "count";
			if(Constants.TC_FACET_SORT_INDEX_TRUE.equals(restSearchReq.getFacetsorttype())){
				sortFlag = "index";
			}
			if(StringUtils.isBlank(restSearchReq.getEstfacetfl())){
				facetSet = facetUtils.getDefaultRestFacets();
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
			if(facetSet.contains("landmark_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("landmark")!=null?excludeTagMap.get("landmark"):"")+"landmark_ft");}
			if(facetSet.contains("area_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("area")!=null?excludeTagMap.get("area"):"")+"area_name_ft");}
			if(facetSet.contains("tags_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("tags")!=null?excludeTagMap.get("tags"):"")+"tags_ft");}
			if(facetSet.contains("costFor2")){
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "costFor2:[0 TO 500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "costFor2:[501 TO 1000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "costFor2:[1001 TO 1500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "costFor2:[1501 TO 2000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "costFor2:[2001 TO *]");
			}
			if(facetSet.contains("avg_rating")){
				queryParam.addParam("facet.query", (excludeTagMap.get("rate")!=null?excludeTagMap.get("rate"):"") +"avg_rating:[4.5 TO 5.0]");
				queryParam.addParam("facet.query", (excludeTagMap.get("rate")!=null?excludeTagMap.get("rate"):"") +"avg_rating:[3.5 TO 4.5]");
				queryParam.addParam("facet.query", (excludeTagMap.get("rate")!=null?excludeTagMap.get("rate"):"") +"avg_rating:[2.5 TO 3.5]");
				queryParam.addParam("facet.query", (excludeTagMap.get("rate")!=null?excludeTagMap.get("rate"):"") +"avg_rating:[1.5 TO 2.5]");
				queryParam.addParam("facet.query", (excludeTagMap.get("rate")!=null?excludeTagMap.get("rate"):"") +"avg_rating:[0.5 TO 1.5]");
			}
			queryParam.addParam("facet.limit",facetLimit);
			queryParam.addParam("facet.mincount", ""+facetMinCount);
			queryParam.addParam("facet.sort", sortFlag);
		}
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
				sortfieldApplied = "fullfillment desc,est_two_price asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,est_two_price desc";
			}
			if(Constants.SORT_OPTION_FIVE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating asc";
			}
			if(Constants.SORT_OPTION_SIX.equals(bySort)){
				sortfieldApplied = "fullfillment desc,avg_rating desc";
			}
		}else{
			sortfieldApplied = "fullfillment desc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}


	private void handleSpatialSortingRequest(QueryParam queryParam,
			DORestSearchRequest restSearchReq) {
		String geoDistance = "geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+")";
		String sortfieldApplied = "";
		String bySort = restSearchReq.getBysort();
		String spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getLat() + "," + restSearchReq.getLng() + " d=" + restSearchReq.getRadius() + "}";
		queryParam.addParam("fq", spatialQuery);

		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count asc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,booking_count desc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,est_two_price asc,"  + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,est_two_price desc,"  + geoDistance + " asc";
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
			sortfieldApplied = "fullfillment desc";
			queryParam.addParam("boost","div(1,sqrt(sum(1,mul(0.4,sub(sum(abs(sub("+geoDistance+",0)),abs(sub("+geoDistance+","+Integer.parseInt(restSearchReq.getRadius())/2+"))),sub("+Integer.parseInt(restSearchReq.getRadius())/2+",0))))))");
		}
		queryParam.addParam("sort", sortfieldApplied);
	}


	private void handlePriceFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) throws SearchException {
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
				priceFacetQr.append("costFor2:["+priceRange[0]+" TO "+priceRange[1]+"]").append(" OR ");
			}
			priceFacetQrStr = priceFacetQr.substring(0,priceFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=costFor2_tag}("+priceFacetQrStr+")");
			excludeTagMap.put("price", "{!ex=costFor2_tag}");
		}
	}

	
	private void handleTagsFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
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

	private void handleLandmarkFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBylandmark()!=null && restSearchReq.getBylandmark().length>0){
			StringBuilder landmarkFacetQr = new StringBuilder();
			String landmarkFacetQrStr = null;
			for(String landmark:restSearchReq.getBylandmark()){
				landmarkFacetQr.append("landmark_ft:\""+landmark+"\"").append(" OR ");
			}
			landmarkFacetQrStr = landmarkFacetQr.substring(0,landmarkFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=landmark_ft_tag}("+landmarkFacetQrStr+")");
			excludeTagMap.put("landmark", "{!ex=landmark_ft_tag}");
		}
	}
	private void handleAreaFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
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

	private void handleLocationFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
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

	private void handleCuisineFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBycuisine()!=null && restSearchReq.getBycuisine().length>0){
			StringBuilder cuisineFacetQr = new StringBuilder();
			String cuisineFacetQrStr = null;
			for(String cuisine:restSearchReq.getBycuisine()){
				cuisine.replaceAll("~","/");
				cuisineFacetQr.append("cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
			}
			cuisineFacetQrStr = cuisineFacetQr.substring(0,cuisineFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "{!tag=cuisine_ft_tag}("+cuisineFacetQrStr.toString()+")");
			excludeTagMap.put("cuisine", "{!ex=cuisine_ft_tag}");
		}
	}

	private void setQueryParser(QueryParam queryParam,DORestSearchRequest req){
		queryParam.addParam("defType","edismax");
		if(req.isSearchExecuted()){
			String query = req.getSearchname();
			String[] tokens = query.split(" ");
			queryParam.addParam("mm",tokens.length+"");
		}
		if(!StringUtils.isEmpty(req.getSearchname())){
			setQfParams(queryParam);
			setPfParams(queryParam);
		}
		setResponseNumLimit(queryParam,req);
	}
	private void setQfParams(QueryParam queryParam) {
		queryParam.addParam("qf", rb.getString("dineout.search.qf.param"));
	}

	private void setPfParams(QueryParam queryParam) {
		queryParam.addParam("pf",rb.getString("dineout.search.pf.param"));
		queryParam.addParam("pf2",rb.getString("dineout.search.pf2.param"));
	}

}
