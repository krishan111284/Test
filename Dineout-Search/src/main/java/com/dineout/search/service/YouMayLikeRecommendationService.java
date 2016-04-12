package com.dineout.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.exception.ErrorCode;
import com.dineout.search.exception.SearchError;
import com.dineout.search.exception.SearchErrors;
import com.dineout.search.query.DORestQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.query.RecommendationQueryCreator;
import com.dineout.search.request.RecommendationRequest;
import com.dineout.search.response.DORecoResult;
import com.dineout.search.server.SolrConnectionUtils;

@Service("youMayLikeRecommendationService")
public class YouMayLikeRecommendationService implements RecommendationService{
	Logger logger = Logger.getLogger(YouMayLikeRecommendationService.class);

	@Autowired
	RecommendationQueryCreator recommendationQueryCreator;
	@Autowired
	DORestQueryCreator restQueryCreator;
	@Autowired 
	SolrConnectionUtils solrConnectionUtils;
	@Autowired
	SimilarVisitedRecommendationService similarVisitedRecommendationService;

	@SuppressWarnings("unchecked")
	@Override
	public List<DORecoResult> getRecommendedResults(RecommendationRequest request, SearchErrors errors) {
		QueryParam doqp = null;
		QueryResponse qres = null;
		List<DORecoResult> finalresultList = new ArrayList<DORecoResult>();
		try {
			String[] restaurantIds = null;
			SolrServer server = solrConnectionUtils.getDinerSolrServer();
			doqp = recommendationQueryCreator.getDinerIdSearchQuery(request);
			qres = server.query(doqp);
			if(qres!=null){
				Iterator<SolrDocument> resultIterator = qres.getResults().iterator();
				while(resultIterator.hasNext()){
					SolrDocument solrDoc = resultIterator.next();
					if(solrDoc.get("restaurants_booked")!=null)
						restaurantIds = ((List<DORecoResult>) solrDoc.get("restaurants_booked")).toArray(new String[0]);
				}
			}

			if(restaurantIds!=null && restaurantIds.length>0){
				DORecoResult result = getRecommendedRestaurants(request,restaurantIds,errors);
				finalresultList.add(result);
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			SearchError error = new SearchError(ErrorCode.SOLR_ERROR_CODE, e.getMessage());
			errors.add(error);
		}
		return finalresultList;
	}

	private DORecoResult getRecommendedRestaurants(RecommendationRequest request, String[] restaurantIds, SearchErrors errors) {
		List<Map<Object, Object>> resultList = new ArrayList<Map<Object,Object>>();
		request.setRestIds(restaurantIds);
		for (String restId: restaurantIds) {
			request.setRestId(restId);
			List<DORecoResult> result = similarVisitedRecommendationService.getRecommendedResults(request, errors);
			for (Map<Object, Object> map : result.get(0).getDocs()) {
				resultList.add(map);
			}
		}

		Map<Integer,Integer> countMap = new HashMap<Integer, Integer>();
		for(Map<Object,Object> m:resultList){
			Integer count = countMap.get(m.get("r_id"));
			if(count==null){
				Integer newCount = new Integer(1);
				Integer id = (Integer)m.get("r_id");
				countMap.put(id,newCount);
				continue;
			}
			count++;
		}


		Map<Map<Object,Object>,Integer> uniqueMap = new HashMap<Map<Object,Object>, Integer>(); 
		for(Map<Object,Object> m:resultList){
			Integer count = countMap.remove(m.get("r_id"));
			if(count!=null){
				uniqueMap.put(m, count);
			}
		}

		List<Entry<Map<Object, Object>, Integer>> listOfSortedResults = sortByValue(uniqueMap);

		List<Map<Object, Object>> result = new ArrayList<Map<Object,Object>>();
		for(Entry<Map<Object, Object>, Integer> entry:listOfSortedResults){
			result.add(entry.getKey());
		}

		DORecoResult finalResult = new DORecoResult();
		finalResult.setDocs(new ArrayList<Map<Object,Object>>(result).subList(0, 10));
		return finalResult;
	}


	public static List<Map.Entry<Map<Object,Object>, Integer>> 
	sortByValue( Map<Map<Object,Object>, Integer> map ){
		List<Map.Entry<Map<Object,Object>, Integer>> list =
				new LinkedList<>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<Map<Object,Object>, Integer>>(){

			@Override
			public int compare( Map.Entry<Map<Object,Object>, Integer> o1, Map.Entry<Map<Object,Object>, Integer> o2 )
			{
				return ((Float) o1.getKey().get("eucledianDistance")).compareTo((Float) o2.getKey().get("eucledianDistance"));
			}
		} );

		Collections.sort( list, new Comparator<Map.Entry<Map<Object,Object>, Integer>>(){

			@Override
			public int compare( Map.Entry<Map<Object,Object>, Integer> o1, Map.Entry<Map<Object,Object>, Integer> o2 )
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		} );

		return list;
	}

}
