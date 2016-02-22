package com.dineout.search.server;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Component;

@Component("httpESConnectionFactory")
public class HttpESConnectionFactory {

	Logger logger = Logger.getLogger(HttpESConnectionFactory.class);

	private ResourceBundle rb;

	private Client client;
	private Settings settings;

	public HttpESConnectionFactory(){		
		initialiseResources();
		initializeESSolrServer();
	}

	private void initializeESSolrServer() {
		try {
			settings =ImmutableSettings.settingsBuilder().put("client.transport.sniff", true)
					.put("cluster.name", rb.getString("es.cluster.name")).build();
			client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(rb.getString("es.host.ip"), 9300));
		}catch(Exception e){
			logger.error("Unable to connect to ES!!!");
		}
	}

	private void initialiseResources() {
		rb = ResourceBundle.getBundle("search");
	}

	public Client getESSolrServer() {
		return client;
	}

}
