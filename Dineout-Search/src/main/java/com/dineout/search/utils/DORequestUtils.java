package com.dineout.search.utils;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.request.GenericDOSearchRequest;

public class DORequestUtils {

	public static boolean isSpatial(GenericDOSearchRequest request){
		return  (!StringUtils.isEmpty(request.getLat())
				&& !StringUtils.isEmpty(request.getLng())
				&& !StringUtils.isEmpty(request.getRadius()));
		
	}
}
