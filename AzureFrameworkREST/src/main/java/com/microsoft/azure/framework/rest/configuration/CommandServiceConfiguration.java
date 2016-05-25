package com.microsoft.azure.framework.rest.configuration;

import java.util.Map;

public interface CommandServiceConfiguration {

	Map<String, Class<?>> getCommandMap();

}
