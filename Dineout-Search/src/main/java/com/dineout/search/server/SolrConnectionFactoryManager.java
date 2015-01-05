package com.dineout.search.server;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("solrConnectionFactoryManager")
public class SolrConnectionFactoryManager {
	
	Logger logger = Logger.getLogger(SolrConnectionFactoryManager.class);
	
	@Autowired
	HttpSolrConnectionFactory httpSolrConnectionFactory;
	
	ResourceBundle rb = ResourceBundle.getBundle("search");
	
	public SolrConnectionFactory getSolrConnectionFactory() {
		SolrConnectionFactory solrConnectionFactory = httpSolrConnectionFactory;
		return solrConnectionFactory;
	}

}
