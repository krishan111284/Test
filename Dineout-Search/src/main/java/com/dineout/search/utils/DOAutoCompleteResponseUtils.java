package com.dineout.search.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import com.dineout.search.response.DOAutoCompleteSearchResult;
import com.dineout.search.response.DOAutoCompleteSuggestionEntry;
import com.dineout.search.response.DOLocationSearchResponseEntry;
import com.dineout.search.response.DOLocationSearchResult;
import com.dineout.search.response.ILocationResponseEntity;

public class DOAutoCompleteResponseUtils {

	public static DOAutoCompleteSearchResult processGroupQueryResponse(QueryResponse qRes) {
		DOAutoCompleteSearchResult result = new DOAutoCompleteSearchResult();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			String dataType = group.getGroupValue();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			List<DOAutoCompleteSuggestionEntry> entryList = new ArrayList<DOAutoCompleteSuggestionEntry>();
			while(iter.hasNext()){
				SolrDocument solrDocument = iter.next();
				DOAutoCompleteSuggestionEntry entry = new DOAutoCompleteSuggestionEntry();
				entry.setUid((String)solrDocument.get("uid"));
				entry.setR_id((String)solrDocument.get("r_id"));
				entry.setGuid((String)solrDocument.get("guid"));
				entry.setProfile_name((String)solrDocument.get("profile_name"));
				entry.setCuisine_name((String)solrDocument.get("cuisine_name"));
				entry.setScore((Float)solrDocument.get("score"));
				entry.setTag_name((String)solrDocument.get("tag_name"));
				entry.setLocation_name(solrDocument.get("location_name")!=null?(String)solrDocument.get("location_name"):null);
				entry.setArea_name(solrDocument.get("area_name")!=null?(String)solrDocument.get("area_name"):null);
				entry.setBookingCount(solrDocument.get("booking_count")!=null?((Float)solrDocument.get("booking_count")).toString():null);
				entry.setFulfillment((String)solrDocument.get("fullfillment"));
				entry.setTg_id((String)solrDocument.get("tg_id"));
				entry.setTicket_name((String)solrDocument.get("ticket_name"));
				entry.setTl_id((String)solrDocument.get("tl_id"));
				entry.setDc_name((String)solrDocument.get("dc_name"));
				entry.setFrom_date((String)solrDocument.get("from_date"));
				entry.setTo_date((String)solrDocument.get("to_date"));

				//entry.setSuggestion(getSuggestion(dataType,solrDocument));	
				entry.setSuggestion((String)solrDocument.get("suggestion"));
				getLatLong(solrDocument.get("lat_lng")!=null?(String)solrDocument.get("lat_lng"):null,entry);
				entryList.add(entry);
			}
			result.getSuggestionsMap().put(dataType, entryList);
		}
		return result;
	}

	private static String getSuggestion(String dataType,
			SolrDocument solrDocument) {
		String entity_name = null;
		if(Constants.AUTOCOMPLETION_DATA_TYPE_RESTAURANT.equals(dataType))
			entity_name = (String)solrDocument.get("profile_location_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_LOCALITY.equals(dataType))
			entity_name = (String)solrDocument.get("loc_area_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_AREA.equals(dataType))
			entity_name = (String)solrDocument.get("area_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_CUISINE.equals(dataType))
			entity_name = (String)solrDocument.get("cuisine_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_TAGS.equals(dataType))
			entity_name = (String)solrDocument.get("tag_name");

		return entity_name;
	}

	public static DOLocationSearchResult processMultipleResponse(DOLocationSearchResult areaCityResult, DOLocationSearchResult locationResult){
		DOLocationSearchResult result = locationResult;
		if(!areaCityResult.getSuggestionsMap().isEmpty())
		{
			Map<String, List<DOLocationSearchResponseEntry>> areaCityMap = areaCityResult.getSuggestionsMap();
			Map<String, List<DOLocationSearchResponseEntry>> locationMap = locationResult.getSuggestionsMap();
			Iterator<?> areaCityResultIterator = areaCityResult.getSuggestionsMap().entrySet().iterator();
			while (areaCityResultIterator.hasNext()) {
				Map.Entry pair = (Map.Entry)areaCityResultIterator.next();
				if(locationMap.get(pair.getKey()) != null){
					List<DOLocationSearchResponseEntry> areaCityList = areaCityMap.get(pair.getKey());
					List<DOLocationSearchResponseEntry> locationList = locationMap.get(pair.getKey());
					Set<DOLocationSearchResponseEntry> setboth = new LinkedHashSet<DOLocationSearchResponseEntry>(areaCityList);
					setboth.addAll(locationList);
					areaCityList.clear();
					areaCityList.addAll(setboth);
					//areaCityMap.get(pair.getKey()).addAll(locationMap.get(pair.getKey()));
				}
			}
			result = areaCityResult;
		}
		return result;
	}

	public static DOLocationSearchResult processLocationGroupQueryResponse(QueryResponse qRes) {
		DOLocationSearchResult result = new DOLocationSearchResult();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			String dataType = group.getGroupValue();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			List<DOLocationSearchResponseEntry> entryList = new ArrayList<DOLocationSearchResponseEntry>();
			while(iter.hasNext()){
				SolrDocument solrDocument = iter.next();
				DOLocationSearchResponseEntry entry = new DOLocationSearchResponseEntry();
				entry.setUid((String)solrDocument.get("uid"));
				entry.setLocation_name(solrDocument.get("location_name")!=null?(String)solrDocument.get("location_name"):null);
				entry.setArea_name(solrDocument.get("area_name")!=null?(String)solrDocument.get("area_name"):null);
				entry.setCity_name(solrDocument.get("city_name")!=null?(String)solrDocument.get("city_name"):null);
				entry.setSuggestion(solrDocument.get("suggestion")!=null?(String)solrDocument.get("suggestion"):null);	
				entry.setCity(solrDocument.get("city")!=null?(String)solrDocument.get("city"):null);
				entry.setCity_id(solrDocument.get("c_id")!=null?(String)solrDocument.get("c_id"):null);	
				entry.setEntity_type(solrDocument.get("data_type")!=null?(String)solrDocument.get("data_type"):null);
				getLatLong(solrDocument.get("lat_lng")!=null?(String)solrDocument.get("lat_lng"):null,entry);
				entryList.add(entry);
			}
			result.getSuggestionsMap().put(dataType.toUpperCase(), entryList);
		}
		return result;
	}

	private static void getLatLong(String latlng, ILocationResponseEntity entry) {
		if(latlng!=null)
		{
			String[] lat_lng = latlng.split(",");			
			entry.setLat(lat_lng[0]!=null?lat_lng[0]:null);
			entry.setLng(lat_lng[1]!=null?lat_lng[1]:null);
		}
	}
}
