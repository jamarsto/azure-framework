package com.microsoft.azure.framework.eventstore.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.eventstore.persistence.EventSet;
import com.microsoft.azure.framework.eventstore.persistence.EventSetRepository;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class SimpleInputEventStream implements InputEventStream {

	public static final class Builder implements InputEventStream.Builder {
		private String bucketID;
		private UUID changeSetID;
		private Class<?> filter = Serializable.class;
		private Long fromVersion = 0L;
		private String partitionID;
		@Autowired
		private PreconditionService preconditionService;
		@Autowired
		private EventSetRepository eventSetRepository;
		private UUID streamID;
		private Long toVersion = Long.MAX_VALUE;

		@Override
		public InputEventStream build() throws IOException {
			preconditionService.requiresNotEmpty("Partition ID is required.", partitionID);
			preconditionService.requiresNotEmpty("Bucket ID is required.", bucketID);
			preconditionService.requiresNotNull("Stream ID is required.", streamID);
			preconditionService.requiresGE("From Version must greater than or equal to zero.", fromVersion, 0L);
			preconditionService.requiresGE("To Version must be greater than or equal to From Version", toVersion,
					fromVersion);
			preconditionService.requiresTrue("Change Set ID cannot be used with From Version or To Version",
					changeSetID == null || (fromVersion == 0L && toVersion == Long.MAX_VALUE));

			if (changeSetID != null) {
				fromVersion = 0L;
				toVersion = Long.MAX_VALUE;
			}

			return new SimpleInputEventStream(this);
		}

		@Override
		public Builder buildBucketID(final String bucketID) {
			preconditionService.requiresNotEmpty("Bucket ID is required.", bucketID);

			this.bucketID = bucketID;

			return this;
		}

		@Override
		public Builder buildChangeSetID(final UUID changeSetID) {
			preconditionService.requiresNotNull("Change Set ID is required.", changeSetID);
			preconditionService.requiresEQ("From Version cannot be used with Change Set ID.", fromVersion, 0L);
			preconditionService.requiresEQ("To Version cannot be used with Change Set ID.", toVersion, Long.MAX_VALUE);

			this.changeSetID = changeSetID;

			return this;
		}

		@Override
		public Builder buildFilter(final Class<?> filter) {
			if (filter == null) {
				this.filter = Serializable.class;
			} else {
				this.filter = filter;
			}

			return this;
		}

		@Override
		public Builder buildFromVersion(final Long fromVersion) {
			preconditionService.requiresGE("From Version must greater than or equal to zero.", fromVersion, 0L);
			preconditionService.requiresLE("From Version must less than or equal to To Version.", fromVersion,
					toVersion);
			preconditionService.requiresNull("Change Set ID cannot be used with From Version", changeSetID);

			this.fromVersion = fromVersion;

			return this;
		}

		@Override
		public Builder buildPartitionID(final String partitionID) {
			preconditionService.requiresNotEmpty("Partition ID is required.", partitionID);

			this.partitionID = partitionID;

			return this;
		}

		@Override
		public Builder buildStreamID(final UUID streamID) {
			preconditionService.requiresNotNull("Stream ID is required.", streamID);

			this.streamID = streamID;

			return this;
		}

		@Override
		public Builder buildToVersion(final Long toVersion) {
			preconditionService.requiresGE("To Version must greater than or equal to zero.", toVersion, 0L);
			preconditionService.requiresGE("To Version must greater than or equal to From Version.", toVersion,
					fromVersion);
			preconditionService.requiresNull("Change Set ID cannot be used with To Version", changeSetID);

			this.toVersion = toVersion;

			return this;
		}
	}

	@Component
	public static final class BuilderFactory implements InputEventStream.BuilderFactory {
		@Autowired
		private AutowireCapableBeanFactory autowireBeanFactory;

		@Override
		public Builder create() {
			final Builder builder = new Builder();
			autowireBeanFactory.autowireBean(builder);
			return builder;
		}
	}

	private final String bucketID;
	private final UUID changeSetID;
	private final Class<?> filter;
	private Long fromVersion;
	private Boolean isClosed;
	private Iterator<Serializable> iterator;
	private List<Serializable> list;
	private int numberConsumed = 0;
	private final String partitionID;
	private final PreconditionService preconditionService;
	private final EventSetRepository eventSetRepository;
	private final UUID streamID;
	private Long toVersion;

	public SimpleInputEventStream(final Builder builder) throws IOException {
		this.eventSetRepository = builder.eventSetRepository;
		this.preconditionService = builder.preconditionService;
		this.partitionID = builder.partitionID;
		this.bucketID = builder.bucketID;
		this.streamID = builder.streamID;
		this.fromVersion = builder.fromVersion;
		this.toVersion = builder.toVersion;
		this.changeSetID = builder.changeSetID;
		this.filter = builder.filter;
		this.isClosed = Boolean.FALSE;
		list = loadEvents();
		iterator = list.iterator();
	}

	@Override
	public int available() {
		return list.size() - numberConsumed;
	}

	@Override
	public void close() throws IOException {
		preconditionService.requiresFalse(IOException.class, "InputEventStream is closed.", isClosed);

		isClosed = Boolean.TRUE;
		list = null;
		iterator = null;
	}

	@Override
	public String getBucketID() {
		return bucketID;
	}

	@Override
	public Long getFromVersion() {
		return fromVersion;
	}

	@Override
	public String getPartitionID() {
		return partitionID;
	}

	@Override
	public UUID getStreamID() {
		return streamID;
	}

	@Override
	public Long getToVersion() {
		return toVersion;
	}

	private List<Serializable> loadEvents() throws IOException {
		try {
			EventSet eventSet = null;
			if (changeSetID != null) {
				eventSet = eventSetRepository.getEventSet(partitionID, bucketID, streamID, filter, changeSetID);
			} else {
				eventSet = eventSetRepository.getEventSet(partitionID, bucketID, streamID, filter, fromVersion,
						toVersion);
			}
			fromVersion = eventSet.getFromVersion();
			toVersion = eventSet.getToVersion();
			return eventSet.getEvents();
		} catch (final RuntimeException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public Serializable read() throws IOException {
		preconditionService.requiresFalse(IOException.class, "InputEventStream is closed.", isClosed);

		if (iterator.hasNext()) {
			numberConsumed++;
			return iterator.next();
		}

		return null;
	}

	@Override
	public List<Serializable> readAll() throws IOException {
		preconditionService.requiresFalse(IOException.class, "InputEventStream is closed.", isClosed);

		numberConsumed = list.size();

		return list;
	}
}
