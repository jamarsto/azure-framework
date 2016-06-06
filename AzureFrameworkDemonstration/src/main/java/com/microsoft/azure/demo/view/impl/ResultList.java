package com.microsoft.azure.demo.view.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultList {
	@XmlElement
	private List<?> results;

	public ResultList(final List<?> results) {
		this.results = results;
	}
	
	public List<?> getResults() {
		return results;
	}
}
