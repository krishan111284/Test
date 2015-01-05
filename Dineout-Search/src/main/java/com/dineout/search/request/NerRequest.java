package com.dineout.search.request;

import org.apache.commons.lang3.StringUtils;

public class NerRequest {
	
	private String query;
	private String city;

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public int hashCode(){
		int hash = 1;
		if(!StringUtils.isBlank(query)){
			hash = query.hashCode();
		}
		if(!StringUtils.isBlank(city)){
			hash = hash + city.hashCode();
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;
		if(!StringUtils.isBlank(((NerRequest)obj).getQuery())){
			equals = ((NerRequest)obj).getQuery().equals(this.query);
		}
		if(!StringUtils.isBlank(((NerRequest)obj).getCity())){
			equals = ((NerRequest)obj).getCity().equals(this.city);
		}
		return equals;
	}
	@Override
	public String toString(){
		return !StringUtils.isBlank(query)?(!StringUtils.isBlank(city)?query+city:query):null;
	}
}
