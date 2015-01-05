package com.dineout.search.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;

import com.dineout.search.response.Facet;
import com.dineout.search.response.Highlight;
import com.dineout.search.response.SearchResult;
import com.dineout.search.response.SpellCheck;

public class ResponseUtils {

	public static SearchResult processQueryResponse(QueryResponse qres, String domain, boolean isSpellcheckApplied) {
		SearchResult result = new SearchResult();
		result.setDomain(domain);
		result.setMatches((Long)qres.getResults().getNumFound());
		result.setDocs(getTCDocList(qres));
		result.setFacet(getFacetInfo(qres));
		result.setHighlight(getHlInfo(qres));
		result.setSpellCheck(getSpellCheckInfo(qres,isSpellcheckApplied));
		return result;
	}

	private static Highlight getHlInfo(QueryResponse qres) {
		Highlight highlight = new Highlight();
		if(qres.getHighlighting()!=null){
			Map<String,Object> highlights = new LinkedHashMap<String, Object>();
			Map<String, Map<String, List<String>>> hlMap = qres.getHighlighting();
			Iterator<String> iter = hlMap.keySet().iterator();
			while(iter.hasNext()){
				String highlightDoc = iter.next();
				highlights.put(highlightDoc, hlMap.get(highlightDoc));
			}
			highlight.setHighlights(highlights);
		}
		return highlight;
	}

	public static Facet getFacetInfo(QueryResponse qres) {
		Facet facet = new Facet();
		if(qres.getFacetQuery()!=null) facet.setFacetQueries(qres.getFacetQuery());
		if(qres.getFacetDates()!=null) facet.setFacetDates(qres.getFacetDates());
		if(qres.getFacetRanges()!=null) facet.setFacetRanges(qres.getFacetRanges());

		if(qres.getFacetFields()!=null && qres.getFacetFields().size()>0){
			Iterator<FacetField> facetFieldsIter = qres.getFacetFields().iterator();
			Map<Object, Object> facetMap = new LinkedHashMap<Object, Object>();

			while(facetFieldsIter.hasNext()){
				FacetField facetField = facetFieldsIter.next();
				String facetFieldName = facetField.getName();
				Map<String, Long> facetFieldMap = new LinkedHashMap<String, Long>();
				List<FacetField.Count> facetEntries = facetField.getValues();
				for (FacetField.Count fcount : facetEntries) {
					facetFieldMap.put(fcount.getName(), fcount.getCount());
				}
				facetMap.put(facetFieldName, facetFieldMap);
			}
			facet.setFacetMap(facetMap);
		}

		return facet;
	}

	public static List<Map<Object, Object>> getTCDocList(QueryResponse qres) {
		List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
		Iterator<SolrDocument> iter = qres.getResults().iterator();
		while(iter.hasNext()){
			SolrDocument solrDoc = iter.next();
			Map<Object, Object> fieldValMap = new HashMap<Object, Object>();
			Iterator<String>fieldIterator = solrDoc.keySet().iterator();
			while(fieldIterator.hasNext()){
				String fieldName = fieldIterator.next();
				fieldValMap.put(fieldName, solrDoc.get(fieldName));
			}
			docList.add(fieldValMap);
		}
		return docList;
	}

	private static SpellCheck getSpellCheckInfo(QueryResponse qres, boolean isSpellcheckApplied) {
		SpellCheck spellCheck = new SpellCheck();
		if(isSpellcheckApplied){
			SpellCheckResponse scRes =  qres.getSpellCheckResponse();
			if(scRes!=null){
				Map<String,Object> suggestions = new HashMap<String, Object>();
				suggestions.put("collation",scRes.getCollatedResults());
				suggestions.put("words", scRes.getSuggestionMap());
				suggestions.put("correctlySpelled", scRes.isCorrectlySpelled());
				spellCheck.setSuggestions(suggestions);
			}
		}
		return spellCheck;
	}


	private static Map<Object, Object> getDODoc(SolrDocument solrDoc) {
		Map<Object, Object> tcDoc = new HashMap<Object, Object>();
		Iterator<String>fieldIterator = solrDoc.keySet().iterator();
		while(fieldIterator.hasNext()){
			String fieldName = fieldIterator.next();
			tcDoc.put(fieldName, solrDoc.get(fieldName));
		}
		return tcDoc;
	}

	public static SearchResult processGroupQueryResponse(QueryResponse qRes,
			String domain,boolean isSpellcheckApplied) {
		SearchResult result = new SearchResult();
		result.setDomain(domain);
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		setMatchesForGroupQuery(result, groupCommand, domain);
		List<Group> groups =groupCommand.getValues();
		List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
		for(Group group:groups){
			Long numFound = group.getResult().getNumFound();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			Map<Object, Object> doc = getDODoc(iter.next());
			doc.put("group_matches", numFound);
			docList.add(doc);
		}
		result.setDocs(docList);
		result.setFacet(getFacetInfo(qRes));
		result.setSpellCheck(getSpellCheckInfo(qRes,isSpellcheckApplied));
		result.setHighlight(getHlInfo(qRes));
		return result;
	}

