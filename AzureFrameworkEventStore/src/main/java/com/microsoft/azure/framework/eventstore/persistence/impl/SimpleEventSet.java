package com.microsoft.azure.framework.eventstore.persistence.impl;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.eventstore.persistence.EventSet;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class SimpleEventSet implements EventSet {
	public static final class Builder implements EventSet.Builder {
		private String bucketID;
		private List<Serializable> events;
		private Long fromVersion = 0L;
		private String partitionID;
		private final PreconditionService preconditionService;
		private UUID streamID;
		private Long toVersion = Long.MAX_VALUE;

		private Builder(final PreconditionService preconditionService) {
			this.preconditionService = preconditionService;
		}

		@Override
		public EventSet build() {
			return new SimpleEventSet(this);
		}

		@Override
		public Builder buildBucketID(String bucketID) {
			preconditionService.requiresNotEmpty("Bucket ID is required.", bucketID);

			this.bucketID = bucketID;

			return this;
		}

		@Override
		public Builder buildEvents(List<Serializable> events) {
			preconditionService.requiresNotNull("Events List is required.", events);

			this.events = events;

			return this;
		}

		@Override
		public Builder buildFromVersion(Long fromVersion) {
			preconditionService.requiresGE("From Version must greater than or equal to zero.", fromVersion, 0L);
			preconditionService.requiresLE("From Version must less than or equal to To Version.", fromVersion, toVersion);

			this.fromVersion = fromVersion;

			return this;
		}

		@Override
		public Builder buildPartitionID(String partitionID) {
			preconditionService.requiresNotEmpty("Partition ID is required.", partitionID);

			this.partitionID = partitionID;

			return this;
		}

		@Override
		public Builder buildStreamID(UUID streamID) {
			preconditionService.requiresNotNull("Stream ID is required.", streamID);

			this.streamID = streamID;

			return this;
		}

		@Override
		public Builder buildToVersion(Long toVersion) {
			preconditionService.requiresGE("To Version must greater than or equal to zero.", toVersion, 0L);
			preconditionService.requiresGE("To Version must greater than or equal to From Version.", toVersion, fromVersion);

			this.toVersion = toVersion;

			return this;
		}
	}

	@Component
	public static final class BuilderFactory implements EventSet.BuilderFactory {
		@Autowired
		private PreconditionService preconditionService;

		@Override
		public Builder create() {
			return new Builder(preconditionService);
		}
	}

	private final String bucketID;
	private final List<Serializable> events;
	private final Long fromVersion;
	private final String partitionID;
	private final UUID streamID;
	private final Long toVersion;

	private SimpleEventSet(final Builder builder) {
		this.partitionID = builder.partitionID;
		this.bucketID = builder.bucketID;
		this.streamID = builder.streamID;
		this.fromVersion = builder.fromVersion;
		this.toVersion = builder.toVersion;
		this.events = builder.events;
	}

	@Override
	public String getBucketID() {
		return bucketID;
	}

	@Override
	public List<Serializable> getEvents() {
		return events;
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
}
