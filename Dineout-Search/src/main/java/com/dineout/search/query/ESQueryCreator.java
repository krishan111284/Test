package com.dineout.search.query;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.stereotype.Component;

import com.dineout.search.request.PopularTrendingRequest;
import com.dineout.search.utils.IndexUtils;

@Component("esQueryCreator")
public class ESQueryCreator {
	Logger logger = Logger.getLogger(ESQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public SearchRequestBuilder getSearchQuery(PopularTrendingRequest req, Client client) {
		Date date = new Date();
		long todate = date.getTime();
		long [] startendtiming = IndexUtils.getstartEndTime(todate, Integer.parseInt(req.getBytiming()));

		SearchRequestBuilder srb = new SearchRequestBuilder(client);
		srb.setIndices(IndexUtils.getIndexes(Integer.parseInt(req.getBytiming())));
		srb.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				FilterBuilders.andFilter(
						FilterBuilders.termFilter("requestpath.raw", "/dineout/nearby/getresult"),
						FilterBuilders.rangeFilter("@timestamp").from(startendtiming[1]).to(startendtiming[0]))));
		srb.addAggregation(AggregationBuilders.terms("popularRestaurantsFacet").field("restId.raw").size(10));
		srb.setSize(0);
		return srb;
	}

}
