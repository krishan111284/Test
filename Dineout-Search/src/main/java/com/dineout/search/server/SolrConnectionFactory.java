package com.dineout.search.server;

import org.apache.solr.client.solrj.SolrServer;

public interface SolrConnectionFactory {
	
	public SolrServer getRestSolrServer();	
	public SolrServer getAutoCompletionSolrServer();
	public SolrServer getNERSolrServer();
	public SolrServer getDinerSolrServer();

}
