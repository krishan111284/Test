package com.dineout.search.response;

public class AutoSuggestResponseBody implements IResponseBody{
	
	private AutoCompleteSearchResult result;

	public AutoCompleteSearchResult getResult() {
		return result;
	}

	public void setResult(AutoCompleteSearchResult result) {
		this.result = result;
	}

}
