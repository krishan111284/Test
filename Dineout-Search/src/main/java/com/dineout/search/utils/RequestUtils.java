package com.dineout.search.utils;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.request.GenericTCSearchRequest;

public class RequestUtils {

	public static boolean isSpatial(GenericTCSearchRequest request){
		return  (!StringUtils.isEmpty(request.getLat())
				&& !StringUtils.isEmpty(request.getLng())
				&& !StringUtils.isEmpty(request.getRadius()));
		
	}
}
