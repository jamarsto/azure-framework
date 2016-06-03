package com.microsoft.azure.framework.domain.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.azure.framework.domain.service.DateTimeService;

@Component
public final class SimpleDateTimeService implements DateTimeService {
	@PersistenceContext(unitName = "event-store-unit")
	private EntityManager entityManager;

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Calendar getUTCDateTime() {
		final Query query = entityManager.createNamedQuery("RDBMS.getDateTime");
		final Timestamp result = (Timestamp) query.getSingleResult();
		final Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		dateTime.setTimeInMillis(result.getTime());
		return dateTime;
	}

}
