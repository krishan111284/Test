package com.dineout.search.query;

import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

@Component("nerQueryCreator")
public class NerQueryCreator {
	ResourceBundle rb = ResourceBundle.getBundle("search");
	public QueryParam getNERSearchQuery(String query,String city){
		QueryParam queryParam = new QueryParam();
		queryParam.addParam("defType", "edismax");
		queryParam.addParam("mm", "1");
		queryParam.addParam("qf", rb.getString("tc.ner.qf.param"));
		queryParam.addParam("pf", rb.getString("tc.ner.pf.param"));
		queryParam.addParam("fl", rb.getString("tc.ner.fl.param"));
		queryParam.addParam("fq", "((-city_name:[* TO *] AND *:*) OR city_name:"+city+")");
		queryParam.addParam("q", query);
		queryParam.addParam("group", "true");
		queryParam.addParam("group.field", "data_type");
		queryParam.addParam("group.limit", "10");
		return queryParam;
	}

}
