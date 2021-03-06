package com.microsoft.azure.framework.rest.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorBean {
	public static final int AGGREGATE = 100;
	public static final int AGGREGATE_EXISTANCE = 200;
	public static final int COMMAND = 300;
	public static final int DOMAIN_SERVICE = 400;
	public static final int PERSISTENCE = 500;
	public static final int PERSISTENCE_CONCURRENT_UPDATE = 600;
	public static final int PRECONDITION = 700;
	public static final int RUNTIME = 800;
	@XmlElement
	private int code;
	@XmlElement
	private String message;

	public ErrorBean() {
	}

	public ErrorBean(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
