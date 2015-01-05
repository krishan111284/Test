package com.dineout.search.response;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;

public class DOFacet {
	
	private Map<String, Integer> facetQueries;
	private List<FacetField> facetDates;
	private List<RangeFacet> facetRanges;
	Map<Object, Object> facetMap;
	public Map<String, Integer> getFacetQueries() {
		return facetQueries;
	}
	public void setFacetQueries(Map<String, Integer> facetQueries) {
		this.facetQueries = facetQueries;
	}
	public List<FacetField> getFacetDates() {
		return facetDates;
	}
	public void setFacetDates(List<FacetField> facetDates) {
		this.facetDates = facetDates;
	}
	public List<RangeFacet> getFacetRanges() {
		return facetRanges;
	}
	public void setFacetRanges(List<RangeFacet> facetRanges) {
		this.facetRanges = facetRanges;
	}
	public Map<Object, Object> getFacetMap() {
		return facetMap;
	}
	public void setFacetMap(Map<Object, Object> facetMap) {
		this.facetMap = facetMap;
	}

}
