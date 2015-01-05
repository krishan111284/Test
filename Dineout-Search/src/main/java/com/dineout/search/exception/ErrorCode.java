package com.dineout.search.exception;

public class ErrorCode {
	
	public static final String SOLR_ERROR_CODE = "101";
	public static final String URL_DECODE_ERROR = "306::URL DECODE ERROR";
	

	public static final String INVALID_PARAMETER_VALUE = "301::INVALID_PARAMETER_VALUE";
	public static final String INVALID_DATE_FORMAT = "302::INVALID_DATE_FORMAT";
	public static final String MISSING_PARAMETER_ERROR = "303::MISSING PARAMETER";
	public static final String INVALID_START_INDEX_VALUE = "304::INVALID_START_INDEX_VALUE";
	public static final String INVALID_MAX_RESULT_VALUE = "305::INVALID_MAX_RESULT_VALUE";
	public static final String MISSING_CITY_LAT_LNG = "306::MISSING_CITY_LAT_LNG";
	public static final String SPATIAL_WITHOUT_NEARBY_RADIUS = "313::SPATIAL_WITHOUT_NEARBY_RADIUS";
}
