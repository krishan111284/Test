package com.dineout.search.response;

import java.util.List;
import java.util.Map;

public class DOSearchResult {
	private String domain;
	private long matches;
	private long numGroups;
	private List<Map<Object, Object>> docs;
	private DOFacet facet;
	private DOSpellCheck spellCheck;
	private DOHighlight highlight;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public long getMatches() {
		return matches;
	}
	public void setMatches(long matches) {
		this.matches = matches;
	}
	public long getNumGroups() {
		return numGroups;
	}
	public void setNumGroups(long numGroups) {
		this.numGroups = numGroups;
	}
	public DOFacet getFacet() {
		return facet;
	}
	public List<Map<Object, Object>> getDocs() {
		return docs;
	}
	public void setDocs(List<Map<Object, Object>> docs) {
		this.docs = docs;
	}
	public void setFacet(DOFacet facet) {
		this.facet = facet;
	}
	public DOSpellCheck getSpellCheck() {
		return spellCheck;
	}
	public void setSpellCheck(DOSpellCheck spellCheck) {
		this.spellCheck = spellCheck;
	}
	public DOHighlight getHighlight() {
		return highlight;
	}
	public void setHighlight(DOHighlight highlight) {
		this.highlight = highlight;
	}
	
	
}
