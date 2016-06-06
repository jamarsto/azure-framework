package com.microsoft.azure.demo.view.persistence.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("viewDataSource")
	public final void setDataSource(final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected final JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
