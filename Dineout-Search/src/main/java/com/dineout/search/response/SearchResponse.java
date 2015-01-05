package com.dineout.search.response;

public class SearchResponse {
	
	private Header header;
	private IResponseBody body;
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public IResponseBody getBody() {
		return body;
	}
	public void setBody(IResponseBody body) {
		this.body = body;
	}
	
}
