package com.dineout.search.request;

import org.apache.commons.lang3.StringUtils;

import com.dineout.search.utils.Constants;



public class DORestSearchRequest extends GenericDOSearchRequest{
	
	private String byexplore;
	private String bywhichtype;
	private String[] byesttype;
	private String[] byesttypeName;
	private String byfeaturetags;
	private String sponsorId;
	private String[] bycuisine;
	private String[] bygenre;
	private String[] bylocation;
	private String[] byinsidelandmark;
	private String[] byprice;
	private String[] byfs;
	private String[] bydeals;
	private String[] bycrowd;
	private String[] byspeciality;
	private String[] byestgroupname;
	private String byestisfeatured;
	private String byrate; 
	private String [] byzone; 
	private String bytags; 
	private String relevance;
	// Group request..
	private String byestgroup;
	private String estgroup;
	private String[] estid;
	private String estfl;
	private String esthl;
	private String esthlfl;
	private String disableestfacet;
	private String estfacetfl;
	private String boost;
	private String enableNearby;
	//Service Tags
	private String[] byestservicetag;
	
	public String[] getByestservicetag() {
		return byestservicetag;
	}
	public void setByestservicetag(String[] byestservicetag) {
		this.byestservicetag = byestservicetag;
	}
	public String getByexplore() {
		return byexplore;
	}
	public void setByexplore(String byexplore) {
		this.byexplore = byexplore;
	}
	public String getBywhichtype() {
		return bywhichtype;
	}
	public void setBywhichtype(String bywhichtype) {
		this.bywhichtype = bywhichtype;
	}
	
	public String[] getByesttype() {
		return byesttype;
	}
	public void setByesttype(String[] byesttype) {
		this.byesttype = byesttype;
	}
	public String getByfeaturetags() {
		return byfeaturetags;
	}
	public void setByfeaturetags(String byfeaturetags) {
		this.byfeaturetags = byfeaturetags;
	}
	public String getSponsorId() {
		return sponsorId;
	}
	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}
	public String[] getBycuisine() {
		return bycuisine;
	}
	public void setBycuisine(String[] bycuisine) {
		this.bycuisine = bycuisine;
	}
	public String[] getBygenre() {
		return bygenre;
	}
	public void setBygenre(String[] bygenre) {
		this.bygenre = bygenre;
	}
	public String[] getBylocation() {
		return bylocation;
	}
	public void setBylocation(String[] bylocation) {
		this.bylocation = bylocation;
	}
	
	public String[] getByinsidelandmark() {
		return byinsidelandmark;
	}
	public void setByinsidelandmark(String[] byinsidelandmark) {
		this.byinsidelandmark = byinsidelandmark;
	}
	public String[] getByprice() {
		return byprice;
	}
	public void setByprice(String[] byprice) {
		this.byprice = byprice;
	}
	public String[] getByfs() {
		return byfs;
	}
	public void setByfs(String[] byfs) {
		this.byfs = byfs;
	}
	public String getByrate() {
		return byrate;
	}
	public void setByrate(String byrate) {
		this.byrate = byrate;
	}
	
	public String[] getByzone() {
		return byzone;
	}
	public void setByzone(String[] byzone) {
		this.byzone = byzone;
	}
	public String getBytags() {
		return bytags;
	}
	public void setBytags(String bytags) {
		this.bytags = bytags;
	}
	public String getByGroup() {
		return byestgroup;
	}
	public void setByestgroup(String estbygroup) {
		this.byestgroup = estbygroup;
	}
	public String getGroup() {
		return estgroup;
	}
	public void setEstgroup(String estgroup) {
		this.estgroup = estgroup;
	}	
	public boolean isGrouprequest(){
		return StringUtils.isEmpty(byestgroup) && Constants.GROUP_TRUE.equals(estgroup);
	}
	public String[] getEstid() {
		return estid;
	}
	public void setEstid(String[] estid) {
		this.estid = estid;
	}
	public String getEstfl() {
		return estfl;
	}
	public void setEstfl(String estfl) {
		this.estfl = estfl;
	}
	public String[] getBydeals() {
		return bydeals;
	}
	public void setBydeals(String[] bydeals) {
		this.bydeals = bydeals;
	}
	public String[] getBycrowd() {
		return bycrowd;
	}
	public void setBycrowd(String[] bycrowd) {
		this.bycrowd = bycrowd;
	}
	public String[] getByspeciality() {
		return byspeciality;
	}
	public void setByspeciality(String[] byspeciality) {
		this.byspeciality = byspeciality;
	}
	public String[] getByestgroupname() {
		return byestgroupname;
	}
	public void setByestgroupname(String[] byestgroupname) {
		this.byestgroupname = byestgroupname;
	}
	public String getByestisfeatured() {
		return byestisfeatured;
	}
	public void setByestisfeatured(String byestisfeatured) {
		this.byestisfeatured = byestisfeatured;
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
	public String getRelevance() {
		return relevance;
	}
	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}
	public String getBoost() {
		return boost;
	}
	public void setBoost(String boost) {
		this.boost = boost;
	}
	public String getEnableNearby() {
		return enableNearby;
	}
	public void setEnableNearby(String enableNearby) {
		this.enableNearby = enableNearby;
	}
	public String[] getByesttypeName() {
		return byesttypeName;
	}
	public void setByesttypeName(String[] byesttypeName) {
		this.byesttypeName = byesttypeName;
	}
	
}
