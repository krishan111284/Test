package com.dineout.search.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dineout.search.query.NerQueryCreator;
import com.dineout.search.query.QueryParam;
import com.dineout.search.request.NerRequest;
import com.dineout.search.server.SolrConnectionUtils;
import com.dineout.search.utils.Constants;
import com.dineout.search.utils.DOResponseUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service("tcNerServiceImpl")
public class NerServiceImpl implements NerService{
	Logger logger = Logger.getLogger(NerServiceImpl.class);
	@Autowired
	NerQueryCreator nerQueryCreator;
	
	@Autowired
	SolrConnectionUtils solrConnectionUtils;
	
	LoadingCache<NerRequest, Map<String, String>> cache = null;
	
	
	public NerServiceImpl(){
		CacheLoader<NerRequest, Map<String, String>> loader = new CacheLoader<NerRequest, Map<String,String>>(){
			public Map<String, String> load (NerRequest key){
				logger.info("Loading for Key"+key.toString());
				return loadNamedEntities(key);
			}
		};
		cache = CacheBuilder.newBuilder().maximumSize(Constants.NER_QUERY_CACHE_ROWS).build(loader);
	}
	
	
	public Map<String,String> extactNamedEntities(NerRequest req){
		Map<String, String> nerMap = null;
		try {
			nerMap = cache.get(req);
		} catch (ExecutionException e) {
			logger.error(e.getMessage(),e);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return nerMap;
	}


	public Map<String,String> loadNamedEntities(NerRequest req){
		Map<String,String> resp = null;
		SolrServer server = solrConnectionUtils.getNERSolrServer();
		QueryParam tcqp = null;
		try {
			tcqp = nerQueryCreator.getNERSearchQuery(req.getQuery(),req.getCity());
		} catch (Exception e) {
			
		}

		QueryResponse qres = null;
		try {
			qres = server.query(tcqp);
			if(qres!=null){
					resp = DOResponseUtils.processStrictGroupQueryResponse(qres, req.getQuery());
			}
		} catch (SolrServerException e) {
			
		}
		
		return resp;
	}
}
