package com.dineout.search.request;

public class PopularTrendingRequest extends DORestSearchRequest{

	//bycity,limit
	//bylocation,byarea,byType - 1 = fulfilment,2 = discovery

	private String bytiming;

	public String getBytiming() {
		return bytiming;
	}

	public void setBytiming(String bytiming) {
		this.bytiming = bytiming;
	}	

}
