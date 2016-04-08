package com.dineout.search.query;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.request.GenericDOSearchRequest;
import com.dineout.search.utils.Constants;

public abstract class DOAbstractQueryCreator {

	public void initializeQueryCreator(GenericDOSearchRequest request,QueryParam queryParam, String reqFl, String fl){
		addFlParams(request,queryParam,reqFl,fl);
		addSpellCheckParams(request,queryParam);
	}

	public void addSpellCheckParams(GenericDOSearchRequest request,
			QueryParam queryParam) {
		if(!StringUtils.isEmpty(request.getSpellcheck()) && Constants.TC_SPELL_CHECK_TRUE.equals(request.getSpellcheck())){
			queryParam.addParam("spellcheck", "true");
			queryParam.addParam("spellcheck.maxCollationTries", "5");
			queryParam.addParam("spellcheck.q", request.getSearchname());
			queryParam.addParam("stopwords", "true");	
			queryParam.addParam("spellcheck.extendedResults", "true");
			queryParam.addParam("spellcheck.alternativeTermCount", "5");
			queryParam.addParam("spellcheck.collate", "true");
			queryParam.addParam("spellcheck.collateExtendedResults", "true");
			queryParam.addParam("spellcheck.maxCollations", "5");
			queryParam.addParam("spellcheck.collateParam.mm", "100%");
		}
	}

	protected void addFlParams(GenericDOSearchRequest request, QueryParam queryParam, String reqFl, String fl) {
		reqFl = StringUtils.isEmpty(reqFl) ? fl:reqFl;
		StringBuilder sb = new StringBuilder(reqFl);

		if(request.isSpatialQuery() && request.isEntitySpatialQuery()){
			String geoDistance = "geodist(lat_lng," + request.getLat() +","+request.getLng()+")";
			sb.append(",").append("geo_distance:"+geoDistance);
		}
		else if(request.isSpatialQuery()){
			String geoDistance = "geodist(lat_lng," + request.getLat() +","+request.getLng()+")";
			sb.append(",").append("geo_distance:"+geoDistance);
		}

		queryParam.addParam("fl",sb.toString());
	}

	public void setResponseNumLimit(QueryParam queryParam, GenericDOSearchRequest req) {
		String start = !StringUtils.isEmpty(req.getStart())? req.getStart():Constants.DEFAULT_START_INDEX;
		String rows = !StringUtils.isEmpty(req.getLimit())? req.getLimit():Constants.DEFAULT_NUM_ROWS;
		queryParam.addParam("start", start);
		queryParam.addParam("rows", rows);
	}
	public void setHlParams(QueryParam queryParam,String hlfl,String query){
		if(!StringUtils.isBlank(hlfl)){
			queryParam.addParam("hl.q", query);
			queryParam.addParam("hl", "true");
			queryParam.addParam("hl.fl", hlfl);
			queryParam.addParam("hl.mergeContiguous","true");
			queryParam.addParam("hl.preserveMulti", "true");
			queryParam.addParam("hl.snippets","10");
		}
	}

}
