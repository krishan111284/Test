package com.dineout.search.request;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.utils.Constants;


public class DORestSearchRequest extends GenericDOSearchRequest{
	
	private String[] bycuisine;
	private String[] bylocation;
	private String[] bylandmark;
	private String[] byprice;
	private String[] byestgroupname;
	private String[] byrate; 
	private String [] byarea; 
	private String[] bytags; 
	private String bygroup;
	private String group;
	private String estfl;
	private String esthl;
	private String esthlfl;
	private String disableestfacet;
	private String estfacetfl;
	//Service Tags
	private String[] byestservicetag;
	private String restId;
	private String avg_rating;
	private String profile_name;
	
	public boolean isGrouprequest(){
		return StringUtils.isEmpty(bygroup) && Constants.GROUP_TRUE.equals(group);
	}
	public String[] getBycuisine() {
		return bycuisine;
	}
	public void setBycuisine(String[] bycuisine) {
		this.bycuisine = bycuisine;
	}
	public String[] getBylocation() {
		return bylocation;
	}
	public void setBylocation(String[] bylocation) {
		this.bylocation = bylocation;
	}
	public String[] getBylandmark() {
		return bylandmark;
	}
	public void setBylandmark(String[] bylandmark) {
		this.bylandmark = bylandmark;
	}
	public String[] getByprice() {
		return byprice;
	}
	public void setByprice(String[] byprice) {
		this.byprice = byprice;
	}
	public String[] getByestgroupname() {
		return byestgroupname;
	}
	public void setByestgroupname(String[] byestgroupname) {
		this.byestgroupname = byestgroupname;
	}
	public String[] getByrate() {
		return byrate;
	}
	public void setByrate(String[] byrate) {
		this.byrate = byrate;
	}
	public String[] getByarea() {
		return byarea;
	}
	public void setByarea(String[] byarea) {
		this.byarea = byarea;
	}
	public String[] getBytags() {
		return bytags;
	}
	public void setBytags(String[] bytags) {
		this.bytags = bytags;
	}
	public String getBygroup() {
		return bygroup;
	}
	public void setBygroup(String bygroup) {
		this.bygroup = bygroup;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getEstfl() {
		return estfl;
	}
	public void setEstfl(String estfl) {
		this.estfl = estfl;
	}
	public String getEsthl() {
		return esthl;
	}
	public void setEsthl(String esthl) {
		this.esthl = esthl;
	}
	public String getEsthlfl() {
		return esthlfl;
	}
	public void setEsthlfl(String esthlfl) {
		this.esthlfl = esthlfl;
	}
	public String getDisableestfacet() {
		return disableestfacet;
	}
	public void setDisableestfacet(String disableestfacet) {
		this.disableestfacet = disableestfacet;
	}
	public String getEstfacetfl() {
		return estfacetfl;
	}
	public void setEstfacetfl(String estfacetfl) {
		this.estfacetfl = estfacetfl;
	}
	public String[] getByestservicetag() {
		return byestservicetag;
	}
	public void setByestservicetag(String[] byestservicetag) {
		this.byestservicetag = byestservicetag;
	}
	public String getRestId() {
		return restId;
	}
	public void setRestId(String restId) {
		this.restId = restId;
	}
	public String getAvg_rating() {
		return avg_rating;
	}
	public void setAvg_rating(String avg_rating) {
		this.avg_rating = avg_rating;
	}
	public String getProfile_name() {
		return profile_name;
	}
	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}
}
