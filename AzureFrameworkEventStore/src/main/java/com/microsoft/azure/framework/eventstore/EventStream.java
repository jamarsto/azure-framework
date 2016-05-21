package com.microsoft.azure.framework.eventstore;

import java.util.UUID;

public interface EventStream {

	String getPartitionID();

	String getBucketID();

	UUID getStreamID();

}
