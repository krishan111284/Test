package com.dineout.search.response;

public class DOAutoCompleteSuggestionEntry implements ILocationResponseEntity{

	private String r_id;
	private String uid;
	private String guid;
	private String location_name;
	private String area_name;
	private String profile_name;
	private String cuisine_name;
	private String tag_name;
	private String fulfillment;
	private String popularityCount;
	private String lat;
	private String lng;
	
	private String ticket_name;
	private String tl_id;
	private String tg_id;
	private String dc_name;
	private String from_date;
	private String to_date;
	private String suggestion;

	private Float score;

	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}

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
	public String getBookingCount() {
		return popularityCount;
	}
	public void setBookingCount(String bookingCount) {
		this.popularityCount = bookingCount;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getPopularityCount() {
		return popularityCount;
	}
	public void setPopularityCount(String popularityCount) {
		this.popularityCount = popularityCount;
	}
	public String getTicket_name() {
		return ticket_name;
	}
	public void setTicket_name(String ticket_name) {
		this.ticket_name = ticket_name;
	}
	public String getTl_id() {
		return tl_id;
	}
	public void setTl_id(String tl_id) {
		this.tl_id = tl_id;
	}
	public String getTg_id() {
		return tg_id;
	}
	public void setTg_id(String tg_id) {
		this.tg_id = tg_id;
	}
	public String getDc_name() {
		return dc_name;
	}
	public void setDc_name(String dc_name) {
		this.dc_name = dc_name;
	}
	public String getFrom_date() {
		return from_date;
	}
	public void setFrom_date(String from_date) {
		this.from_date = from_date;
	}
	public String getTo_date() {
		return to_date;
	}
	public void setTo_date(String to_date) {
		this.to_date = to_date;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}

}
