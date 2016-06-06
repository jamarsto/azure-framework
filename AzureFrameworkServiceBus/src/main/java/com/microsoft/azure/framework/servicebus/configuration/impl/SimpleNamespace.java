package com.microsoft.azure.framework.servicebus.configuration.impl;

import com.microsoft.azure.framework.servicebus.configuration.Namespace;

public final class SimpleNamespace implements Namespace {
	private String name;
	private String secret;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public void setSecret(final String secret) {
		this.secret = secret;
	}
}
