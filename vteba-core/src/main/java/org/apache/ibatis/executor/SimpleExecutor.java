package org.apache.ibatis.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Clinton Begin
 */
public class SimpleExecutor extends BaseExecutor {

	public SimpleExecutor(Configuration configuration, Transaction transaction) {
		super(configuration, transaction);
	}

	public int doUpdate(MappedStatement ms, Object parameter)
			throws SQLException {
		Statement stmt = null;
		try {
			Configuration configuration = ms.getConfiguration();
			StatementHandler handler = configuration.newStatementHandler(this,
					ms, parameter, RowBounds.DEFAULT, null, null);
			stmt = prepareStatement(handler, ms.getStatementLog());
			return handler.update(stmt);
		} finally {
			closeStatement(stmt);
		}
	}

	public <E> List<E> doQuery(MappedStatement ms, Object parameter,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
			throws SQLException {
		Statement stmt = null;
		try {
			Configuration configuration = ms.getConfiguration();
			List<String> sqlList = boundSql.getSqlList();
			StatementHandler handler = configuration.newStatementHandler(
					wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
			stmt = prepareStatement(handler, ms.getStatementLog());
			return handler.<E> query(stmt, resultHandler);
		} finally {
			closeStatement(stmt);
		}
	}

	public List<BatchResult> doFlushStatements(boolean isRollback)
			throws SQLException {
		return Collections.emptyList();
	}

	private Statement prepareStatement(StatementHandler handler,
			Log statementLog) throws SQLException {
		Statement stmt;
		Connection connection = getConnection(statementLog);
		stmt = handler.prepare(connection);
		handler.parameterize(stmt);
		return stmt;
	}

}
