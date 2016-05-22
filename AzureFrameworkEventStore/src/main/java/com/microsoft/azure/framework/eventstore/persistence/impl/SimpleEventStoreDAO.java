package com.microsoft.azure.framework.eventstore.persistence.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.azure.framework.eventstore.persistence.EventStoreDAO;
import com.microsoft.azure.framework.eventstore.persistence.EventStoreEntry;

@Component
public final class SimpleEventStoreDAO implements EventStoreDAO {

	@PersistenceContext(unitName = "event-store-unit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<EventStoreEntry> getEvents(final String partitionID, final String bucketID, final UUID streamID,
			final Class<?> filter, final Long fromVersion, final Long toVersion) {
		if (filter.getName().equals(Serializable.class.getName())) {
			final Query query = entityManager.createNamedQuery("EventStoreEntry.versionRange");
			query.setParameter("partitionID", partitionID).setParameter("bucketID", bucketID)
					.setParameter("streamID", streamID).setParameter("fromVersion", fromVersion)
					.setParameter("toVersion", toVersion);
			return Collections.unmodifiableList(query.getResultList());
		} else {
			if (fromVersion.equals(Long.MAX_VALUE) && toVersion.equals(Long.MAX_VALUE)) {
				final Query query = entityManager.createNamedQuery("EventStoreEntry.classNameHighestVersion");
				query.setParameter("partitionID", partitionID).setParameter("bucketID", bucketID)
						.setParameter("streamID", streamID).setParameter("eventClassName", filter.getName());
				query.setMaxResults(1);
				return Collections.unmodifiableList(query.getResultList());
			} else {
				final Query query = entityManager.createNamedQuery("EventStoreEntry.classNameAndVersionRange");
				query.setParameter("partitionID", partitionID).setParameter("bucketID", bucketID)
						.setParameter("streamID", streamID).setParameter("eventClassName", filter.getName())
						.setParameter("fromVersion", fromVersion).setParameter("toVersion", toVersion);
				return Collections.unmodifiableList(query.getResultList());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<EventStoreEntry> getEvents(final String partitionID, final String bucketID, final UUID streamID,
			final Class<?> filter, final UUID changeSetID) {
		if (filter.getName().equals(Serializable.class.getName())) {
			final Query query = entityManager.createNamedQuery("EventStoreEntry.changeSetID");
			query.setParameter("partitionID", partitionID).setParameter("bucketID", bucketID)
					.setParameter("streamID", streamID).setParameter("changeSetID", changeSetID);
			return Collections.unmodifiableList(query.getResultList());
		} else {
			final Query query = entityManager.createNamedQuery("EventStoreEntry.classNameAndChangeSetID");
			query.setParameter("partitionID", partitionID).setParameter("bucketID", bucketID)
					.setParameter("streamID", streamID).setParameter("eventClassName", filter.getName())
					.setParameter("changeSetID", changeSetID);
			return Collections.unmodifiableList(query.getResultList());
		}
	}

	@Transactional
	@Override
	public void putEvents(final List<EventStoreEntry> entries) {
		for (final EventStoreEntry entry : entries) {
			entityManager.persist(entry);
		}
		entityManager.flush();
	}
}
