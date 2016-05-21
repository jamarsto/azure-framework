package com.microsoft.azure.demo;

import java.math.BigDecimal;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class TestHarness {
	public static void main(final String[] args) {
		@SuppressWarnings("resource")
		final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/context.xml");
		final HarnessService harness = applicationContext.getBean(HarnessService.class);
		harness.testPreconditions();
		harness.depositFunds(new BigDecimal("100.00"));
	}
}
