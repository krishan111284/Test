package com.dineout.search.request;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.utils.Constants;


public abstract class GenericDOSearchRequest {
	
	private String searchname;
	private String bycity;
	private String bysort;
	/**Lat-long**/
	private String lat;
	private String lng;
	private String radius;
	private String facetlimit;
	private String facetsorttype;
	private String facetmincount;
	private String start;
	private String limit;
	private String spellcheck;
	
	/**Flags**/
	private boolean isSearchExecuted;
	private boolean isSpatialQuery;
	private boolean isSpellcheckApplied;

	public String getSearchname() {
		return searchname;
	}
	public void setSearchname(String searchname) {
		//TO AVOID UN - INTENTIONAL BOOLEAN QUERY WITH EDISMAX
        Pattern specialCharPatternRegex = Pattern.compile(Constants.SPECIAL_CHAR_REGEX);
        this.searchname = !StringUtils.isEmpty(searchname)?specialCharPatternRegex.matcher(searchname).replaceAll(" "):searchname;

	}
	public String getBycity() {
		return bycity;
	}
	public void setBycity(String bycity) {
		this.bycity = bycity;
	}
	public String getBysort() {
		return bysort;
	}
	public void setBysort(String bysort) {
		this.bysort = bysort;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getFacetlimit() {
		return facetlimit;
	}
	public void setFacetlimit(String facetlimit) {
		this.facetlimit = facetlimit;
	}
	public String getFacetsorttype() {
		return facetsorttype;
	}
	public void setFacetsorttype(String facetsorttype) {
		this.facetsorttype = facetsorttype;
	}
	public String getFacetmincount() {
		return facetmincount;
	}
	public void setFacetmincount(String facetmincount) {
		this.facetmincount = facetmincount;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getSpellcheck() {
		return spellcheck;
	}
	public void setSpellcheck(String spellcheck) {
		this.spellcheck = spellcheck;
	}
	public boolean isSearchExecuted() {
		return isSearchExecuted;
	}
	public void setSearchExecuted(boolean isSearchExecuted) {
		this.isSearchExecuted = isSearchExecuted;
	}
	public boolean isSpatialQuery() {
		return isSpatialQuery;
	}
	public void setSpatialQuery(boolean isSpatialQuery) {
		this.isSpatialQuery = isSpatialQuery;
	}
	public boolean isSpellcheckApplied() {
		return isSpellcheckApplied;
	}
	public void setSpellcheckApplied(boolean isSpellcheckApplied) {
		this.isSpellcheckApplied = isSpellcheckApplied;
	}
	
	
}
