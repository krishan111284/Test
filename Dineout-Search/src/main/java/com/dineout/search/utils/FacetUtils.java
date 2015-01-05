package com.dineout.search.utils;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component("facetUtils")
public class FacetUtils {

	private ResourceBundle rb = ResourceBundle.getBundle("search");

	private Set<String> restfacets;
	public FacetUtils() {
		intializeRestFacets();
	}
	private void intializeRestFacets() {
		 String facetList=rb.getString("dineout.search.facets");
		 restfacets = new HashSet<String>();
			String[] facets = facetList.split(Constants.SEPERATOR);
			for(String facet:facets){
				restfacets.add(facet);
			}
	}

	public Set<String> getDefaultRestFacets(){
		return restfacets;
	}
}
