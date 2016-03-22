package com.dineout.search.request;

public class RecommendationRequest extends DORestSearchRequest{
	
	private String dinerId;
	private String restId;

	public String getDinerId() {
		return dinerId;
	}
	public void setDinerId(String dinerId) {
		this.dinerId = dinerId;
	}
	public String getRestId() {
		return restId;
	}
	public void setRestId(String restId) {
		this.restId = restId;
	}
	

}
