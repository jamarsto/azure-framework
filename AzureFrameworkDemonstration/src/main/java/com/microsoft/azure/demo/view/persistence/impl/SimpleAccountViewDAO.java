package com.microsoft.azure.demo.view.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.impl.AccountBean;
import com.microsoft.azure.demo.view.persistence.AccountViewDAO;

@Component
public final class SimpleAccountViewDAO extends AbstractDAO implements AccountViewDAO {
	private static final String SELECT_CLAUSE = "SELECT ID, BALANCE FROM ACCOUNT_VIEW ";

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
		return getJdbcTemplate().queryForObject(SELECT_CLAUSE + "WHERE ID = ?",
				new Object[] { accountId.toString() }, new AccountBeanRowMapper());
	}

	@Override
	public List<AccountBean> getAccounts() {
		return getJdbcTemplate().queryForList(SELECT_CLAUSE, AccountBean.class,
				new AccountBeanRowMapper());
	}
}
