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
				entry.setTc_id((String)solrDocument.get("tc_id"));
				entry.setGuid((String)solrDocument.get("guid"));
				entry.setAddress((String)solrDocument.get("address"));
				entry.setScore((Float)solrDocument.get("score"));
				entry.setLocation_name(solrDocument.get("loc_name")!=null?(String)solrDocument.get("loc_name"):null);
				entry.setSuggestion(getSuggestion(dataType,solrDocument));	
				entryList.add(entry);
			}
			result.getSuggestionsMap().put(dataType, entryList);
		}
		return result;
	}
	
	private static String getSuggestion(String dataType,
			SolrDocument solrDocument) {
		return (String)solrDocument.get("est_name");
	}

}
