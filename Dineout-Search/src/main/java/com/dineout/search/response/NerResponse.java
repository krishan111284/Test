package com.dineout.search.response;

import java.util.HashMap;
import java.util.Map;

public class NerResponse {
	
	private Map<String, String> entityTypeValMap = new HashMap<String, String>();

	public Map<String, String> getEntityTypeValMap() {
		return entityTypeValMap;
	}

	public void setEntityTypeValMap(Map<String, String> entityTypeValMap) {
		this.entityTypeValMap = entityTypeValMap;
	}

}
