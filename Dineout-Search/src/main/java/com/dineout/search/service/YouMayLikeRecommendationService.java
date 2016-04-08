package com.dineout.search.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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

		Map<Integer,List<Map<Object, Object>>> megaMap = new HashMap<Integer, List<Map<Object,Object>>>();
		for(int i=0;i<resultList.size()-1;i++){
			int count=1;
			Map<Object, Object> map1 = resultList.get(i);
			int rid1 = (Integer) map1.get("r_id");

			for(int j=i+1;j<resultList.size()-1;j++){
				Map<Object, Object> map2 = resultList.get(j);
				int rid2 = (Integer) map2.get("r_id");

				if(rid1 == rid2){
					count++;
				}
				//1 restaurant compared with entire list, and count obtained
				map1.put("count", count);					
			}
			/*if(i>=(resultList.size()-1)/2)
				continue;*/
			if(megaMap.containsKey(count)){
				megaMap.get(count).add(map1);
			}
			else
			{
				List<Map<Object, Object>> tempList = new ArrayList<Map<Object,Object>>();
				tempList.add(map1);
				megaMap.put(count, tempList);
			}

		}
		//outer loop ends, sort within each map
		LinkedHashSet<Map<Object, Object>> sortedResultList = new LinkedHashSet<Map<Object,Object>>();
		
		for (Entry<Integer, List<Map<Object, Object>>> entry : megaMap.entrySet()) {
			List<Map<Object, Object>> value = entry.getValue();
			Collections.sort(value, mapComparator);
		}
		Map<Integer,List<Map<Object, Object>>> sortedMap = new TreeMap<Integer, List<Map<Object, Object>>>(Collections.reverseOrder());
		sortedMap.putAll(megaMap);	

		for (Entry<Integer, List<Map<Object, Object>>> entry : megaMap.entrySet()) {
			List<Map<Object, Object>> value = entry.getValue();
			for(Map<Object, Object> tempMap : value){
				sortedResultList.add(tempMap);
			}
		}
		DORecoResult finalResult = new DORecoResult();
		finalResult.setDocs(new ArrayList<Map<Object,Object>>(sortedResultList).subList(0, 10));
		return finalResult;
	}

	public Comparator<Map<Object, Object>> mapComparator = new Comparator<Map<Object, Object>>() {
		public int compare(Map<Object, Object> m1, Map<Object, Object> m2) {
			return ((Double) m1.get("eucledianDistance")).compareTo((Double) m2.get("eucledianDistance"));
		}
	};

}
