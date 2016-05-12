package com.dineout.search.request;

public class DOTicketSearchRequest extends DORestSearchRequest{
	private String dealId;
	private String[] byCategory;
	private String fromDate;
	private String toDate;
	private String[] byTicketType;
	private String[] byRestaurant;
	
	public String getDealId() {
		return dealId;
	}
	public void setDealId(String dealId) {
		this.dealId = dealId;
	}
	public String[] getByCategory() {
		return byCategory;
	}
	public void setByCategory(String[] byCategory) {
		this.byCategory = byCategory;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String[] getByTicketType() {
		return byTicketType;
	}
	public void setByTicketType(String[] byTicketType) {
		this.byTicketType = byTicketType;
	}
	public String[] getByRestaurant() {
		return byRestaurant;
	}
	public void setByRestaurant(String[] byRestaurant) {
		this.byRestaurant = byRestaurant;
	}

}
