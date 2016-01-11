package com.dineout.search.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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
			Map<String, ArrayList<String>> nerMap) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		Map<String,String> excludeTagMap = new HashMap<String,String>();
		initializeQueryCreator(req, queryParam, req.getEstfl(),rb.getString("dineout.search.fl"));
		queryString = !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, req,nerMap);
		//handleGroupRequest(queryParam,req); TODO: Field to be shared
		applyFilters(queryParam, req, excludeTagMap);
		handleNerEntity(queryParam, nerMap, req);
		handleFacetingRequest(queryParam, req, excludeTagMap);
		if(req.getOldModel()!=null && req.getOldModel().equalsIgnoreCase("true"))
			applyOldGlobalBoosts(queryParam, req);
		else if(req.isSpatialQuery())
			applySpatialGlobalBoosts(queryParam,req);
		else
			applyGlobalBoosts(queryParam,req);	
		if(req.isSpatialQuery() || req.isEntitySpatialQuery()){
			handleSpatialSortingRequest(queryParam,req);
		}else{
			handleSortingRequest(queryParam,req);
		}
		if(Constants.IS_HL_TRUE.equals(req.getEsthl())){
			setHlParams(queryParam, req.getEsthlfl(),req.getSearchname());
		}
		return queryParam;
	}

	private void applyGlobalBoosts(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("boost", "product(scale(booking_last_7,1,5),0.45)");
		queryParam.addParam("boost", "product(scale(booking_last_90,1,5),0.40)");
		queryParam.addParam("boost", "product(sum(avg_rating,1),0.30)");
		queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,recent_days)))),0.1)");
	}

	private void applySpatialGlobalBoosts(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("boost", "product(scale(booking_last_7,1,5),0.45)");
		queryParam.addParam("boost", "product(scale(booking_last_90,1,5),0.40)");
		queryParam.addParam("boost", "product(sum(avg_rating,1),0.30)");
		//queryParam.addParam("boost", "product(div(1,sum(pow(2.71,product(0.005,recent_days)))),0.1)");
		//queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,recent_days)))),0.1)");
	}

	private void applyOldGlobalBoosts(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("boost", "product(scale(booking_count,1,5),0.35)");
		queryParam.addParam("boost", "product(sum(avg_rating,1),0.30)");
		queryParam.addParam("boost", "if(exists(rank),product(div(sub(11,rank),2),0.15),0.01)");

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
		handleFeaturesFilters(queryParam, req,excludeTagMap);
		handleHotelFilters(queryParam, req,excludeTagMap);
		handleChainFilters(queryParam, req,excludeTagMap);
		handleAreaLocationFilters(queryParam,req,excludeTagMap);
		handleTypeFilters(queryParam,req,excludeTagMap);
	}

	private void handleTypeFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getByType()!=null){
			if(restSearchReq.getByType().equalsIgnoreCase("1"))
				queryParam.addParam("fq", "fullfillment:true");
			if(restSearchReq.getByType().equalsIgnoreCase("2"))
				queryParam.addParam("fq", "fullfillment:false");
		}
	}

	private void handleAreaLocationFilters(QueryParam queryParam,
			DORestSearchRequest restSearchReq, Map<String, String> excludeTagMap) {
		if(restSearchReq.getBylocarea()!=null && restSearchReq.getBylocarea().length>0){
			StringBuilder locationAreaFacetQr = new StringBuilder();
			String locationAreaFacetQrStr=null;
			for(String locationArea:restSearchReq.getBylocarea()){
				locationAreaFacetQr.append("locality_area_ft:\""+locationArea+"\"").append(" OR ");
			}
			locationAreaFacetQrStr = locationAreaFacetQr.substring(0,locationAreaFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=locality_area_name_ft_tag}("+locationAreaFacetQrStr.toString()+")");
			excludeTagMap.put("locationArea", "{!ex=locality_area_name_ft_tag}");
		}
	}

	/*private void handleAreaLocationFilters(QueryParam queryParam,
			DORestSearchRequest req, Map<String, String> excludeTagMap) {
		ArrayList<String>commonList = new ArrayList<String>();
		StringBuilder areaLocFacetQr = new StringBuilder();

		if(req.getByarea()!=null && req.getByarea().length>0){
			for(String area:req.getByarea()){
				commonList.add("area_name_ft:"+"\""+area+"\"");
			}
		}

		if(req.getBylocation()!=null && req.getBylocation().length>0){
			for(String location:req.getBylocation()){
				commonList.add("locality_name_ft:"+"\""+location+"\"");
			}
		}

		for (String alFilter: commonList){
			areaLocFacetQr.append(alFilter).append(" OR ");
		}
		if(commonList.size()>0){
			String areaLocFilter = areaLocFacetQr.substring(0,areaLocFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "{!tag=area_loc_ft_tag}("+areaLocFilter+")");
			excludeTagMap.put("area_loc", "{!ex=area_loc_ft_tag}");
		}
	}*/

	private void handleChainFilters(QueryParam queryParam,DORestSearchRequest req, Map<String, String> excludeTagMap) {
		if(req.getBychain()!=null && req.getBychain().length>0){
			StringBuilder chainFacetQr = new StringBuilder();
			String chainFacetQrStr=null;
			for(String hotel:req.getBychain()){
				chainFacetQr.append("chain_name:\""+hotel+"\"").append(" OR ");
			}
			chainFacetQrStr = chainFacetQr.substring(0,chainFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=chain_ft_tag}("+chainFacetQrStr.toString()+")");
			excludeTagMap.put("chain", "{!ex=chain_ft_tag}");
		}			

	}

	private void handleHotelFilters(QueryParam queryParam, DORestSearchRequest req, Map<String, String> excludeTagMap) {
		if(req.getByhotel()!=null && req.getByhotel().length>0){
			StringBuilder hotelFacetQr = new StringBuilder();
			String hotelFacetQrStr=null;
			for(String hotel:req.getByhotel()){
				hotelFacetQr.append("hotel_ft:\""+hotel+"\"").append(" OR ");
			}
			hotelFacetQrStr = hotelFacetQr.substring(0,hotelFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=hotel_ft_tag}("+hotelFacetQrStr.toString()+")");
			excludeTagMap.put("hotel", "{!ex=hotel_ft_tag}");
		}			
	}

	private void handleFeaturesFilters(QueryParam queryParam, DORestSearchRequest req, Map<String, String> excludeTagMap) {
		if(req.getByfeatures()!=null && req.getByfeatures().length>0){
			StringBuilder featureFacetQr = new StringBuilder();
			String featureFacetQrStr=null;
			for(String feature:req.getByfeatures()){
				featureFacetQr.append("features_ft:\""+feature+"\"").append(" OR ");
			}
			featureFacetQrStr = featureFacetQr.substring(0,featureFacetQr.lastIndexOf(" OR "));

			queryParam.addParam("fq", "{!tag=features_ft_tag}("+featureFacetQrStr.toString()+")");
			excludeTagMap.put("features", "{!ex=features_ft_tag}");
		}		
	}

	private void handleNerEntity(QueryParam queryParam,
			Map<String, ArrayList<String>> nerMap,DORestSearchRequest req) {
		if(nerMap!=null && nerMap.size()>0){

			if(nerMap.containsKey(Constants.NER_CUISINE_KEY)){
				handleNerCuisine(queryParam,nerMap);
			}
		}
	}

	private void handleNerCuisine(QueryParam queryParam, Map<String, ArrayList<String>> nerMap) {
		applyFamilyFilter(queryParam,nerMap);
		applyCuisineBoosts(queryParam,nerMap);
		changeQueryString(queryParam,nerMap);
	}

	private void changeQueryString(QueryParam queryParam,Map<String, ArrayList<String>> nerMap) {
		if(nerMap.get(Constants.PROCESSED_QUERY).get(0).trim().isEmpty())
			queryParam.updateParam("q", "*:*");
		else
			queryParam.updateParam("q", nerMap.get(Constants.PROCESSED_QUERY).get(0));
	}

	private void applyCuisineBoosts(QueryParam queryParam,Map<String, ArrayList<String>> nerMap) {
		for(String childCuisine:nerMap.get(Constants.NER_CUISINE_KEY)){
			queryParam.addParam("bq", "(primary_cuisine_ft:"+childCuisine+")^40000");
			queryParam.addParam("bq", "(secondary_cuisine_ft:"+childCuisine+")^10000");	
		}
	}

	private void applyFamilyFilter(QueryParam queryParam,Map<String, ArrayList<String>> nerMap) {

		StringBuilder familyFilterBuilder = new StringBuilder();
		String fqQrStr = null;
		for(String cuisineFamily:nerMap.get(Constants.NER_CUISINE_FAMILY_KEY)){
			familyFilterBuilder.append("primary_family_ft:\""+cuisineFamily+"\"").append(" OR ");
			familyFilterBuilder.append("secondary_family_ft:\""+cuisineFamily+"\"").append(" OR ");
		}
		fqQrStr = familyFilterBuilder.substring(0,familyFilterBuilder.lastIndexOf(" OR "));
		queryParam.addParam("fq", fqQrStr);
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
	/*	private void handleGroupRequest(QueryParam queryParam,
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
	}*/

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
			if(facetSet.contains("hotel_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("hotel")!=null?excludeTagMap.get("hotel"):"")+"hotel_ft");}
			if(facetSet.contains("chain_name")){queryParam.addParam("facet.field",(excludeTagMap.get("chain")!=null?excludeTagMap.get("chain"):"")+"chain_name");}
			if(facetSet.contains("features_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("features")!=null?excludeTagMap.get("features"):"")+"features_ft");}
			if(facetSet.contains("area_name_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("area")!=null?excludeTagMap.get("area"):"")+"area_name_ft");}
			if(facetSet.contains("tags_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("tags")!=null?excludeTagMap.get("tags"):"")+"tags_ft");}
			if(facetSet.contains("locality_area_ft")){queryParam.addParam("facet.field",(excludeTagMap.get("locationArea")!=null?excludeTagMap.get("locationArea"):"")+"locality_area_ft");}
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

	private void handleSpatialSortingRequest(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		String spatialQuery = "";
		String geoDistance = "";

		if(restSearchReq.isSpatialQuery() && restSearchReq.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getElat() + "," + restSearchReq.getElng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+")";
			queryParam.addParam("boost", "scale(div(1,sum(1,product(1,geodist(lat_lng,"+restSearchReq.getLat()+","+restSearchReq.getLng()+")))),0,5)");
		}
		else if(restSearchReq.isEntitySpatialQuery()){
			spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getElat() + "," + restSearchReq.getElng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getElat() +","+restSearchReq.getElng()+")";
			queryParam.addParam("boost", "scale(div(1,sum(1,product(1,geodist(lat_lng,"+restSearchReq.getElat()+","+restSearchReq.getElng()+")))),0,5)");
		}
		else if(restSearchReq.isSpatialQuery()){
			if(restSearchReq.getSearchType()==null || !restSearchReq.getSearchType().equalsIgnoreCase(rb.getString("dineout.search.type.explicit")))
				spatialQuery = "{!geofilt sfield=lat_lng pt=" + restSearchReq.getLat() + "," + restSearchReq.getLng() + " d=" + restSearchReq.getRadius() + "}";
			geoDistance = "geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+")";
			//queryParam.addParam("boost", "scale(div(1,sum(1,product(1,geodist(lat_lng,"+restSearchReq.getLat()+","+restSearchReq.getLng()+")))),0,5)");
			queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.5,geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+"))))),0.1)");
			//queryParam.addParam("boost", "product(div(5,sum(pow(2.71,product(0.005,geodist(lat_lng," + restSearchReq.getLat() +","+restSearchReq.getLng()+"))))),0.1)");
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
			sortfieldApplied = "fullfillment desc,score desc";
			//queryParam.addParam("boost","div(1,sqrt(sum(1,product(0.4,sub(sum(abs(sub("+geoDistance+",0)),abs(sub("+geoDistance+","+Double.parseDouble(restSearchReq.getRadius())/2.2+"))),sub("+Double.parseDouble(restSearchReq.getRadius())/2.2+",0))))))");				
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
			StringBuilder primaryCuisineFacetQr = new StringBuilder();
			StringBuilder secondaryCuisineFacetQr = new StringBuilder();
			String cuisineFacetQrStr = null;
			String primaryCuisineFacetQrStr = null;
			String secondaryCuisineFacetQrStr = null;
			for(String cuisine:restSearchReq.getBycuisine()){
				cuisine.replaceAll("~","/");
				cuisineFacetQr.append("cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
				primaryCuisineFacetQr.append("primary_cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
				secondaryCuisineFacetQr.append("secondary_cuisine_ft:"+"\""+cuisine+"\"").append(" OR ");
			}
			cuisineFacetQrStr = cuisineFacetQr.substring(0,cuisineFacetQr.lastIndexOf(" OR "));
			primaryCuisineFacetQrStr = primaryCuisineFacetQr.substring(0,primaryCuisineFacetQr.lastIndexOf(" OR "));
			secondaryCuisineFacetQrStr = secondaryCuisineFacetQr.substring(0,secondaryCuisineFacetQr.lastIndexOf(" OR "));
			//to give extra boost to primary cuisine when applying filter
			queryParam.addParam("bq", "("+primaryCuisineFacetQrStr+")^40000");
			queryParam.addParam("bq", "("+secondaryCuisineFacetQrStr+")^10000");	

			queryParam.addParam("fq", "{!tag=cuisine_ft_tag}("+cuisineFacetQrStr.toString()+")");
			excludeTagMap.put("cuisine", "{!ex=cuisine_ft_tag}");
		}
	}

	private void setQueryParser(QueryParam queryParam,DORestSearchRequest req, Map<String, ArrayList<String>> nerMap){
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
		queryParam.addParam("qf", rb.getString("dineout.search.qf.param"));
	}

	private void setPfParams(QueryParam queryParam) {
		queryParam.addParam("pf",rb.getString("dineout.search.pf.param"));
		queryParam.addParam("pf2",rb.getString("dineout.search.pf2.param"));
	}

}
