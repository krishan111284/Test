package com.dineout.search.utils;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.request.DORestSearchRequest;
import com.dineout.search.request.GenericDOSearchRequest;

public class DORequestUtils {

	public static boolean isSpatial(GenericDOSearchRequest request){
		return  (!StringUtils.isEmpty(request.getLat())
					&& !StringUtils.isEmpty(request.getLng())
					&& !StringUtils.isEmpty(request.getRadius()));

	}
	public static boolean isESpatial(DORestSearchRequest request){
		return  (!StringUtils.isEmpty(request.getElat())
						&& !StringUtils.isEmpty(request.getElng())
						&& !StringUtils.isEmpty(request.getRadius()));

	}
}
