package com.microsoft.azure.demo.view.impl;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class AbstractViewHandler {
	protected final <T> T getBean(final ServletContext servletContext, final Class<T> clazz) {
		return WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(clazz);
	}
}
