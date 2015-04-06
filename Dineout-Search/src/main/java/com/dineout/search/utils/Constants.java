package com.dineout.search.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.http.MediaType;

public class Constants {
	public static final MediaType JAVA_SCRIPT_MEDIA_TYPE = MediaType.valueOf("application/javascript");
	public static final MediaType JSON_MEDIA_TYPE = MediaType.valueOf("application/json;charset=UTF-8");      

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final String SPECIAL_CHAR_REGEX = "\\band\\b|\\bor\\b|[+\\-,]";
	public static final String DEFAULT_FACET_LIMIT = "1000";
	public static final String DEFAULT_FACET_MIN_COUNT = "1";
	public static final String DEFAULT_START_INDEX = "0";
	public static final String DEFAULT_NUM_ROWS = "10";
	public static final String WILD_SEARCH_QUERY="*:*";
	public static final String FIELD_LIST_ALL="city_name,cuisine,primary_cuisine,secondary_cuisine,primary_family,secondary_family,locality_name,event_title,event_desc,rank,area_name,costFor2,locality_alias,avg_rating,r_id,profile_name,url,landmark,booking_count,address,rest_alias,score,img,n_offers,lat_lng,fullfillment,rest_alias";


	/*****************Response Types *************************/


	public static final String TC_SPELL_CHECK_TRUE = "true";
	public static final String TC_AUTO_SUGGEST_TRUE = "true";
	public static final String TC_ID_SEARCH_TRUE = "true";
	public static final String TC_FACET_SORT_INDEX_TRUE = "true";
	public static final String IS_HL_TRUE = "true";

	/*****************************Autocompletion data types*****************************************/
	public static final String AUTOCOMPLETION_DATA_TYPE_RESTAURANT = "Restaurant";
	public static final String AUTOCOMPLETION_DATA_TYPE_NIGHTLIFE = "Nightlife";
	public static final String AUTOCOMPLETION_DATA_TYPE_EVENT = "Event";
	public static final String AUTOCOMPLETION_DATA_TYPE_MOVIE = "Movie";
	public static final String AUTOCOMPLETION_DATA_TYPE_THEATER = "Theater";
	public static final String AUTOCOMPLETION_DATA_TYPE_CUISINE = "Cuisine";
	public static final String AUTOCOMPLETION_DATA_TYPE_LOCALITY = "Locality";
	public static final String AUTOCOMPLETION_DATA_TYPE_QUERY = "Query";
	public static final String AUTOCOMPLETION_DATA_TYPE_TAGS = "Tags";
	public static final String AUTOCOMPLETION_DATA_TYPE_AREA = "Area";

	/*****************************Group types*****************************************/
	public static final String GROUP_TRUE = "true";

	public static boolean IS_UNIFIED_MEMCACHE_ENABLED = false;
	public static boolean IS_AUTO_MEMCACHE_ENABLED = false;
	public static boolean IS_EST_MEMCACHE_ENABLED = false;
	public static boolean IS_EVENT_MEMCACHE_ENABLED = false;
	public static boolean IS_MOVIES_MEMCACHE_ENABLED = false;
	public static boolean IS_THEATER_MEMCACHE_ENABLED = false;
	public static boolean IS_REVIEWS_MEMCACHE_ENABLED = false;
	//public static boolean IS_SPONSORED_MEMCACHE_ENABLED = false;

	public static int MEMCACHE_EXPIRY_TIME = 43200;

	public static String RESPONSE_STATUS_OK = "1";
	public static String RESPONSE_STATUS_ERROR = "0";


	public static String ATTRIBUTE_TYPE_ERROR = "error";
	public static String ERROR_FORWARD_URL = "/unifiedsearch/returnerror";


	public static final String SOLR_SERVER_EMBEDDED = "embedded";
	public static final String SOLR_SERVER_HTTP = "http";
	public static final long CYCLIC_BARRIER_WAIT_TIMEOUT = 1;
	public static final String TC_REVIEWS_RESPONSE_DETAIL = "true";

	public static final String RELEVANCE_OPTION_POPULARITY = "POPULARITY";

	public static final String NER_ZONE_KEY = "Zone";
	public static final String NER_CUISINE_KEY = "Cuisine";
	public static final String NER_CUISINE_FAMILY_KEY = "Family";
	public static final String NER_LOC_KEY = "Locality";
	public static final String TC_NER_TRUE = "true";
	public static final long NER_QUERY_CACHE_ROWS = 1000;
	public static final String IS_FACET_DISABLED = "true";
	public static final String SEPERATOR = ",";
	public static final String LAT_LNG_KEY = "LAT_LNG";
	public static final String PROCESSED_QUERY = "PROCESSED_QUERY";
	public static final String IS_NEARBY_ENABLED = "true";
	public static final String NER_FEATURE_KEY = "Feature";

	/******************Sort options**********************************/
	public static final String SORT_OPTION_ONE= "1";
	public static final String SORT_OPTION_TWO = "2";
	public static final String SORT_OPTION_THREE = "3";
	public static final String SORT_OPTION_FOUR = "4";
	public static final String SORT_OPTION_FIVE = "5";
	public static final String SORT_OPTION_SIX = "6";
	public static final String SORT_OPTION_SEVEN = "7";
	public static final String SORT_OPTION_EIGHT = "8";
	public static final String SORT_OPTION_NINE = "9";
	public static final String RESPONSE_TYPE_REST = "RESTAURANT";


}
