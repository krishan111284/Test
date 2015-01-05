package com.dineout.search.response;

public class DOAutoSuggestResponseBody implements IResponseBody{
	
	private DOAutoCompleteSearchResult result;

	public DOAutoCompleteSearchResult getResult() {
		return result;
	}

	public void setResult(DOAutoCompleteSearchResult result) {
		this.result = result;
	}

}
