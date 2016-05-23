package com.microsoft.azure.demo.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
	public static final int AGGREGATE = 100;
	public static final int COMMAND = 200;
	public static final int DOMAIN_SERVICE = 300;
	public static final int PERSISTENCE = 400;
	public static final int PRECONDITION = 500;
	public static final int RUNTIME = 600;
	@XmlElement
	private int code;
	@XmlElement
	private String message;

	public Error() {
	}

	public Error(final int code, final String message) {
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
