package com.dineout.search.service;

import java.util.ArrayList;
import java.util.Map;

import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.request.NerRequest;

public interface NerService {
	public Map<String,ArrayList<String>> extactNamedEntities(NerRequest req);
	public String getArea(DORestSearchRequest request);
}
