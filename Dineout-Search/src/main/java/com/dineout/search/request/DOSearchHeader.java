package com.dineout.search.request;

public class DOSearchHeader {
	
	private String auto;
	private String idsearch;
	private String device;
	private String source;
		
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getAuto() {
		return auto;
	}
	public void setAuto(String auto) {
		this.auto = auto;
	}
	public String getIdsearch() {
		return idsearch;
	}
	public void setIdsearch(String idsearch) {
		this.idsearch = idsearch;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}

}
