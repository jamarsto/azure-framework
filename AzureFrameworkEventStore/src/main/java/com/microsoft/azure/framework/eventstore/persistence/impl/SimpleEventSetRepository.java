package com.microsoft.azure.framework.eventstore.persistence.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.eventstore.persistence.DeserializationException;
import com.microsoft.azure.framework.eventstore.persistence.EventSet;
import com.microsoft.azure.framework.eventstore.persistence.EventSetRepository;
import com.microsoft.azure.framework.eventstore.persistence.EventStoreDAO;
import com.microsoft.azure.framework.eventstore.persistence.EventStoreEntry;
import com.microsoft.azure.framework.eventstore.persistence.SerializationException;

@Component
public final class SimpleEventSetRepository implements EventSetRepository {
	private static final List<Serializable> EMPTY = Collections.unmodifiableList(new ArrayList<Serializable>());
	@Autowired
	private EventSet.BuilderFactory eventSetBuilderFactory;
	@Autowired
	private EventStoreDAO eventStoreDAO;
	@Autowired
	private EventStoreEntry.BuilderFactory eventStoreEntryBuilderFactory;

	private List<Serializable> deserialize(final List<EventStoreEntry> entries) {
		final List<Serializable> results = new ArrayList<Serializable>();
		for (final EventStoreEntry entry : entries) {
			try {
				Class<?> clazz = Class.forName(entry.getEventClassName());
				ObjectMapper mapper = new ObjectMapper();
				results.add((Serializable) mapper.readValue(entry.getEvent(), clazz));
			} catch (ClassNotFoundException | IOException e) {
				throw new DeserializationException(e.getMessage(), e);
			}
		}
		return Collections.unmodifiableList(results);
	}

	@Override
	public EventSet getEventSet(final String partitionID, final String bucketID, final UUID streamID,
			final Class<?> filter, final Long fromVersion, final Long toVersion) {
		final List<EventStoreEntry> entries = eventStoreDAO.getEvents(partitionID, bucketID, streamID, filter,
				fromVersion, toVersion);
		final EventSet.Builder eventSetBuilder = eventSetBuilderFactory.create();
		eventSetBuilder.buildPartitionID(partitionID).buildBucketID(bucketID).buildStreamID(streamID);

		if (!entries.isEmpty()) {
			final Long startVersion = entries.get(0).getVersion();
			final Long endVersion = entries.get(entries.size() - 1).getVersion();
			return eventSetBuilder.buildFromVersion(startVersion).buildToVersion(endVersion)
					.buildEvents(deserialize(entries)).build();
		} else {
			return eventSetBuilder.buildFromVersion(0L).buildToVersion(0L).buildEvents(EMPTY).build();
		}
	}

	@Override
	public EventSet getEventSet(final String partitionID, final String bucketID, final UUID streamID,
			final Class<?> filter, final UUID changeSetID) {
		final List<EventStoreEntry> entries = eventStoreDAO.getEvents(partitionID, bucketID, streamID, filter,
				changeSetID);
		final EventSet.Builder eventSetBuilder = eventSetBuilderFactory.create();
		eventSetBuilder.buildPartitionID(partitionID).buildBucketID(bucketID).buildStreamID(streamID);

		if (!entries.isEmpty()) {
			final Long startVersion = entries.get(0).getVersion();
			final Long endVersion = entries.get(entries.size() - 1).getVersion();
			return eventSetBuilder.buildFromVersion(startVersion).buildToVersion(endVersion)
					.buildEvents(deserialize(entries)).build();
		} else {
			return eventSetBuilder.buildFromVersion(0L).buildToVersion(0L).buildEvents(EMPTY).build();
		}
	}

	@Override
	public void putEventSet(final EventSet eventSet, final UUID changeSetID) {
		final List<EventStoreEntry> entries = new ArrayList<EventStoreEntry>();
		final EventStoreEntry.Builder builder = eventStoreEntryBuilderFactory.create();
		builder.buildPartitionID(eventSet.getPartitionID()).buildBucketID(eventSet.getBucketID())
				.buildStreamID(eventSet.getStreamID()).buildChangeSetID(changeSetID);
		Long version = eventSet.getFromVersion();
		for (final Serializable event : eventSet.getEvents()) {
			builder.buildVersion(version++).buildEventName(event.getClass().getName()).buildEvent(serialize(event));
			entries.add(builder.build());
		}
		eventStoreDAO.putEvents(Collections.unmodifiableList(entries));
	}

	private String serialize(final Serializable entry) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(entry);
		} catch (JsonProcessingException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}
}
