package com.microsoft.azure.demo.view.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
	public static final int RUNTIME = 800;
	@XmlElement
	private int code;
	@XmlElement
	private String message;

	public Error() {
	}

	public Error(final String message) {
		this.code = RUNTIME;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
