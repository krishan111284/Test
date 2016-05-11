package com.dineout.search.utils;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component("facetUtils")
public class FacetUtils {

	private ResourceBundle rb = ResourceBundle.getBundle("search");

	private Set<String> restfacets;
	private Set<String> dealsfacets;
	public FacetUtils() {
		intializeRestFacets();
		intializeDealsFacets();
	}
	private void intializeRestFacets() {
		String facetList=rb.getString("dineout.search.facets");
		restfacets = new HashSet<String>();
		String[] facets = facetList.split(Constants.SEPERATOR);
		for(String facet:facets){
			restfacets.add(facet);
		}
	}
	private void intializeDealsFacets() {
		String facetList=rb.getString("dineout.deals.facets");
		dealsfacets = new HashSet<String>();
		String[] facets = facetList.split(Constants.SEPERATOR);
		for(String facet:facets){
			dealsfacets.add(facet);
		}
	}

	public Set<String> getDefaultRestFacets(){
		return restfacets;
	}

	public Set<String> getDefaultDealsFacets(){
		return dealsfacets;
	}
}
