package com.microsoft.azure.framework.eventstore.persistence;

import java.util.UUID;

public interface EventSetRepository {

	EventSet getEventSet(String partitionID, String bucketID, UUID streamID, Class<?> filter, Long fromVersion,
			Long toVersion);

	EventSet getEventSet(String partitionID, String bucketID, UUID streamID, Class<?> filter, UUID changeSetID);

	void putEventSet(EventSet eventSet, UUID changeSetID);

}
