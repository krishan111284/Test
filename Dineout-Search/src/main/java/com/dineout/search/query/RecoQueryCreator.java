package com.dineout.search.query;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.request.GenericDOSearchRequest;
import com.dineout.search.utils.Constants;

@Component("recoQueryCreator")
public class RecoQueryCreator {

	Logger logger = Logger.getLogger(RecoQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getIdSearchQuery(DORestSearchRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("fq", "r_id:"+req.getRestId());
		queryParam.addParam("defType","edismax");
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("fl", "profile_name,costFor2,avg_rating,cuisine,tags,city_name,r_id,lat_lng");
		return queryParam;
	}

	public QueryParam getUserIdSearchQuery(DORestSearchRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("fq", "diner_id:"+req.getUserId());
		queryParam.addParam("defType","edismax");
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("fl", "restaurant_id");
		return queryParam;
	}

	public QueryParam getUserRecoQuery(List<Map<Object, Object>> docList) throws SearchException {
		QueryParam queryParam = new QueryParam();
		StringBuilder resQr = new StringBuilder();
		for (Map<Object, Object> doc :docList){
			resQr.append("r_id:\""+doc.get("restaurant_id")+"\"").append(" OR ");
		}			
		String resQrStr = null;
		resQrStr = resQr.substring(0,resQr.lastIndexOf(" OR "));
		queryParam.addParam("fq", "("+resQrStr+")");
		queryParam.addParam("defType","edismax");
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		queryParam.addParam("fl", "city_name,cuisine,primary_cuisine,secondary_cuisine,locality_name,area_name,costFor2,avg_rating,r_id,profile_name,url,score,img,n_offers,lat_lng,fullfillment,is_accept_payment,tags,offers,features_ft,booking_last_7,booking_last_90,recency,recent_days");
		return queryParam;
	}

	public QueryParam getNearByRestaurantQuery(DORestSearchRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		setQueryParser(queryParam,req);
		setNearbyFlFields(queryParam,req);
		setResponseNumLimit(queryParam,req);
		applyCityRestFilter(queryParam,req);
		applyNearbyFilter(queryParam,req);
		applyNearbyBoosts(queryParam,req);
		handleGroupsAndChain(queryParam,req);
		handleFulfillment(queryParam, req);
		return queryParam;
	}

	public QueryParam getSimilarRestaurantQuery(DORestSearchRequest req) throws SearchException {
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("q", Constants.WILD_SEARCH_QUERY);
		setQueryParser(queryParam,req);
		setSimilarFlFields(queryParam,req);
		setResponseNumLimit(queryParam,req);
		applyCityRestFilter(queryParam,req);
		applySimilarBoosts(queryParam,req);
		handleGroupsAndChain(queryParam,req);
		handleFulfillment(queryParam, req);
		return queryParam;
	}

	private void handleFulfillment(QueryParam queryParam, DORestSearchRequest req){
		String sortfieldApplied = "fullfillment desc,score desc";
		if(!StringUtils.isEmpty(req.getBysort())){
			if(Constants.SORT_OPTION_ONE.equals(req.getBysort())){
				sortfieldApplied = "score desc";
			}
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handleGroupsAndChain(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("group","true");
		queryParam.addParam("group.field","profile_name");
		queryParam.addParam("group.limit", "1");
		queryParam.addParam("fq", "-profile_name:"+"\""+req.getProfile_name()+"\"");
	}

	private void setNearbyFlFields(QueryParam queryParam, DORestSearchRequest req) {
		String fl="r_id,costFor2,avg_rating,profile_name,cuisine_ft,tags_ft,locality_name_ft,img,url,fullfillment,n_offers,score";
		String geoDistance = "geo_distance:geodist(lat_lng," + req.getLat() +","+req.getLng()+")";
		queryParam.addParam("fl", fl+","+geoDistance);
	}

	private void setSimilarFlFields(QueryParam queryParam, DORestSearchRequest req) {
		String fl="r_id,costFor2,avg_rating,profile_name,cuisine_ft,tags_ft,locality_name_ft,img,url,fullfillment,n_offers,score";
		queryParam.addParam("fl", fl);
	}

	private void applyCityRestFilter(QueryParam queryParam, DORestSearchRequest req) {
		queryParam.addParam("fq", "city_name:"+req.getBycity());
	}

	private void applyNearbyFilter(QueryParam queryParam, DORestSearchRequest req) {
		String radius = req.getRadius()!=null?req.getRadius():"5";
		String spatialQuery = "{!geofilt sfield=lat_lng pt=" + req.getLat() + "," + req.getLng() + " d="+radius+"}";
		queryParam.addParam("fq", spatialQuery);
	}

	private void applyNearbyBoosts(QueryParam queryParam, DORestSearchRequest req) {
		applyGeoBoost(queryParam,req);
		applyRatingsBoost(queryParam,req);
		applyPriceBoost(queryParam,req);
		applyCuisineBoost(queryParam,req);
		applyTagsBoost(queryParam,req);
	}

	private void applySimilarBoosts(QueryParam queryParam, DORestSearchRequest req) {
		applyRatingsBoost(queryParam,req);
		applyPriceBoost(queryParam,req);
		applyCuisineBoost(queryParam,req);
		applyTagsBoost(queryParam,req);
		applyProductReqBoost(queryParam,req);
	}

	private void applyRatingsBoost(QueryParam queryParam, DORestSearchRequest req) {
		if(req.getAvg_rating()!=null){
			float rest_rating = Float.parseFloat(req.getAvg_rating());
			float min=rest_rating-0.3f;
			float max = rest_rating+0.3f;
			queryParam.addParam("boost", "div(1,sqrt(sum(1,product(1000,sub(sum(abs(sub(avg_rating,"+min+")),abs(sub(avg_rating,"+max+"))),sub("+max+","+min+"))))))");
		}
	}

	private void applyTagsBoost(QueryParam queryParam, DORestSearchRequest req) {
		if(req.getBytags()!=null && req.getBytags().length>0){
			StringBuilder tags = new StringBuilder();
			String tagsQrStr = null;
			for(String tag:req.getBytags()){
				tags.append("tags_ft:\""+tag+"\""+"^300").append(" OR ");
			}
			tagsQrStr = tags.substring(0,tags.lastIndexOf(" OR "));
			queryParam.addParam("bq", tagsQrStr);
		}
	}

	private void applyCuisineBoost(QueryParam queryParam, DORestSearchRequest req) {
		if(req.getBycuisine()!=null && req.getBycuisine().length>0){
			StringBuilder cuisines = new StringBuilder();
			String CuisineQrStr = null;
			for(String cuisine:req.getBycuisine()){
				cuisines.append("cuisine_ft:\""+cuisine+"\""+"^100").append(" OR ");
			}
			CuisineQrStr = cuisines.substring(0,cuisines.lastIndexOf(" OR "));
			queryParam.addParam("bq", CuisineQrStr);
		}
	}

	private void applyPriceBoost(QueryParam queryParam, DORestSearchRequest req) {
		if(req.getByprice()!=null && req.getByprice().length>0){
			float costForTwo = Float.parseFloat(req.getByprice()[0]);
			float min=costForTwo-300f;
			float max = costForTwo+300f;
			queryParam.addParam("boost", "div(1,sqrt(sum(1,product(0.004,sub(sum(abs(sub(costFor2,"+min+")),abs(sub(costFor2,"+max+"))),sub("+max+","+min+"))))))");
		}
	}

	private void applyGeoBoost(QueryParam queryParam, DORestSearchRequest req) {
		String geoDistance = "geodist(lat_lng," + req.getLat() +","+req.getLng()+")";
		queryParam.addParam("bf", "recip("+geoDistance+",5,1,1)");

	}

	private void applyProductReqBoost(QueryParam queryParam, DORestSearchRequest req) {
		//queryParam.addParam("boost", "product(booking_count,0.5)");
	}

	private void setQueryParser(QueryParam queryParam,DORestSearchRequest req){
		queryParam.addParam("defType","edismax");
	}

	public void setResponseNumLimit(QueryParam queryParam, GenericDOSearchRequest req) {
		String start = !StringUtils.isEmpty(req.getStart())? req.getStart():Constants.DEFAULT_START_INDEX;
		String rows = !StringUtils.isEmpty(req.getLimit())? req.getLimit():Constants.DEFAULT_NUM_ROWS;
		queryParam.addParam("start", start);
		queryParam.addParam("rows", rows);
	}

}
