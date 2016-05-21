package com.microsoft.azure.framework.eventstore.persistence;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface EventSet {

	interface Builder {
	
		EventSet build();

		Builder buildBucketID(String bucketID);

		Builder buildEvents(List<Serializable> events);

		Builder buildFromVersion(Long fromVersion);

		Builder buildPartitionID(String partitionID);

		Builder buildStreamID(UUID streamID);

		Builder buildToVersion(Long toVersion);
	
	}
	
	interface BuilderFactory {
	
		Builder create();
	
	}

	String getBucketID();

	List<Serializable> getEvents();

	Long getFromVersion();

	String getPartitionID();

	UUID getStreamID();

	Long getToVersion();

}
