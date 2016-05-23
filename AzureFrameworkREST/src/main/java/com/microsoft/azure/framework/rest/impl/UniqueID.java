package com.microsoft.azure.framework.rest.impl;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UniqueID {
	@XmlElement
	private UUID id;
	
	public UniqueID() {
	}
	
	public UniqueID(final UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
}
