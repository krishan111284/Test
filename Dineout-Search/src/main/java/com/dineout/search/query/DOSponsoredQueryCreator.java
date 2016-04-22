package com.dineout.search.query;

import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dineout.search.exception.SearchException;
import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.utils.Constants;

@Component("sponsoredQueryCreator")
public class DOSponsoredQueryCreator extends DOAbstractQueryCreator {
	Logger logger = Logger.getLogger(DOSponsoredQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public QueryParam getLocationFeaturedQuery(DORestSearchRequest req) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		initializeQueryCreator(req, queryParam, req.getEstfl(),rb.getString("dineout.search.fl"));
		queryString = !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, req);
		applyCityFilter(queryParam, req);
		handleLocationFilters(queryParam, req);
		handleSortingRequest(queryParam, req);
		return queryParam;
	}

	public QueryParam getAreaFeaturedQuery(DORestSearchRequest req) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		initializeQueryCreator(req, queryParam, req.getEstfl(),rb.getString("dineout.search.fl"));
		queryString = !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, req);
		applyCityFilter(queryParam, req);
		handleAreaFilters(queryParam, req);
		removeLocationFilters(queryParam, req);
		handleSortingRequest(queryParam,req);
		return queryParam;
	}

	public QueryParam getCityFeaturedQuery(DORestSearchRequest req) throws SearchException {
		String queryString = null;
		QueryParam queryParam = new QueryParam();
		initializeQueryCreator(req, queryParam, req.getEstfl(),rb.getString("dineout.search.fl"));
		queryString = !StringUtils.isBlank(req.getSearchname()) ? req.getSearchname():Constants.WILD_SEARCH_QUERY;
		queryParam.addParam("q", queryString);
		setQueryParser(queryParam, req);
		applyCityFilter(queryParam, req);
		remmoveAreaFilters(queryParam, req);
		handleSortingRequest(queryParam,req);
		return queryParam;
	}

	private void applyCityFilter(QueryParam queryParam,DORestSearchRequest req) throws SearchException{
		if(!StringUtils.isEmpty(req.getBycity()))
			queryParam.addParam("fq", "city_name:\""+req.getBycity()+"\"");
	}

	private void handleSortingRequest(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		String bySort = restSearchReq.getBysort();
		String sortfieldApplied = "";
		if(!StringUtils.isEmpty(bySort)){
			if(Constants.SORT_OPTION_ONE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,rank asc,score desc";
			}
			if(Constants.SORT_OPTION_TWO.equals(bySort)){
				sortfieldApplied = "fullfillment desc,rank desc,score desc";
			}
			if(Constants.SORT_OPTION_THREE.equals(bySort)){
				sortfieldApplied = "fullfillment desc,rank asc, avg_rating desc";
			}
		}else{
			sortfieldApplied = "fullfillment desc,rank asc,score desc";
		}
		queryParam.addParam("sort", sortfieldApplied);
	}

	private void handleAreaFilters(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		if(restSearchReq.getByarea()!=null && restSearchReq.getByarea().length>0){
			StringBuilder zoneFacetQr = new StringBuilder();
			String zoneFacetQrStr = null;
			for(String zone:restSearchReq.getByarea()){
				zoneFacetQr.append("area_name_ft:\""+zone+"\"").append(" OR ");
			}
			zoneFacetQrStr = zoneFacetQr.substring(0,zoneFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "("+zoneFacetQrStr+")");
		}
	}

	private void handleLocationFilters(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		if(restSearchReq.getBylocation()!=null && restSearchReq.getBylocation().length>0){
			StringBuilder locationFacetQr = new StringBuilder();
			String locationFacetQrStr=null;
			for(String location:restSearchReq.getBylocation()){
				locationFacetQr.append("locality_name_ft:\""+location+"\"").append(" OR ");
			}
			locationFacetQrStr = locationFacetQr.substring(0,locationFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "("+locationFacetQrStr.toString()+")");
		}
	}

	private void removeLocationFilters(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		if(restSearchReq.getBylocation()!=null && restSearchReq.getBylocation().length>0){
			StringBuilder locationFacetQr = new StringBuilder();
			String locationFacetQrStr=null;
			for(String location:restSearchReq.getBylocation()){
				locationFacetQr.append("-locality_name_ft:\""+location+"\"").append(" OR ");
			}
			locationFacetQrStr = locationFacetQr.substring(0,locationFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "("+locationFacetQrStr.toString()+")");
		}
	}

	private void remmoveAreaFilters(QueryParam queryParam, DORestSearchRequest restSearchReq) {
		if(restSearchReq.getByarea()!=null && restSearchReq.getByarea().length>0){
			StringBuilder zoneFacetQr = new StringBuilder();
			String zoneFacetQrStr = null;
			for(String zone:restSearchReq.getByarea()){
				zoneFacetQr.append("-area_name_ft:\""+zone+"\"").append(" OR ");
			}
			zoneFacetQrStr = zoneFacetQr.substring(0,zoneFacetQr.lastIndexOf(" OR "));
			queryParam.addParam("fq", "("+zoneFacetQrStr+")");
		}
	}

	private void setQueryParser(QueryParam queryParam,DORestSearchRequest req){
		queryParam.addParam("defType","edismax");
		queryParam.addParam("mm", "100%");
		if(!StringUtils.isEmpty(req.getSearchname())){
			setQfParams(queryParam);
		}
		setResponseNumLimit(queryParam,req);
	}

	private void setQfParams(QueryParam queryParam) {
		queryParam.addParam("qf", rb.getString("dineout.search.qf.param"));
	}

	/*private void setPfParams(QueryParam queryParam) {
		queryParam.addParam("pf",rb.getString("dineout.search.pf.param"));
		queryParam.addParam("pf2",rb.getString("dineout.search.pf2.param"));
	}*/

}
