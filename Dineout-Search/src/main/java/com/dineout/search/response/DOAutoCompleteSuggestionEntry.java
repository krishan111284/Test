package com.dineout.search.response;

public class DOAutoCompleteSuggestionEntry {
	
	private String r_id;
	private String uid;
	private String guid;
	private String location_name;
	private String area_name;
	private String profile_name;
	private String cuisine_name;
	private String tag_name;
	private String fulfillment;
	private float bookingCount;

	private Float score;
	
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	
	private String suggestion;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLocation_name() {
		return location_name;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	public String getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getProfile_name() {
		return profile_name;
	}
	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}
	public String getCuisine_name() {
		return cuisine_name;
	}
	public void setCuisine_name(String cuisine_name) {
		this.cuisine_name = cuisine_name;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	public String getR_id() {
		return r_id;
	}
	public void setR_id(String r_id) {
		this.r_id = r_id;
	}
	public String getFulfillment() {
		return fulfillment;
	}
	public void setFulfillment(String fulfillment) {
		this.fulfillment = fulfillment;
	}
	public float getBookingCount() {
		return bookingCount;
	}
	public void setBookingCount(float bookingCount) {
		this.bookingCount = bookingCount;
	}
	
}
