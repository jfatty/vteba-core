package com.vteba.tx.jdbc.datasource;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.util.Histogram;
import com.vteba.service.tenant.SchemaWeightHolder;

public class VtebaDataSource implements DataSource, Referenceable, Closeable,
		Cloneable, ConnectionPoolDataSource, Serializable {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(VtebaDataSource.class);
	
	private ConcurrentMap<String, DruidDataSource> delegateDataSourceMap = new ConcurrentHashMap<String, DruidDataSource>();
	private int loginTimeout;// 登录超时，单位秒
	private String dbKey;// 标示唯一的数据源
	private DataSource defaultDataSource;

	// *****druid*******//
	private static final long serialVersionUID = 1L;

	public final static int DEFAULT_INITIAL_SIZE = 0;
	public final static int DEFAULT_MAX_ACTIVE_SIZE = 8;
	public final static int DEFAULT_MAX_IDLE = 8;
	public final static int DEFAULT_MIN_IDLE = 0;
	public final static int DEFAULT_MAX_WAIT = -1;
	public final static String DEFAULT_VALIDATION_QUERY = null; //
	public final static boolean DEFAULT_TEST_ON_BORROW = false;
	public final static boolean DEFAULT_TEST_ON_RETURN = false;
	public final static boolean DEFAULT_WHILE_IDLE = true;
	public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60 * 1000L;
	public static final long DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS = 30 * 1000;
	public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;

	/**
	 * The default value for {@link #getMinEvictableIdleTimeMillis}.
	 * 
	 * @see #getMinEvictableIdleTimeMillis
	 * @see #setMinEvictableIdleTimeMillis
	 */
	public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1000L * 60L * 30L;

	protected volatile boolean defaultAutoCommit = true;
	protected volatile Boolean defaultReadOnly;
	protected volatile Integer defaultTransactionIsolation;
	protected volatile String defaultCatalog = null;

	protected String name;

	protected volatile String username;
	protected volatile String password;
	protected volatile String jdbcUrl;
	protected volatile String driverClass;
	protected volatile ClassLoader driverClassLoader;
	protected volatile Properties connectProperties = new Properties();

	protected volatile PasswordCallback passwordCallback;
	protected volatile NameCallback userCallback;

	protected volatile int initialSize = DEFAULT_INITIAL_SIZE;
	protected volatile int maxActive = DEFAULT_MAX_ACTIVE_SIZE;
	protected volatile int minIdle = DEFAULT_MIN_IDLE;
	protected volatile int maxIdle = DEFAULT_MAX_IDLE;
	protected volatile long maxWait = DEFAULT_MAX_WAIT;

	protected volatile String validationQuery = DEFAULT_VALIDATION_QUERY;
	protected volatile int validationQueryTimeout = -1;
	private volatile boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
	private volatile boolean testOnReturn = DEFAULT_TEST_ON_RETURN;
	private volatile boolean testWhileIdle = DEFAULT_WHILE_IDLE;
	protected volatile boolean poolPreparedStatements = false;
	protected volatile boolean sharePreparedStatements = false;
	protected volatile int maxPoolPreparedStatementPerConnectionSize = 10;

	protected volatile boolean inited = false;

	protected PrintWriter logWriter = new PrintWriter(System.out);

	protected List<Filter> filters = new CopyOnWriteArrayList<Filter>();
	private boolean clearFiltersEnable = true;
	protected volatile ExceptionSorter exceptionSorter = null;

	protected Driver driver;

	protected volatile int queryTimeout;
	protected volatile int transactionQueryTimeout;

	protected AtomicLong createErrorCount = new AtomicLong();

	protected long createTimespan;

	protected volatile int maxWaitThreadCount = -1;

	protected volatile boolean accessToUnderlyingConnectionAllowed = true;

	protected volatile long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

	protected volatile int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

	protected volatile long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

	protected volatile boolean removeAbandoned;

	protected volatile long removeAbandonedTimeoutMillis = 300 * 1000;

	protected volatile boolean logAbandoned;

	protected volatile int maxOpenPreparedStatements = -1;

	protected volatile List<String> connectionInitSqls;

	protected volatile String dbType;

	protected volatile long timeBetweenConnectErrorMillis = DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

	protected volatile ValidConnectionChecker validConnectionChecker = null;

	protected final AtomicLong errorCount = new AtomicLong();
	protected final AtomicLong dupCloseCount = new AtomicLong();

	protected final Map<DruidPooledConnection, Object> activeConnections = new IdentityHashMap<DruidPooledConnection, Object>();
	protected final static Object PRESENT = new Object();

	protected long id;

	protected final Date createdTime = new Date();
	protected Date initedTime;

	protected int connectionErrorRetryAttempts = 30;

	protected boolean breakAfterAcquireFailure = false;

	protected long transactionThresholdMillis = 0L;

	protected final AtomicLong commitCount = new AtomicLong();
	protected final AtomicLong startTransactionCount = new AtomicLong();
	protected final AtomicLong rollbackCount = new AtomicLong();
	protected final AtomicLong cachedPreparedStatementHitCount = new AtomicLong();
	protected final AtomicLong preparedStatementCount = new AtomicLong();
	protected final AtomicLong closedPreparedStatementCount = new AtomicLong();
	protected final AtomicLong cachedPreparedStatementCount = new AtomicLong();
	protected final AtomicLong cachedPreparedStatementDeleteCount = new AtomicLong();
	protected final AtomicLong cachedPreparedStatementMissCount = new AtomicLong();

	protected final Histogram transactionHistogram = new Histogram(10, 100,
			1000, 10 * 1000, 100 * 1000);

	private boolean dupCloseLogEnable = false;

	private ObjectName objectName;

	protected final AtomicLong executeCount = new AtomicLong();

	protected volatile Throwable createError;
	protected volatile Throwable lastError;
	protected volatile long lastErrorTimeMillis;
	protected volatile Throwable lastCreateError;
	protected volatile long lastCreateErrorTimeMillis;

	protected boolean isOracle = false;

	protected boolean useOracleImplicitCache = true;

	protected ReentrantLock lock;
	protected Condition notEmpty;
	protected Condition empty;

	protected AtomicLong createCount = new AtomicLong();
	protected AtomicLong destroyCount = new AtomicLong();

	private Boolean useUnfairLock = null;

	private boolean useLocalSessionState = true;

	protected long timeBetweenLogStatsMillis;

	// ********druid********//

	public void init() throws SQLException {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(jdbcUrl);
		druidDataSource.setUsername(username);
		druidDataSource.setPassword(password);
		druidDataSource.setInitialSize(initialSize);
		druidDataSource.setMaxActive(maxActive);
		druidDataSource.setMinIdle(minIdle);
		druidDataSource.setMaxWait(maxWait);
		druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		druidDataSource.setValidationQuery(validationQuery);
		druidDataSource.setTestWhileIdle(testWhileIdle);
		druidDataSource.setTestOnBorrow(testOnBorrow);
		druidDataSource.setTestOnReturn(testOnReturn);
		druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		druidDataSource.setProxyFilters(filters);
		try {
			druidDataSource.init();
		} catch (SQLException e) {
			LOGGER.error("VtebaDataSource init error", e);
			druidDataSource.close();
            throw e;
		}
		delegateDataSourceMap.put("default", druidDataSource);
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
		this.loginTimeout = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeout;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
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
		String schema = SchemaWeightHolder.getSchema();
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
		for (Entry<String, DruidDataSource> entry : delegateDataSourceMap.entrySet()) {
			DruidDataSource ds = entry.getValue();
			if (ds != null) {
				ds.close();
			}
		}
	}

	@Override
	public Reference getReference() throws NamingException {
		return getCurrentDataSource().getReference();
	}

	public ConcurrentMap<String, DruidDataSource> getDelegateDataSourceMap() {
		return delegateDataSourceMap;
	}
	
	public void setDelegateDataSourceMap(
			ConcurrentMap<String, DruidDataSource> delegateDataSourceMap) {
		this.delegateDataSourceMap = delegateDataSourceMap;
	}
	
	//*******以下是数据源配置属性的getter和setter************//

	public boolean isDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public Boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public Integer getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	public void setDefaultTransactionIsolation(Integer defaultTransactionIsolation) {
		this.defaultTransactionIsolation = defaultTransactionIsolation;
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public ClassLoader getDriverClassLoader() {
		return driverClassLoader;
	}

	public void setDriverClassLoader(ClassLoader driverClassLoader) {
		this.driverClassLoader = driverClassLoader;
	}

	public Properties getConnectProperties() {
		return connectProperties;
	}

	public void setConnectProperties(Properties connectProperties) {
		this.connectProperties = connectProperties;
	}

	public PasswordCallback getPasswordCallback() {
		return passwordCallback;
	}

	public void setPasswordCallback(PasswordCallback passwordCallback) {
		this.passwordCallback = passwordCallback;
	}

	public NameCallback getUserCallback() {
		return userCallback;
	}

	public void setUserCallback(NameCallback userCallback) {
		this.userCallback = userCallback;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public int getValidationQueryTimeout() {
		return validationQueryTimeout;
	}

	public void setValidationQueryTimeout(int validationQueryTimeout) {
		this.validationQueryTimeout = validationQueryTimeout;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public boolean isSharePreparedStatements() {
		return sharePreparedStatements;
	}

	public void setSharePreparedStatements(boolean sharePreparedStatements) {
		this.sharePreparedStatements = sharePreparedStatements;
	}

	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(
			int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public boolean isInited() {
		return inited;
	}

	public void setInited(boolean inited) {
		this.inited = inited;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public boolean isClearFiltersEnable() {
		return clearFiltersEnable;
	}

	public void setClearFiltersEnable(boolean clearFiltersEnable) {
		this.clearFiltersEnable = clearFiltersEnable;
	}

	public ExceptionSorter getExceptionSorter() {
		return exceptionSorter;
	}

	public void setExceptionSorter(ExceptionSorter exceptionSorter) {
		this.exceptionSorter = exceptionSorter;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public int getTransactionQueryTimeout() {
		return transactionQueryTimeout;
	}

	public void setTransactionQueryTimeout(int transactionQueryTimeout) {
		this.transactionQueryTimeout = transactionQueryTimeout;
	}

	public AtomicLong getCreateErrorCount() {
		return createErrorCount;
	}

	public void setCreateErrorCount(AtomicLong createErrorCount) {
		this.createErrorCount = createErrorCount;
	}

	public long getCreateTimespan() {
		return createTimespan;
	}

	public void setCreateTimespan(long createTimespan) {
		this.createTimespan = createTimespan;
	}

	public int getMaxWaitThreadCount() {
		return maxWaitThreadCount;
	}

	public void setMaxWaitThreadCount(int maxWaitThreadCount) {
		this.maxWaitThreadCount = maxWaitThreadCount;
	}

	public boolean isAccessToUnderlyingConnectionAllowed() {
		return accessToUnderlyingConnectionAllowed;
	}

	public void setAccessToUnderlyingConnectionAllowed(
			boolean accessToUnderlyingConnectionAllowed) {
		this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public boolean isRemoveAbandoned() {
		return removeAbandoned;
	}

	public void setRemoveAbandoned(boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public long getRemoveAbandonedTimeoutMillis() {
		return removeAbandonedTimeoutMillis;
	}

	public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
		this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
	}

	public boolean isLogAbandoned() {
		return logAbandoned;
	}

	public void setLogAbandoned(boolean logAbandoned) {
		this.logAbandoned = logAbandoned;
	}

	public int getMaxOpenPreparedStatements() {
		return maxOpenPreparedStatements;
	}

	public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
		this.maxOpenPreparedStatements = maxOpenPreparedStatements;
	}

	public List<String> getConnectionInitSqls() {
		return connectionInitSqls;
	}

	public void setConnectionInitSqls(List<String> connectionInitSqls) {
		this.connectionInitSqls = connectionInitSqls;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public long getTimeBetweenConnectErrorMillis() {
		return timeBetweenConnectErrorMillis;
	}

	public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
		this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
	}

	public ValidConnectionChecker getValidConnectionChecker() {
		return validConnectionChecker;
	}

	public void setValidConnectionChecker(
			ValidConnectionChecker validConnectionChecker) {
		this.validConnectionChecker = validConnectionChecker;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getInitedTime() {
		return initedTime;
	}

	public void setInitedTime(Date initedTime) {
		this.initedTime = initedTime;
	}

	public int getConnectionErrorRetryAttempts() {
		return connectionErrorRetryAttempts;
	}

	public void setConnectionErrorRetryAttempts(int connectionErrorRetryAttempts) {
		this.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
	}

	public boolean isBreakAfterAcquireFailure() {
		return breakAfterAcquireFailure;
	}

	public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
		this.breakAfterAcquireFailure = breakAfterAcquireFailure;
	}

	public long getTransactionThresholdMillis() {
		return transactionThresholdMillis;
	}

	public void setTransactionThresholdMillis(long transactionThresholdMillis) {
		this.transactionThresholdMillis = transactionThresholdMillis;
	}

	public boolean isDupCloseLogEnable() {
		return dupCloseLogEnable;
	}

	public void setDupCloseLogEnable(boolean dupCloseLogEnable) {
		this.dupCloseLogEnable = dupCloseLogEnable;
	}

	public ObjectName getObjectName() {
		return objectName;
	}

	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
	}

	public Throwable getCreateError() {
		return createError;
	}

	public void setCreateError(Throwable createError) {
		this.createError = createError;
	}

	public Throwable getLastError() {
		return lastError;
	}

	public void setLastError(Throwable lastError) {
		this.lastError = lastError;
	}

	public long getLastErrorTimeMillis() {
		return lastErrorTimeMillis;
	}

	public void setLastErrorTimeMillis(long lastErrorTimeMillis) {
		this.lastErrorTimeMillis = lastErrorTimeMillis;
	}

	public Throwable getLastCreateError() {
		return lastCreateError;
	}

	public void setLastCreateError(Throwable lastCreateError) {
		this.lastCreateError = lastCreateError;
	}

	public long getLastCreateErrorTimeMillis() {
		return lastCreateErrorTimeMillis;
	}

	public void setLastCreateErrorTimeMillis(long lastCreateErrorTimeMillis) {
		this.lastCreateErrorTimeMillis = lastCreateErrorTimeMillis;
	}

	public boolean isOracle() {
		return isOracle;
	}

	public void setOracle(boolean isOracle) {
		this.isOracle = isOracle;
	}

	public boolean isUseOracleImplicitCache() {
		return useOracleImplicitCache;
	}

	public void setUseOracleImplicitCache(boolean useOracleImplicitCache) {
		this.useOracleImplicitCache = useOracleImplicitCache;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public void setLock(ReentrantLock lock) {
		this.lock = lock;
	}

	public Condition getNotEmpty() {
		return notEmpty;
	}

	public void setNotEmpty(Condition notEmpty) {
		this.notEmpty = notEmpty;
	}

	public Condition getEmpty() {
		return empty;
	}

	public void setEmpty(Condition empty) {
		this.empty = empty;
	}

	public AtomicLong getCreateCount() {
		return createCount;
	}

	public void setCreateCount(AtomicLong createCount) {
		this.createCount = createCount;
	}

	public AtomicLong getDestroyCount() {
		return destroyCount;
	}

	public void setDestroyCount(AtomicLong destroyCount) {
		this.destroyCount = destroyCount;
	}

	public Boolean getUseUnfairLock() {
		return useUnfairLock;
	}

	public void setUseUnfairLock(Boolean useUnfairLock) {
		this.useUnfairLock = useUnfairLock;
	}

	public boolean isUseLocalSessionState() {
		return useLocalSessionState;
	}

	public void setUseLocalSessionState(boolean useLocalSessionState) {
		this.useLocalSessionState = useLocalSessionState;
	}

	public long getTimeBetweenLogStatsMillis() {
		return timeBetweenLogStatsMillis;
	}

	public void setTimeBetweenLogStatsMillis(long timeBetweenLogStatsMillis) {
		this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
	}

	public AtomicLong getErrorCount() {
		return errorCount;
	}

	public AtomicLong getDupCloseCount() {
		return dupCloseCount;
	}

	public Map<DruidPooledConnection, Object> getActiveConnections() {
		return activeConnections;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public AtomicLong getCommitCount() {
		return commitCount;
	}

	public AtomicLong getStartTransactionCount() {
		return startTransactionCount;
	}

	public AtomicLong getRollbackCount() {
		return rollbackCount;
	}

	public AtomicLong getCachedPreparedStatementHitCount() {
		return cachedPreparedStatementHitCount;
	}

	public AtomicLong getPreparedStatementCount() {
		return preparedStatementCount;
	}

	public AtomicLong getClosedPreparedStatementCount() {
		return closedPreparedStatementCount;
	}

	public AtomicLong getCachedPreparedStatementCount() {
		return cachedPreparedStatementCount;
	}

	public AtomicLong getCachedPreparedStatementDeleteCount() {
		return cachedPreparedStatementDeleteCount;
	}

	public AtomicLong getCachedPreparedStatementMissCount() {
		return cachedPreparedStatementMissCount;
	}

	public Histogram getTransactionHistogram() {
		return transactionHistogram;
	}

	public AtomicLong getExecuteCount() {
		return executeCount;
	}

	// 以上是数据源配置属性的getter和setter
	
	public String getDbKey() {
		return dbKey;
	}
	
	public void setDbKey(String dbKey) {
		this.dbKey = dbKey;
	}

	public DataSource getDefaultDataSource() {
		return defaultDataSource;
	}

	public void setDefaultDataSource(DataSource defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}
	
}
