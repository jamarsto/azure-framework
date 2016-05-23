package com.microsoft.azure.framework.rest;

import java.util.Map;

public interface CommandServiceConfiguration {

	Map<String, Class<?>> getCommandMap();

}
