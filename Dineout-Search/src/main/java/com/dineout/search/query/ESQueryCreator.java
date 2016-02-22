package com.dineout.search.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.stereotype.Component;

import com.dineout.search.request.PopularTrendingRequest;
import com.dineout.search.utils.IndexUtils;

@Component("esQueryCreator")
public class ESQueryCreator {
	Logger logger = Logger.getLogger(ESQueryCreator.class);
	ResourceBundle rb = ResourceBundle.getBundle("search");

	public SearchRequestBuilder getSearchQuery1(PopularTrendingRequest req, Client client) {
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

	public SearchRequestBuilder getSearchQuery(PopularTrendingRequest req, Client client) {
		Date date = new Date();
		long todate = date.getTime();
		ArrayList<FilterBuilder> filterList = new ArrayList<FilterBuilder>();
		long [] startendtiming = IndexUtils.getstartEndTime(todate, Integer.parseInt(req.getBytiming()));

		SearchRequestBuilder srb = new SearchRequestBuilder(client);
		srb.setIndices(IndexUtils.getIndexes(Integer.parseInt(req.getBytiming())));
		applyCityFilter(req,filterList);
		applyLocationAreaFilter(req,filterList);
		applyFulfilmentFilter(req,filterList);
		applyTimeRangeFilter(req,filterList,startendtiming);

		srb.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				FilterBuilders.andFilter(filterList.toArray(new FilterBuilder[filterList.size()]))));
		srb.addAggregation(AggregationBuilders.terms("popularRestaurantsFacet").field("restId.raw").size(10));
		srb.setSize(0);
		System.out.println(srb.toString());
		return srb;
	}

	private void applyTimeRangeFilter(PopularTrendingRequest req, ArrayList<FilterBuilder> filterList, long[] startendtiming) {
		FilterBuilder timeRangeFilter = FilterBuilders.rangeFilter("@timestamp").from(startendtiming[1]).to(startendtiming[0]);
		filterList.add(timeRangeFilter);
	}

	private void applyCityFilter(PopularTrendingRequest req, ArrayList<FilterBuilder> filterList) {
		if(req.getBycity()!=null){
			FilterBuilder cityFilter = FilterBuilders.termFilter("city.raw", req.getBycity());
			filterList.add(cityFilter);
		}
	}

	private void applyLocationAreaFilter(PopularTrendingRequest req, ArrayList<FilterBuilder> filterList) {
		if(req.getByarea()!=null && req.getByarea().length>0){
			ArrayList<TermFilterBuilder> areaFilterList = new ArrayList<TermFilterBuilder>();
			for(String area : req.getByarea()){
				areaFilterList.add(FilterBuilders.termFilter("area.raw", area));	
			}
			FilterBuilder areaFilter = FilterBuilders.orFilter(areaFilterList.toArray(new TermFilterBuilder[areaFilterList.size()]));
			filterList.add(areaFilter);
		}
		else if(req.getBylocation()!=null && req.getBylocation().length>0){
			ArrayList<TermFilterBuilder> locationFilterList = new ArrayList<TermFilterBuilder>();
			for(String location : req.getBylocation()){
				locationFilterList.add(FilterBuilders.termFilter("location.raw", location));	
			}
			FilterBuilder locationFilter = FilterBuilders.orFilter(locationFilterList.toArray(new TermFilterBuilder[locationFilterList.size()]));
			filterList.add(locationFilter);
		}
	}

	private void applyFulfilmentFilter(PopularTrendingRequest req, ArrayList<FilterBuilder> filterList) {
		if(req.getByType()!=null){
			FilterBuilder fulfilmentFilter = FilterBuilders.termFilter("fulfilment.raw", req.getByType());
			filterList.add(fulfilmentFilter);
		}
	}

}
