package com.dineout.search.utils;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component("facetUtils")
public class FacetUtils {

	private ResourceBundle rb = ResourceBundle.getBundle("search");

	private Set<String> estfacets;
	public FacetUtils() {
		intializeRestFacets();
	}
	private void intializeRestFacets() {
		 String facetList=rb.getString("dineout.search.facets");
		 estfacets = new HashSet<String>();
			String[] facets = facetList.split(Constants.SEPERATOR);
			for(String facet:facets){
				estfacets.add(facet);
			}
	}

	public Set<String> getDefaultRestFacets(){
		return estfacets;
	}
}
