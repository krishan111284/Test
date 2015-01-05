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
		handleGroupRequest(queryParam,req);
		applyFilters(queryParam, req, excludeTagMap);
		handleNerEntity(queryParam, nerMap, req);
		handleFacetingRequest(queryParam, req, excludeTagMap);
		if(req.isSpatialQuery()){
			handleSpatialSortingRequest(queryParam,req);
		}else{
			handleSortingRequest(queryParam,req);
		}
		if(Constants.IS_HL_TRUE.equals(req.getEsthl())){
			setHlParams(queryParam, req.getEsthlfl());
		}
		return queryParam;
	}
	
	private void applyFilters(QueryParam queryParam,DORestSearchRequest req,Map<String, String> excludeTagMap) throws SearchException{
		//city filter
		if(!StringUtils.isEmpty(req.getBycity())){
			queryParam.addParam("fq", "city_name:\""+req.getBycity()+"\"");
		}

		if(!StringUtils.isEmpty(req.getByfeaturetags())){
			queryParam.addParam("fq", "feature_tags_string:"+req.getByfeaturetags());
		}
		handleEstGroupFilter(queryParam, req);
		handleCuisineFilters(queryParam,req,excludeTagMap);
		handleLocationFilters(queryParam,req,excludeTagMap);
		handleLandmarkFilters(queryParam,req,excludeTagMap);
		handleAreaFilters(queryParam, req, excludeTagMap);
		handlePriceFilters(queryParam,req,excludeTagMap);
		handleTagsFilters(queryParam, req, excludeTagMap);
		if(!StringUtils.isEmpty(req.getByrate())){
			String[] ratingRange = req.getByrate().split("-");
			queryParam.addParam("fq", "est_food_rating:["+ratingRange[0]+" TO "+ratingRange[1]+"]");
		}
		if(!StringUtils.isEmpty(req.getBytags())){
			String byTags = null;
			try {
				byTags = URLDecoder.decode(req.getBytags(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(),e);
				throw new SearchException(e.getMessage(),e.getCause(),ErrorCode.URL_DECODE_ERROR);
			}
			queryParam.addParam("fq","est_tags:\""+byTags+"\"");
		}
		if(!StringUtils.isEmpty(req.getByestisfeatured())){
			queryParam.addParam("fq","est_is_featured:\""+req.getByestisfeatured()+"\"");
		}

	}

	private void handleNerEntity(QueryParam queryParam,
			Map<String, String> nerMap,DORestSearchRequest req) {
		if(nerMap!=null && nerMap.size()>0){
			if(nerMap.containsKey(Constants.NER_ZONE_KEY)){
				handleNerZone(queryParam,nerMap.get(Constants.NER_ZONE_KEY));
			}
			if(nerMap.containsKey(Constants.NER_CUISINE_KEY)){
				handleNerCuisine(queryParam,nerMap.get(Constants.NER_CUISINE_KEY));
			}
			
			
		}

	}


	private void handleNerCuisine(QueryParam queryParam, String nerCuisine) {
		String primCuisineBoost1 = "(cuisine_priorities_list_ft:(\""+nerCuisine+"\") AND est_food_rating:[3.0 TO 5.0])^100000";
		String secCuisineBoost1 = "(cuisine_secondary_list_ft:(\""+nerCuisine+"\") AND -cuisine_priorities_list_ft:("+nerCuisine+") AND est_food_rating:[3.0 TO 5.0])^10000";
		String primCuisineBoost2 = "(cuisine_priorities_list_ft:(\""+nerCuisine+"\") AND -est_food_rating:[3.0 TO 5.0] )^5000";
		String secCuisineBoost2 = "(cuisine_secondary_list_ft:(\""+nerCuisine+"\") AND -cuisine_priorities_list_ft:("+nerCuisine+") AND -est_food_rating:[3.0 TO 5.0])^1";

		queryParam.addParam("bq", primCuisineBoost1);
		queryParam.addParam("bq", secCuisineBoost1);
		queryParam.addParam("bq", primCuisineBoost2);
		queryParam.addParam("bq", secCuisineBoost2);
	}

	private void handleNerZone(QueryParam queryParam, String nerZone) {
		String zoneBoost = "(zone_name_ft:\""+nerZone+"\")^100000";
		queryParam.addParam("bq", zoneBoost);
	}

	private void handleEstGroupFilter(QueryParam queryParam,
			DORestSearchRequest req) {
		if(req.getByestgroupname()!=null && req.getByestgroupname().length>0){
			StringBuilder estGroupFilterQr = new StringBuilder();
			String estGroupFilterQrStr=null;
			for(String estGroup:req.getByestgroupname()){
				estGroupFilterQr.append("est_grp_name_ft:\""+estGroup+"\"").append(" OR ");
			}
			estGroupFilterQrStr = estGroupFilterQr.substring(0,estGroupFilterQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", estGroupFilterQrStr.toString());
		}
	}


	
	private void handleGroupRequest(QueryParam queryParam,
			DORestSearchRequest req) {
		if(!StringUtils.isEmpty(req.getByGroup())){
			queryParam.addParam("fq", "est_group:"+req.getByGroup());
		}
		if(StringUtils.isEmpty(req.getByGroup()) && Constants.GROUP_TRUE.equals(req.getGroup())){
			queryParam.addParam("group","true");
			queryParam.addParam("group.field","est_group");
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
			if(facetSet.contains("loc_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("location")!=null?excludeTagMap.get("location"):"")+"loc_name_ft");}
			if(facetSet.contains("cuisines_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("cuisine")!=null?excludeTagMap.get("cuisine"):"")+"cuisines_name_ft");}
			if(facetSet.contains("feature_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("feature")!=null?excludeTagMap.get("feature"):"")+"feature_name_ft");}
			if(facetSet.contains("est_hotel_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("landmark")!=null?excludeTagMap.get("landmark"):"")+"est_hotel_name_ft");}
			if(facetSet.contains("zone_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("zone")!=null?excludeTagMap.get("zone"):"")+"zone_name_ft");}
			if(facetSet.contains("service_tags_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("serviceTags")!=null?excludeTagMap.get("serviceTags"):"")+"service_tags_ft");}
			if(facetSet.contains("deals")){queryParam.addParam("facet.field",(excludeTagMap.get("deals")!=null?excludeTagMap.get("deals"):"")+"deals");}
			if(facetSet.contains("est_which_type_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("whichTypeTags")!=null?excludeTagMap.get("whichTypeTags"):"")+"est_which_type_ft");}
			if(facetSet.contains("est_type_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("bytypeNameTags")!=null?excludeTagMap.get("bytypeNameTags"):"")+"est_type_name_ft");}
			if(facetSet.contains("est_two_price")){
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "est_two_price:[0 TO 500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "est_two_price:[501 TO 1000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "est_two_price:[1001 TO 1500]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "est_two_price:[1501 TO 2000]");
				queryParam.addParam("facet.query", (excludeTagMap.get("price")!=null?excludeTagMap.get("price"):"") + "est_two_price:[2001 TO *]");
			}
			if(facetSet.contains("est_food_rating")){
				queryParam.addParam("facet.query", "est_food_rating:[4.5 TO 5.0]");
				queryParam.addParam("facet.query", "est_food_rating:[3.5 TO 4.5]");
				queryParam.addParam("facet.query", "est_food_rating:[2.5 TO 3.5]");
				queryParam.addParam("facet.query", "est_food_rating:[1.5 TO 2.5]");
				queryParam.addParam("facet.query", "est_food_rating:[0.5 TO 1.5]");
			}
			queryParam.addParam("facet.limit",facetLimit);
			queryParam.addParam("facet.mincount", ""+facetMinCount);
			queryParam.addParam("facet.sort", sortFlag);
		}
	}

	private void handleSortingRequest(QueryParam queryParam,
			DORestSearchRequest restSearchReq) {
		String bySort = restSearchReq.getBysort();
		if(!StringUtils.isEmpty(bySort)){
			String sortfieldApplied = "";
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "est_food_rating asc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "est_food_rating desc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "est_two_price asc";
				queryParam.addParam("fq", "!est_two_price:0");
			}
			if(Constants.SORT_OPTION_FOUR.equals(bySort)){
				sortfieldApplied = "est_two_price desc";
			}
			if(Constants.SORT_OPTION_NINE.equals(bySort)){
				sortfieldApplied = "est_name asc";
			}
			queryParam.addParam("sort", sortfieldApplied);
		}
	}


	private void handleSpatialSortingRequest(QueryParam queryParam,
			DORestSearchRequest tcMainSearchReq) {
		String geoDistance = "geodist(lat_lng," + tcMainSearchReq.getLat() +","+tcMainSearchReq.getLng()+")";
		String sortfieldApplied = geoDistance + "asc";
		String bySort = tcMainSearchReq.getBysort();
		String spatialQuery = "{!geofilt sfield=lat_lng pt=" + tcMainSearchReq.getLat() + "," + tcMainSearchReq.getLng() + " d=" + tcMainSearchReq.getRadius() + "}";
		queryParam.addParam("fq", spatialQuery);

		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "est_food_rating desc," + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "est_two_price asc," + geoDistance + " asc";
				queryParam.addParam("fq", "!est_two_price:0");
			}
			if(Constants.SORT_OPTION_SEVEN.equals(bySort)){
				sortfieldApplied = "score desc," + geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_EIGHT.equals(bySort)){
				sortfieldApplied = geoDistance + " asc";
			}
			if(Constants.SORT_OPTION_NINE.equals(bySort)){
				sortfieldApplied = "est_name asc,"  + geoDistance + " asc";
			}
			queryParam.addParam("sort", sortfieldApplied);
		}
	}


	private void handlePriceFilters(QueryParam queryParam,
			DORestSearchRequest tcMainSearchReq, Map<String, String> excludeTagMap) throws SearchException {
		if(tcMainSearchReq.getByprice()!=null && tcMainSearchReq.getByprice().length>0){
			StringBuilder priceFacetQr = new StringBuilder();
			String priceFacetQrStr = null;
			for(String price:tcMainSearchReq.getByprice()){
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
		if(restSearchReq.getByfs()!=null && restSearchReq.getByfs().length>0){
			StringBuilder facilityQr = new StringBuilder();
			String facilityQrStr = null;
			for(String facility:restSearchReq.getByfs()){
				facilityQr.append("tags_ft:\""+facility+"\"").append(" AND ");
			}
			facilityQrStr = facilityQr.substring(0,facilityQr.lastIndexOf(" AND "));

			queryParam.addParam("fq", "{!tag=tags_ft_tag}("+facilityQrStr+")");
			excludeTagMap.put("feature", "{!ex=tags_ft_tag}");
		}
	}

	private void handleLandmarkFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getByinsidelandmark()!=null && restSearchReq.getByinsidelandmark().length>0){
			StringBuilder landmarkFacetQr = new StringBuilder();
			String landmarkFacetQrStr = null;
			for(String landmark:restSearchReq.getByinsidelandmark()){
				landmarkFacetQr.append("landmark_ft:\""+landmark+"\"").append(" OR ");
			}
			landmarkFacetQrStr = landmarkFacetQr.substring(0,landmarkFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=landmark_ft_tag}("+landmarkFacetQrStr+")");
			excludeTagMap.put("landmark", "{!ex=landmark_ft_tag}");
		}
	}
	private void handleAreaFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getByzone()!=null && restSearchReq.getByzone().length>0){
			StringBuilder zoneFacetQr = new StringBuilder();
			String zoneFacetQrStr = null;
			for(String zone:restSearchReq.getByzone()){
				zoneFacetQr.append("area_name_ft:\""+zone+"\"").append(" OR ");
			}
			zoneFacetQrStr = zoneFacetQr.substring(0,zoneFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=area_name_ft_tag}("+zoneFacetQrStr+")");
			excludeTagMap.put("zone", "{!ex=area_name_ft_tag}");
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
			queryParam.addParam("mm","100%");
		}
		if(!StringUtils.isEmpty(req.getSearchname())){
			setQfParams(queryParam);
			setPfParams(queryParam);
		}
		setResponseNumLimit(queryParam,req);
	}
	private void setQfParams(QueryParam queryParam) {
		queryParam.addParam("qf", rb.getString("tc.mainsearch.qf.param"));
	}

	private void setPfParams(QueryParam queryParam) {
		queryParam.addParam("pf",rb.getString("tc.mainsearch.pf.param"));
		queryParam.addParam("pf2",rb.getString("tc.mainsearch.pf2.param"));
	}

}
