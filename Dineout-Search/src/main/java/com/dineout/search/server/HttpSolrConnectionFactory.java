package com.dineout.search.server;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.stereotype.Component;

@Component("httpSolrConnectionFactory")
public class HttpSolrConnectionFactory implements SolrConnectionFactory{
	
	Logger logger = Logger.getLogger(HttpSolrConnectionFactory.class);
	
	private ResourceBundle rb;
	
	private HttpSolrServer estSolrServer;
	private HttpSolrServer autocompleteSolrServer;
	private HttpSolrServer nerSolrServer;
	
	public HttpSolrConnectionFactory(){		
		initialiseResources();
		initializeRestSolrServer();
		initializeAutocompleteSolrServer();	
		initializeNERSolrServer();
	}
	
	private void initializeNERSolrServer() {
		try {
			  String url=rb.getString("dineout.ner.solr.url");
			  nerSolrServer = new HttpSolrServer(url);
			  setServerProperties(nerSolrServer);
		}catch(Exception e){
					logger.error("Unable to connect to SOLR!!!");
				}
	}
	
	

	private void initializeRestSolrServer() {
		try {
		  String url=rb.getString("dineout.est.solr.url");
		  estSolrServer = new HttpSolrServer(url);
		  setServerProperties(estSolrServer);
		}catch(Exception e){
			logger.error("Unable to connect to SOLR!!!");
		}
	}
	
	private void initializeAutocompleteSolrServer() {
		try {
			  String url=rb.getString("dineout.autocomplete.solr.url");
			  autocompleteSolrServer = new HttpSolrServer(url);
			  setServerProperties(autocompleteSolrServer);
			}catch(Exception e){
				logger.error("Unable to connect to SOLR!!!");
			}
	}
	
	
	private void setServerProperties(HttpSolrServer server){
		server.setSoTimeout(1000*10);// socket read timeout
		server.setConnectionTimeout(1000*10);
		server.setDefaultMaxConnectionsPerHost(100);
		server.setMaxTotalConnections(100);
		server.setFollowRedirects(false);// defaults to false
		server.setAllowCompression(true);
		server.setMaxRetries(1);// defaults to 0.  > 1 not recommended.
		  logger.info("Connection created successfully!!!");
	}


	private void initialiseResources() {
		rb = ResourceBundle.getBundle("search");
	}

	@Override
	public SolrServer getRestSolrServer() {
		return estSolrServer;
	}
	@Override
	public SolrServer getAutoCompletionSolrServer() {
		return autocompleteSolrServer;
	}
	@Override
	public SolrServer getNERSolrServer() {
		return nerSolrServer;
	}
	
}
