package com.dineout.search.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import com.dineout.search.response.DOAutoCompleteSearchResult;
import com.dineout.search.response.DOAutoCompleteSuggestionEntry;
import com.dineout.search.response.DOLocationSearchResponseEntry;
import com.dineout.search.response.DOLocationSearchResult;

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
				entry.setBookingCount(solrDocument.get("booking_count")!=null?(Float)solrDocument.get("booking_count"):0);
				entry.setFulfillment((String)solrDocument.get("fullfillment"));
				entry.setSuggestion(getSuggestion(dataType,solrDocument));	
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
				getLatLong(solrDocument.get("lat_lng")!=null?(String)solrDocument.get("lat_lng"):null,entry);
				entryList.add(entry);
			}
			result.getSuggestionsMap().put(dataType.toUpperCase(), entryList);
		}
		return result;
	}

	private static void getLatLong(String latlng, DOLocationSearchResponseEntry entry) {
		if(latlng!=null)
		{
			String[] lat_lng = latlng.split(",");			
			entry.setLat(lat_lng[0]!=null?lat_lng[0]:null);
			entry.setLng(lat_lng[1]!=null?lat_lng[1]:null);
		}
	}
}
