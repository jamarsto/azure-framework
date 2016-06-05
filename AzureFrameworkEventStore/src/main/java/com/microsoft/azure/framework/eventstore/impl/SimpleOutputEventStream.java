package com.microsoft.azure.framework.eventstore.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.eventstore.ConcurrentUpdateException;
import com.microsoft.azure.framework.eventstore.OutputEventStream;
import com.microsoft.azure.framework.eventstore.persistence.EventSet;
import com.microsoft.azure.framework.eventstore.persistence.EventSetRepository;
import com.microsoft.azure.framework.precondition.PreconditionService;

public final class SimpleOutputEventStream implements OutputEventStream {

	public static final class Builder implements OutputEventStream.Builder {
		private String bucketID;
		@Autowired
		private EventSet.BuilderFactory eventSetBuilderFactory;
		@Autowired
		private EventSetRepository eventSetRepository;
		private Long fromVersion = 0L;
		@Autowired
		private PreconditionService preconditionService;
		private UUID streamID;

		@Override
		public OutputEventStream build() {
			preconditionService.requiresNotEmpty("Bucket ID is required.", bucketID);
			preconditionService.requiresNotNull("Stream ID is required.", streamID);
			preconditionService.requiresGE("From Version must greater than or equal to zero.", fromVersion, 0L);

			return new SimpleOutputEventStream(this);
		}

		@Override
		public Builder buildBucketID(final String bucketID) {
			preconditionService.requiresNotEmpty("Bucket ID is required.", bucketID);

			this.bucketID = bucketID;

			return this;
		}

		@Override
		public Builder buildFromVersion(final Long fromVersion) {
			preconditionService.requiresGE("From Version must greater than or equal to zero.", fromVersion, 0L);

			this.fromVersion = fromVersion;

			return this;
		}

		@Override
		public Builder buildStreamID(final UUID streamID) {
			preconditionService.requiresNotNull("Stream ID is required.", streamID);

			this.streamID = streamID;

			return this;
		}
	}

	@Component
	public static final class BuilderFactory implements OutputEventStream.BuilderFactory {
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
	private final List<Serializable> events;
	private final EventSet.Builder eventSetBuilder;
	private final EventSetRepository eventSetRepository;
	private Long fromVersion;
	private Boolean isClosed;
	private final PreconditionService preconditionService;
	private final UUID streamID;

	private SimpleOutputEventStream(final Builder builder) {
		this.bucketID = builder.bucketID;
		this.streamID = builder.streamID;
		this.fromVersion = builder.fromVersion;
		this.preconditionService = builder.preconditionService;
		this.eventSetRepository = builder.eventSetRepository;
		this.eventSetBuilder = builder.eventSetBuilderFactory.create();
		this.eventSetBuilder.buildBucketID(bucketID).buildStreamID(streamID)
				.buildFromVersion(fromVersion);
		this.events = new ArrayList<Serializable>();
		this.isClosed = Boolean.FALSE;
	}

	@Override
	public void close() throws IOException {
		preconditionService.requiresFalse(IOException.class, "OutputEventStream is closed.", isClosed);

		isClosed = Boolean.TRUE;
	}

	@Override
	public void flush(final UUID changeSetID) throws IOException {
		preconditionService.requiresFalse(IOException.class, "OutputEventStream is closed.", isClosed);

		try {
			eventSetBuilder.buildToVersion(fromVersion + events.size());
			eventSetBuilder.buildEvents(Collections.unmodifiableList(events));
			eventSetRepository.putEventSet(eventSetBuilder.build(), changeSetID);
			fromVersion += events.size();
			eventSetBuilder.buildFromVersion(fromVersion);
			events.clear();
		} catch (final RuntimeException e) {
			if(e instanceof RollbackException && e.getCause() instanceof EntityExistsException) {
				throw new ConcurrentUpdateException("Concurrent Update Exception");
			}
			if(e instanceof EntityExistsException) {
				throw new ConcurrentUpdateException("Concurrent Update Exception");
			}
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public String getBucketID() {
		return bucketID;
	}

	@Override
	public UUID getStreamID() {
		return streamID;
	}

	@Override
	public void write(final Collection<Serializable> events) throws IOException {
		preconditionService.requiresFalse(IOException.class, "OutputEventStream is closed.", isClosed);
		preconditionService.requiresNotNull("Events are required.", events);

		this.events.addAll(events);
	}

	@Override
	public void write(final Serializable event) throws IOException {
		preconditionService.requiresFalse(IOException.class, "OutputEventStream is closed.", isClosed);
		preconditionService.requiresNotNull("Event is required.", event);

		this.events.add(event);
	}

	@Override
	public void write(final Serializable[] events) throws IOException {
		preconditionService.requiresFalse(IOException.class, "OutputEventStream is closed.", isClosed);
		preconditionService.requiresNotNull("Events are required.", events);

		this.events.addAll(Arrays.asList(events));
	}
}
