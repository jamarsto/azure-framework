package com.microsoft.azure.demo.view.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.impl.TransactionBean;
import com.microsoft.azure.demo.view.persistence.TransactionViewDAO;

@Component
public final class SimpleTransactionViewDAO extends AbstractDAO implements TransactionViewDAO {
	private static final String SELECT_CLAUSE = "SELECT AGGREGATE_ID, ID, TRANSACTION_TYPE, TRANSACTION_CREATED, AMOUNT FROM TRANSACTION_VIEW ";

	private static final class TransactionBeanRowMapper implements RowMapper<TransactionBean> {
		@Override
		public TransactionBean mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final TransactionBean transactionBean = new TransactionBean();
			transactionBean.setAccountId(UUID.fromString(rs.getString("AGGREGATE_ID")));
			transactionBean.setId(UUID.fromString(rs.getString("ID")));
			transactionBean.setType(rs.getString("TRANSACTION_TYPE").charAt(0));
			final Calendar createdDateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			createdDateTime.setTime(rs.getTimestamp("TRANSACTION_CREATED"));
			transactionBean.setCreatedDateTime(createdDateTime);
			transactionBean.setAmount(rs.getBigDecimal("AMOUNT"));
			return transactionBean;
		}
	}

	@Override
	public TransactionBean getTransaction(final UUID accountId, final UUID transactionId) {
		return getJdbcTemplate().queryForObject(SELECT_CLAUSE + "WHERE AGGREGATE_ID = ? AND ID = ?",
				new Object[] { accountId.toString(), transactionId.toString() }, new TransactionBeanRowMapper());
	}

	@Override
	public List<TransactionBean> getTransactions(final UUID accountId) {
		return getJdbcTemplate().queryForList(SELECT_CLAUSE + "WHERE AGGREGATE_ID = ? ORDER BY TRANSACTION_CREATED",
				TransactionBean.class, new Object[] { accountId.toString() }, new TransactionBeanRowMapper());
	}
}
