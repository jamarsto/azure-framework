package com.microsoft.azure.framework.viewmanager;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@NamedQueries({ @NamedQuery(name = "ViewManager.getVersion", query = "SELECT v from ViewVersion v "
		+ "WHERE v.aggregateClassName = :aggregateName AND v.viewName = :viewName AND v.aggregateId = :aggregateID") })
@Entity
@Table(name = "VIEW_VERSION")
public final class ViewVersion {
	@Column(name = "AGGREGATE_NAME", updatable = false)
	private String aggregateClassName;
	@Column(name = "AGGREGATE_ID", updatable = false)
	private String aggregateId;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", columnDefinition = "uniqueidentifier", insertable = false, updatable = false)
	private UUID id;
	@Version
	@Column(name = "REVISION")
	private Long revision;
	@Column(name = "VERSION")
	private Long version;
	@Column(name = "VIEW_NAME", updatable = false)
	private String viewName;

	public ViewVersion() {
	}

	public ViewVersion(final String aggregateClassName, final String viewName, final UUID aggregateId) {
		this.aggregateClassName = aggregateClassName;
		this.viewName = viewName;
		this.aggregateId = aggregateId.toString();
		this.version = 0L;
	}

	public String getAggregateClassName() {
		return aggregateClassName;
	}

	public UUID getAggregateId() {
		return UUID.fromString(aggregateId);
	}

	public UUID getId() {
		return id;
	}

	public Long getVersion() {
		return version;
	}

	public String getViewName() {
		return viewName;
	}

	public void setVersion(final Long version) {
		this.version = version;
	}
}
