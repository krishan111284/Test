package com.dineout.search.response;

public class DORecoResponseBody implements IResponseBody{
	private int matches;
	private DORecoResult recommendations;

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public DORecoResult getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(DORecoResult recommendations) {
		this.recommendations = recommendations;
	}
}
