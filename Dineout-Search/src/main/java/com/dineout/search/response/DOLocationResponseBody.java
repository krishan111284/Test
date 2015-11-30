package com.dineout.search.response;

public class DOLocationResponseBody implements IResponseBody {
	private DOLocationSearchResult result;

	public DOLocationSearchResult getResult() {
		return result;
	}

	public void setResult(DOLocationSearchResult result) {
		this.result = result;
	}

}
