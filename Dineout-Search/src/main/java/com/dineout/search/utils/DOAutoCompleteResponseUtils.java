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
				entry.setGuid((String)solrDocument.get("guid"));
				entry.setProfile_name((String)solrDocument.get("profile_name"));
				entry.setCuisine_name((String)solrDocument.get("cuisine_name"));
				entry.setScore((Float)solrDocument.get("score"));
				entry.setTag_name((String)solrDocument.get("tag_name"));
				entry.setLocation_name(solrDocument.get("location_name")!=null?(String)solrDocument.get("location_name"):null);
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
				entity_name = (String)solrDocument.get("profile_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_LOCALITY.equals(dataType))
			entity_name = (String)solrDocument.get("location_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_CUISINE.equals(dataType))
			entity_name = (String)solrDocument.get("cuisine_name");
		if(Constants.AUTOCOMPLETION_DATA_TYPE_TAGS.equals(dataType))
			entity_name = (String)solrDocument.get("tag_name");
		
		return entity_name;
	}

}
