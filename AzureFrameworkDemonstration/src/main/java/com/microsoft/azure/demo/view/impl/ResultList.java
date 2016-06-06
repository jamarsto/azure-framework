package com.microsoft.azure.demo.view.impl;

import java.util.List;

public class ResultList {
	private List<?> results;

	public ResultList(final List<?> results) {
		this.results = results;
	}
	
	public List<?> getResults() {
		return results;
	}
}