	private static void setMatchesForGroupQuery(SearchResult result, GroupCommand groupCommand, String domain ){
		int matches = groupCommand.getMatches();
		int numGroups = groupCommand.getNGroups();
		result.setNumGroups(numGroups);
		result.setMatches(matches);

	}

	private static boolean isStrictValid(String input, String query){
		boolean isValidSuggestion = false;
		String queryProcessed = query.trim().toLowerCase().replaceAll("\\s+", " ");
		input = input.trim().toLowerCase();
		if(input.contains("/")){
			String tokens[] = input.replaceAll("^[,\\s]+", "").split("[,\\s/]+");
			for(String token:tokens){
				if(queryProcessed.contains(token)){
					isValidSuggestion = true;
					break;
				}
			}
		}else if(queryProcessed.contains(input)){
			isValidSuggestion = true;
		}
		return isValidSuggestion;
	}
	private static boolean isValid(String input, String query){
		boolean isValidSuggestion = true;
		String queryProcessed = query.trim().toLowerCase();
		String tokens[] = input.trim().replaceAll("^[,\\s]+", "").split("[,\\s]+");
		for(String token:tokens){
			if(!queryProcessed.contains(token.trim().toLowerCase()) && !token.contains("/")){
				isValidSuggestion = false;
				break;
			}else if(token.contains("/")){
				String t[] = token.split("/");
				boolean found = false;
				for(String tt:t){
					if(queryProcessed.contains(tt.trim().toLowerCase())){
						found = true;
						break;
					}
				}
				isValidSuggestion = found;
			}
		}
		return isValidSuggestion;
	}

	public static Map<String, String> processStrictGroupQueryResponse(QueryResponse qRes, String query) {
		Map<String, String> entityTypeValMap = new LinkedHashMap<String, String>();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			String eType = group.getGroupValue();


			Iterator<SolrDocument>iter = group.getResult().iterator();
			while(iter.hasNext()){
				SolrDocument doc = iter.next();
				if(entityTypeValMap.get(Constants.NER_LOC_KEY)==null && entityTypeValMap.get(Constants.NER_ZONE_KEY)==null && Constants.NER_LOC_KEY.equalsIgnoreCase(eType)){
					String locality = (String)doc.get("location_name");
					ArrayList<String> localityAlias = (ArrayList<String>)doc.get("location_alias");
					if(isValid(locality, query)){
						String lat_lng = (String)doc.get("lat_lng");
						entityTypeValMap.put(Constants.NER_LOC_KEY, locality);
						entityTypeValMap.put(Constants.LAT_LNG_KEY, lat_lng);
						entityTypeValMap.put(Constants.PROCESSED_QUERY, processQuery(query,locality));
						break;
					}

					if(localityAlias!=null && localityAlias.size()>0){
						if(!StringUtils.isBlank(localityAlias.get(0)) && isValid(localityAlias.get(0),query)){
							String lat_lng = (String)doc.get("lat_lng");
							entityTypeValMap.put(Constants.NER_LOC_KEY, locality);
							entityTypeValMap.put(Constants.LAT_LNG_KEY, lat_lng);
							entityTypeValMap.put(Constants.PROCESSED_QUERY, processQuery(query,localityAlias.get(0)));
							break;
						}
						if(localityAlias.size()>1 && !StringUtils.isBlank(localityAlias.get(1)) && isValid(localityAlias.get(1),query)){
							String lat_lng = (String)doc.get("lat_lng");
							entityTypeValMap.put(Constants.NER_LOC_KEY, locality);
							entityTypeValMap.put(Constants.LAT_LNG_KEY, lat_lng);
							entityTypeValMap.put(Constants.PROCESSED_QUERY, processQuery(query,localityAlias.get(1)));
							break;
						}
					}
				}else if(entityTypeValMap.get(Constants.NER_CUISINE_KEY)==null &&  Constants.NER_CUISINE_KEY.equalsIgnoreCase(eType)){
					String cuisine = (String)doc.get("cuisine");
					if(isStrictValid(cuisine, query)){
						entityTypeValMap.put(Constants.NER_CUISINE_KEY,cuisine);
						break;
					}
				}else if(entityTypeValMap.get(Constants.NER_LOC_KEY)==null && entityTypeValMap.get(Constants.NER_ZONE_KEY)==null &&  Constants.NER_ZONE_KEY.equalsIgnoreCase(eType)){
					String zone = (String)doc.get("zone_name");
					if(isStrictValid(zone, query)){
						entityTypeValMap.put(Constants.NER_ZONE_KEY,zone);
						break;
					}
				}else if(entityTypeValMap.get(Constants.NER_FEATURE_KEY)==null && Constants.NER_FEATURE_KEY.equalsIgnoreCase(eType)){
					String feature = (String)doc.get("feature_name");
					if(isStrictValid(feature, query)){
						entityTypeValMap.put(Constants.NER_FEATURE_KEY,feature);
						break;
					}
				}
			}
		}
		return entityTypeValMap;
	}

	private static String processQuery(String query,String locality) {
		String processed = query.toLowerCase();
		String[] tokens = locality.toLowerCase().split("[,\\s/]");
		for(String token:tokens){
			processed = processed.replaceAll(token, "");
		}
		return processed;
	}
}
