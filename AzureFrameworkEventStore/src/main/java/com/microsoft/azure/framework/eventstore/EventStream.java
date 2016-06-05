package com.microsoft.azure.framework.eventstore;

import java.util.UUID;

public interface EventStream {

	String getBucketID();

	UUID getStreamID();

}
