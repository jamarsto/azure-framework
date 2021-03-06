package com.microsoft.azure.framework.eventstore.persistence;

import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.precondition.PreconditionService;

@NamedQueries({
		@NamedQuery(name = "EventStoreEntry.classNameHighestVersion", query = "SELECT e FROM EventStoreEntry e "
				+ "WHERE e.bucketID = :bucketID AND e.streamID = :streamID "
				+ "AND e.eventClassName = :eventClassName " 
				+ "ORDER BY e.version DESC"),
		@NamedQuery(name = "EventStoreEntry.classNameAndVersionRange", query = "SELECT e FROM EventStoreEntry e "
				+ "WHERE e.bucketID = :bucketID AND e.streamID = :streamID "
				+ "AND e.eventClassName = :eventClassName AND e.version >= :fromVersion AND e.version <= :toVersion "
				+ "ORDER BY e.version"),
		@NamedQuery(name = "EventStoreEntry.classNameAndChangeSetID", query = "SELECT e FROM EventStoreEntry e "
				+ "WHERE e.bucketID = :bucketID AND e.streamID = :streamID "
				+ "AND e.eventClassName = :eventClassName AND e.changeSetID = :changeSetID "
				+ "ORDER BY e.version"),
		@NamedQuery(name = "EventStoreEntry.changeSetID", query = "SELECT e FROM EventStoreEntry e "
				+ "WHERE e.bucketID = :bucketID AND e.streamID = :streamID "
				+ "AND e.changeSetID = :changeSetID "
				+ "ORDER BY e.version"),
		@NamedQuery(name = "EventStoreEntry.versionRange", query = "SELECT e FROM EventStoreEntry e "
				+ "WHERE e.bucketID = :bucketID AND e.streamID = :streamID "
				+ "AND e.version >= :fromVersion AND e.version <= :toVersion " 
				+ "ORDER BY e.version") })
@Entity
@Table(name = "EVENT_STORE")
public class EventStoreEntry {
	
	public static final class Builder {
		private String bucketID;
		private UUID changeSetID;
		private String event;
		private String eventClassName;
		@Autowired
		private PreconditionService precondition;
		private UUID streamID;
		private Long version;

		public EventStoreEntry build() {
			precondition.requiresNotEmpty("Bucket ID is required.", bucketID);
			precondition.requiresNotNull("Stream ID is required.", streamID);
			precondition.requiresNotNull("Version is required.", version);
			precondition.requiresNotNull("Change Set ID is required.", changeSetID);
			precondition.requiresNotEmpty("Event is required.", event);
			precondition.requiresNotEmpty("Event Class Name is required.", eventClassName);

			return new EventStoreEntry(this);
		}

		public Builder buildBucketID(final String bucketID) {
			precondition.requiresNotEmpty("Bucket ID is required.", bucketID);

			this.bucketID = bucketID;

			return this;
		}

		public Builder buildChangeSetID(final UUID changeSetID) {
			precondition.requiresNotNull("Change Set ID is required.", changeSetID);

			this.changeSetID = changeSetID;

			return this;
		}

		public Builder buildEvent(final String event) {
			precondition.requiresNotEmpty("Event is required.", event);

			this.event = event;

			return this;
		}

		public Builder buildEventName(final String eventClassName) {
			precondition.requiresNotEmpty("Event Class Name is required.", eventClassName);

			this.eventClassName = eventClassName;

			return this;
		}

		public Builder buildStreamID(final UUID streamID) {
			precondition.requiresNotNull("Stream ID is required.", streamID);

			this.streamID = streamID;

			return this;
		}

		public Builder buildVersion(final Long version) {
			precondition.requiresNotNull("Version is required.", version);

			this.version = version;

			return this;
		}
	}

	@Component
	public static final class BuilderFactory {
		@Autowired
		private AutowireCapableBeanFactory autowireBeanFactory;

		public Builder create() {
			final Builder builder = new Builder();
			autowireBeanFactory.autowireBean(builder);
			return builder;
		}
	}

	@Column(name = "BUCKET_ID", updatable = false)
	private String bucketID;
	@Column(name = "CHANGE_SET_ID", updatable = false)
	private String changeSetID;
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "EVENT_CREATED", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar created;
	@Column(name = "EVENT", updatable = false)
	private String event;
	@Column(name = "EVENT_CLASS_NAME", updatable = false)
	private String eventClassName;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", columnDefinition = "uniqueidentifier", insertable = false, updatable = false)
	private UUID id;
	@Column(name = "STREAM_ID", updatable = false)
	private String streamID;
	@Column(name = "EVENT_VERSION", updatable = false)
	private Long version;

	public EventStoreEntry() {
	}

	private EventStoreEntry(Builder builder) {
		this.bucketID = builder.bucketID;
		this.streamID = builder.streamID.toString();
		this.version = builder.version;
		this.changeSetID = builder.changeSetID.toString();
		this.event = builder.event;
		this.eventClassName = builder.eventClassName;
	}

	public String getBucketID() {
		return bucketID;
	}

	public Calendar getCreated() {
		return created;
	}

	public String getEvent() {
		return event;
	}

	public String getEventClassName() {
		return eventClassName;
	}

	public UUID getId() {
		return id;
	}

	public UUID getStreamID() {
		return UUID.fromString(streamID);
	}

	public Long getVersion() {
		return version;
	}
}
