package com.microsoft.azure.framework.eventstore.persistence;

import java.util.List;
import java.util.UUID;

public interface EventStoreDAO {

	List<EventStoreEntry> getEvents(String partitionID, String bucketID, UUID streamID, Class<?> filter,
			Long fromVersion, Long toVersion);

	List<EventStoreEntry> getEvents(String partitionID, String bucketID, UUID streamID, Class<?> filter,
			UUID changeSetID);

	void putEvents(List<EventStoreEntry> entries);

}
