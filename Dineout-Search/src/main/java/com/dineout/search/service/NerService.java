package com.dineout.search.service;

import java.util.Map;

import com.dineout.search.request.NerRequest;

public interface NerService {
	public Map<String,String> extactNamedEntities(NerRequest req);
}
