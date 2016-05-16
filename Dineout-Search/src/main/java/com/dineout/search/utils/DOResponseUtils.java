package com.dineout.search.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;

import com.dineout.search.request.DOTicketSearchRequest;
import com.dineout.search.response.DOFacet;
import com.dineout.search.response.DOHighlight;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.response.DOSearchResult;
import com.dineout.search.response.DOSpellCheck;

public class DOResponseUtils {
	static ResourceBundle rb = ResourceBundle.getBundle("search");

	public static DORecoResult processRecoQueryResponse(QueryResponse qres) {
		return processGroupQueryResponseForReco(qres);
	}

	public static DOSearchResult processIdQueryResponse(QueryResponse qres, String domain) {
		DOSearchResult result = new DOSearchResult();
		result.setDomain(domain);
		result.setMatches((Long)qres.getResults().getNumFound());
		result.setDocs(getTCDocList(qres));
		return result;
	}

	public static DORecoResult processRecommendationQueryResponse(QueryResponse qres) {
		DORecoResult result = new DORecoResult();
		result.setDocs(getTCDocList(qres));
		return result;
	}

	public static DOSearchResult processFeaturedQueryResponse(QueryResponse qres, String domain, boolean isSpellcheckApplied) {
		DOSearchResult result = new DOSearchResult();
		result.setDomain(domain);
		result.setMatches((Long)qres.getResults().getNumFound());
		result.setDocs(getTCDocList(qres));
		return result;
	}

	public static DOSearchResult processQueryResponse(QueryResponse qres, String domain, boolean isSpellcheckApplied) {
		DOSearchResult result = new DOSearchResult();
		result.setDomain(domain);
		result.setMatches((Long)qres.getResults().getNumFound());
		result.setDocs(getTCDocList(qres));
		result.setFacet(getFacetInfo(qres));
		result.setHighlight(getHlInfo(qres));
		result.setSpellCheck(getSpellCheckInfo(qres,isSpellcheckApplied));
		return result;
	}

