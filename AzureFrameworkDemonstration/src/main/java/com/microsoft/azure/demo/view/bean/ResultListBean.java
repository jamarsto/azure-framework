package com.microsoft.azure.demo.view.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultListBean {
	@XmlElement
	private List<?> results;

	public ResultListBean(final List<?> results) {
		this.results = results;
	}
	
	public List<?> getResults() {
		return results;
	}
}
