package com.microsoft.azure.demo.view.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.AccountBean;
import com.microsoft.azure.demo.view.persistence.AccountViewDAO;

@Component
public final class SimpleAccountViewDAO extends AbstractDAO implements AccountViewDAO {
	private static final String SELECT_CLAUSE = "SELECT ID, BALANCE FROM ACCOUNT_VIEW ";
	private static final String UNIQUE_QUERY = SELECT_CLAUSE + "WHERE ID = ?";
	private static final String LIST_QUERY = SELECT_CLAUSE + "ORDER BY ID";

	private static final class AccountBeanRowMapper implements RowMapper<AccountBean> {
		@Override
		public AccountBean mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final AccountBean accountBean = new AccountBean();
			accountBean.setId(UUID.fromString(rs.getString("ID")));
			accountBean.setBalance(rs.getBigDecimal("BALANCE"));
			return accountBean;
		}
	}

	@Override
	public AccountBean getAccount(final UUID accountId) {
		return getJdbcTemplate().queryForObject(UNIQUE_QUERY, new Object[] { accountId.toString() },
				new AccountBeanRowMapper());
	}

	@Override
	public List<AccountBean> getAccounts() {
		return getJdbcTemplate().queryForList(LIST_QUERY, AccountBean.class, new AccountBeanRowMapper());
	}
}
