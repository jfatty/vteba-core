package com.vteba.tx.jdbc.datasource;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

import com.alibaba.druid.pool.DruidDataSource;
import com.vteba.service.multitenant.SchemaContextHolder;

public class VtebaDataSource implements DataSource, Referenceable, Closeable, Cloneable, ConnectionPoolDataSource {

	private ConcurrentMap<String, DruidDataSource> delegateDataSourceMap = new ConcurrentHashMap<String, DruidDataSource>();
	private PrintWriter logWriter;
	private int loginTimeoutSeconds;
	
	public void init() {
		
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.logWriter;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeoutSeconds = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeoutSeconds;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
			return (T) this;
		} else {
			return null;
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (iface.isAssignableFrom(DataSource.class)) {
			return true;
		} else {
			return false;
		}
	}

	private DruidDataSource getCurrentDataSource() {
		String schema = SchemaContextHolder.getSchema();
		DruidDataSource ds = delegateDataSourceMap.get(schema);
		return ds;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return getCurrentDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return getCurrentDataSource().getConnection(username, password);
	}

	@Override
	public PooledConnection getPooledConnection() throws SQLException {
		return getCurrentDataSource().getPooledConnection();
	}

	@Override
	public PooledConnection getPooledConnection(String user, String password)
			throws SQLException {
		return getCurrentDataSource().getPooledConnection(user, password);
	}

	@Override
	public void close() throws IOException {
		getCurrentDataSource().close();
	}

	@Override
	public Reference getReference() throws NamingException {
		return getCurrentDataSource().getReference();
	}

}
