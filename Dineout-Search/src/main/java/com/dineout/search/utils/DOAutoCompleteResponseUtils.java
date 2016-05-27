package com.dineout.search.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import com.dineout.search.response.DOAutoCompleteSearchResult;
import com.dineout.search.response.DOLocationSearchResponseEntry;
import com.dineout.search.response.DOLocationSearchResult;
import com.dineout.search.response.ILocationResponseEntity;

public class DOAutoCompleteResponseUtils {

	public static DOAutoCompleteSearchResult processGroupQueryResponse(QueryResponse qRes) {
		DOAutoCompleteSearchResult result = new DOAutoCompleteSearchResult();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
			String dataType = group.getGroupValue();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			while(iter.hasNext()){
				Map<Object, Object> doc = DOResponseUtils.getDODoc(iter.next());
				docList.add(doc);
			}
			result.getSuggestionsMap().put(dataType, docList);
		}
		return result;
	}
	public static Map<Object, Object> processRestGroupQueryResponse(QueryResponse qRes) {
		int docSum = 0;
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		Map<Object, Object> docMap = new LinkedHashMap<Object, Object>();
		List<Map<Object, Object>>listMap = new ArrayList<Map<Object,Object>>();
		docMap.put("groupMatches", groupCommand.getValues().size());
		List<Group> groups = groupCommand.getValues();
		for(Group group:groups){
			Map<Object, Object> groupMap = new LinkedHashMap<Object, Object>();
			List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			while(iter.hasNext()){
				Map<Object, Object> doc = DOResponseUtils.getDODoc(iter.next());
				docList.add(doc);
			}
			int numFound = (int) group.getResult().getNumFound();
			docSum = docSum + numFound;
			groupMap.put("numFound", numFound);
			groupMap.put("groupName", group.getGroupValue());
			groupMap.put("docs", docList);
			listMap.add(groupMap);

		}
		docMap.put("docsFound", docSum);
		docMap.put("groups", listMap);
		return docMap;
	}

	public static DOLocationSearchResult processMultipleResponse(DOLocationSearchResult areaCityResult, DOLocationSearchResult locationResult){
		DOLocationSearchResult result = locationResult;
		if(!areaCityResult.getSuggestionsMap().isEmpty())
		{
			Map<String, List<DOLocationSearchResponseEntry>> areaCityMap = areaCityResult.getSuggestionsMap();
			Map<String, List<DOLocationSearchResponseEntry>> locationMap = locationResult.getSuggestionsMap();
			Iterator<?> areaCityResultIterator = areaCityResult.getSuggestionsMap().entrySet().iterator();
			while (areaCityResultIterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry)areaCityResultIterator.next();
				if(locationMap.get(pair.getKey()) != null){
					List<DOLocationSearchResponseEntry> areaCityList = areaCityMap.get(pair.getKey());
					List<DOLocationSearchResponseEntry> locationList = locationMap.get(pair.getKey());
					Set<DOLocationSearchResponseEntry> setboth = new LinkedHashSet<DOLocationSearchResponseEntry>(areaCityList);
					setboth.addAll(locationList);
					areaCityList.clear();
					areaCityList.addAll(setboth);
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
