package com.microsoft.azure.demo.view.persistence.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.microsoft.azure.demo.view.bean.TransactionBean;
import com.microsoft.azure.demo.view.persistence.TransactionViewDAO;

@Component
public final class SimpleTransactionViewDAO extends AbstractDAO implements TransactionViewDAO {
	private static final String SELECT_CLAUSE = "SELECT AGGREGATE_ID, ID, TRANSACTION_TYPE, TRANSACTION_CREATED, AMOUNT FROM TRANSACTION_VIEW ";
	private static final String UNIQUE_QUERY = SELECT_CLAUSE + "WHERE AGGREGATE_ID = ? AND ID = ?";
	private static final String LIST_QUERY = SELECT_CLAUSE + "WHERE AGGREGATE_ID = ? ORDER BY TRANSACTION_CREATED";

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
		try {
			return getJdbcTemplate().queryForObject(UNIQUE_QUERY,
					new Object[] { accountId.toString(), transactionId.toString() }, new TransactionBeanRowMapper());
		} catch (final IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<TransactionBean> getTransactions(final UUID accountId) {
		final List<Map<String, Object>> results = getJdbcTemplate().queryForList(LIST_QUERY,
				new Object[] { accountId.toString() });
		final List<TransactionBean> transactionBeans = new ArrayList<TransactionBean>();
		for (final Map<String, Object> rs : results) {
			final TransactionBean transactionBean = new TransactionBean();
			transactionBean.setAccountId(UUID.fromString((String) rs.get("AGGREGATE_ID")));
			transactionBean.setId(UUID.fromString((String) rs.get("ID")));
			transactionBean.setType(((String) rs.get("TRANSACTION_TYPE")).charAt(0));
			final Calendar createdDateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			createdDateTime.setTime((Timestamp) rs.get("TRANSACTION_CREATED"));
			transactionBean.setCreatedDateTime(createdDateTime);
			transactionBean.setAmount((BigDecimal) rs.get("AMOUNT"));
			transactionBeans.add(transactionBean);
		}
		return transactionBeans;
	}
}
