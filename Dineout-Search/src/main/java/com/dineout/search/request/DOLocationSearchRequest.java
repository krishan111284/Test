package com.dineout.search.request;

public class DOLocationSearchRequest extends GenericDOSearchRequest {
	/**Flags**/
	private boolean isGPSQuery; 			// only lat-long passed, to retrieve current location
	private boolean isSearchQuery; 			// only search key passed to retrieve all matching locations city wise
	private boolean isDistanceSearchQuery;	// both lat/lng and key passed to retrieve matching locations as per distance
	
	public boolean isGPSQuery() {
		return isGPSQuery;
	}
	public void setGPSQuery(boolean isGPSQuery) {
		this.isGPSQuery = isGPSQuery;
	}
	public boolean isSearchQuery() {
		return isSearchQuery;
	}
	public void setSearchQuery(boolean isSearchQuery) {
		this.isSearchQuery = isSearchQuery;
	}
	public boolean isDistanceSearchQuery() {
		return isDistanceSearchQuery;
	}
	public void setDistanceSearchQuery(boolean isDistanceSearchQuery) {
		this.isDistanceSearchQuery = isDistanceSearchQuery;
	}
}