	private static DOHighlight getHlInfo(QueryResponse qres) {
		DOHighlight highlight = new DOHighlight();
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

	public static DOFacet getFacetInfo(QueryResponse qres) {
		DOFacet facet = new DOFacet();
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

	public static List<Map<Object, Object>> getSortedDocList(QueryResponse qres, ArrayList<String> restIds) {
		List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
		Map<String, Map<Object, Object>> docMap = new HashMap<String, Map<Object, Object>>();
		Iterator<SolrDocument> iter = qres.getResults().iterator();
		while(iter.hasNext()){
			SolrDocument solrDoc = iter.next();
			Map<Object, Object> fieldValMap = new HashMap<Object, Object>();
			Iterator<String>fieldIterator = solrDoc.keySet().iterator();
			while(fieldIterator.hasNext()){
				String fieldName = fieldIterator.next();
				fieldValMap.put(fieldName, solrDoc.get(fieldName));
			}
			if(!fieldValMap.isEmpty())
				docMap.put(Integer.toString((Integer) solrDoc.get("r_id")), fieldValMap);
		}
		for(String restId: restIds){
			if(docMap.containsKey(restId)){
				docList.add(docMap.get(restId));
			}
		}
		return docList;
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

	private static DOSpellCheck getSpellCheckInfo(QueryResponse qres, boolean isSpellcheckApplied) {
		DOSpellCheck spellCheck = new DOSpellCheck();
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

	public static DORecoResult processGroupQueryResponseForReco(QueryResponse qRes) {
		DORecoResult result = new DORecoResult();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		List<Map<Object, Object>> docList = new ArrayList<Map<Object, Object>>();
		for(Group group:groups){
			Iterator<SolrDocument> iter = group.getResult().iterator();
			Map<Object, Object> doc = getDODoc(iter.next());
			docList.add(doc);
		}
		result.setDocs(docList);
		return result;
	}

	public static DOSearchResult processTicketGroupQueryResponse(QueryResponse qRes, String domain,DOTicketSearchRequest request) {
		DOSearchResult result = new DOSearchResult();
		ArrayList<Map<Object, Object>> resultList = new ArrayList<Map<Object, Object>>();
		result.setDomain(domain);
		SolrDocument rInfo = null;
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		setMatchesForGroupQuery(result, groupCommand, domain);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			HashMap<Object, Object> restaurantInfo = new HashMap<Object, Object>();
			List<Map<Object, Object>> ticketList = new ArrayList<Map<Object, Object>>();
			Iterator<SolrDocument> iter = group.getResult().iterator();
			while(iter.hasNext()){
				rInfo = iter.next();
				if(rInfo.containsKey("tl_id"))
					ticketList.add(getTicketDoc(rInfo, request, domain));	
			}
			processRestaurantInfo(restaurantInfo,rInfo);
			restaurantInfo.put("dealevents",new ArrayList<Map<Object, Object>>(new LinkedHashSet<Map<Object, Object>>(ticketList)));
			resultList.add(restaurantInfo);
		}
		result.setDocs(resultList);
		result.setFacet(getFacetInfo(qRes));
		result.setSpellCheck(getSpellCheckInfo(qRes,request.isSpellcheckApplied()));
		return result;
	}

	private static Map<Object, Object> getTicketDoc(SolrDocument solrDoc,DOTicketSearchRequest req, String domain) {
		Map<Object, Object> ticketDoc = new HashMap<Object, Object>();
		String fl="";
		fl = StringUtils.isEmpty(req.getEstfl())?rb.getString("dineout.deals.ticket.fl"):req.getEstfl();
		if(req.isSpatialQuery()){
			fl =fl + "," + "distance";
		}
		Set<String>displayFields = new HashSet<String>(Arrays.asList(fl.split(",")));
		Iterator<String>fieldIterator = displayFields.iterator();
		while(fieldIterator.hasNext()){
			String fieldName = fieldIterator.next();
			ticketDoc.put(fieldName, solrDoc.get(fieldName));
		}

		return ticketDoc;
	}

	private static void processRestaurantInfo(HashMap<Object, Object> restaurantInfo, SolrDocument rInfo) {
		//dineout.search.fl
		restaurantInfo.put("r_id", rInfo.get("r_id")!=null?rInfo.get("r_id"):null);
		restaurantInfo.put("rest_name", rInfo.get("rest_name")!=null?rInfo.get("rest_name"):null);
		restaurantInfo.put("img", rInfo.get("img")!=null?rInfo.get("img"):null);
		restaurantInfo.put("fullfillment", rInfo.get("fullfillment")!=null?rInfo.get("fullfillment"):null);
		restaurantInfo.put("is_pf", rInfo.get("is_pf")!=null?rInfo.get("is_pf"):null);
		restaurantInfo.put("url", rInfo.get("url")!=null?rInfo.get("url"):null);
		restaurantInfo.put("area_name", rInfo.get("area_name")!=null?rInfo.get("area_name"):null);
		restaurantInfo.put("locality_name", rInfo.get("locality_name")!=null?rInfo.get("locality_name"):null);
		restaurantInfo.put("avg_rating", rInfo.get("avg_rating")!=null?rInfo.get("avg_rating"):null);
		restaurantInfo.put("tags", rInfo.get("tags")!=null?rInfo.get("tags"):null);
	}

	public static DOSearchResult processGroupQueryResponse(QueryResponse qRes,
			String domain,boolean isSpellcheckApplied) {
		DOSearchResult result = new DOSearchResult();
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

	private static void setMatchesForGroupQuery(DOSearchResult result, GroupCommand groupCommand, String domain ){
		int matches = groupCommand.getMatches();
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


	public static Map<String, ArrayList<String>> processStrictGroupQueryResponse(QueryResponse qRes, String query) {
		Map<String, ArrayList<String>> nerMap = new LinkedHashMap<String, ArrayList<String>>();
		GroupCommand groupCommand = qRes.getGroupResponse().getValues().get(0);
		List<Group> groups =groupCommand.getValues();
		for(Group group:groups){
			String eType = group.getGroupValue();
			if(Constants.NER_CUISINE_KEY.equalsIgnoreCase(eType)){
				Set<String> familyList = new HashSet<String>();
				Set<String> cuisineList = new HashSet<String>();
				Set<String> queryList = new HashSet<String>();
				Iterator<SolrDocument>iter = group.getResult().iterator();
				while(iter.hasNext()){
					SolrDocument doc = iter.next();
					familyList.add((String)doc.get("parent_cuisine_name"));
					@SuppressWarnings("unchecked")
					ArrayList<String> childCuisines = (ArrayList<String>)doc.get("cuisine");
					for(String cuisine:childCuisines){
						if(isStrictValid(cuisine, query)){
							cuisineList.add(cuisine);
						}
					}
				}
				queryList.add(processQuery(query,new ArrayList<String>(cuisineList)));
				nerMap.put(Constants.NER_CUISINE_KEY, new ArrayList<String>(cuisineList));
				nerMap.put(Constants.NER_CUISINE_FAMILY_KEY,  new ArrayList<String>(familyList));
				nerMap.put(Constants.PROCESSED_QUERY,  new ArrayList<String>(queryList));	
			}			
			//For next group/entity in future
		}
		return nerMap;
	}

	private static String processQuery(String query,ArrayList<String> cuisineList) {
		String processed = query.toLowerCase();
		for(String token:cuisineList){
			processed = processed.replaceAll(token.toLowerCase(), "");
		}
		return processed;
	}

	public static String[] getData(Object object){
		ArrayList tempObject =(ArrayList)object;
		String [] fieldData = (String[]) tempObject.toArray(new String[tempObject.size()]);
		return fieldData;
	}

	public static String processStringResponseForArea(QueryResponse qRes) {	
		String area = null;
		Iterator<SolrDocument> iter = qRes.getResults().iterator();
		while(iter.hasNext()){
			SolrDocument solrDoc = iter.next();
			area = (String) solrDoc.get("area_name");
		}
		return area;
	}

}
