package com.dineout.search.response;

import java.util.List;
import java.util.Map;

public class DORecoResult {
	private List<Map<Object, Object>> docs;

	public List<Map<Object, Object>> getDocs() {
		return docs;
	}
	public void setDocs(List<Map<Object, Object>> docs) {
		this.docs = docs;
	}


}
