package com.dineout.search.server;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("solrConnectionUtils")
public class SolrConnectionUtils {	
	
	@Autowired
	SolrConnectionFactoryManager solrConnectionFactoryManager;
	
	SolrConnectionFactory solrConnectionFactory;
	
	@PostConstruct
	public void initializeSolrConnectionFactory(){
		solrConnectionFactory = solrConnectionFactoryManager.getSolrConnectionFactory();
	}
	
	public SolrServer getRestSolrServer(){
		return solrConnectionFactory.getRestSolrServer();
	}
	public SolrServer getAutoSolrServer(){
		return solrConnectionFactory.getAutoCompletionSolrServer();
	}
	
	public SolrServer getNERSolrServer(){
		return solrConnectionFactory.getNERSolrServer();
	}	
	
}
