package com.dineout.search.response;

import java.util.List;
import java.util.Map;

public class SearchResult {
	private String domain;
	private long matches;
	private long numGroups;
	private List<Map<Object, Object>> docs;
	private Facet facet;
	private SpellCheck spellCheck;
	private Highlight highlight;
	
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
	public Facet getFacet() {
		return facet;
	}
	public List<Map<Object, Object>> getDocs() {
		return docs;
	}
	public void setDocs(List<Map<Object, Object>> docs) {
		this.docs = docs;
	}
	public void setFacet(Facet facet) {
		this.facet = facet;
	}
	public SpellCheck getSpellCheck() {
		return spellCheck;
	}
	public void setSpellCheck(SpellCheck spellCheck) {
		this.spellCheck = spellCheck;
	}
	public Highlight getHighlight() {
		return highlight;
	}
	public void setHighlight(Highlight highlight) {
		this.highlight = highlight;
	}
	
	
}
