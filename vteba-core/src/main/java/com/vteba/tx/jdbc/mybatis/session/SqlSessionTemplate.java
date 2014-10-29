package com.vteba.tx.jdbc.mybatis.session;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.closeSqlSession;
import static org.mybatis.spring.SqlSessionUtils.getSqlSession;
import static org.mybatis.spring.SqlSessionUtils.isSqlSessionTransactional;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
//import org.mybatis.spring.SqlSessionInterceptor;
//import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import com.vteba.service.tenant.SchemaHolder;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.converter.SqlConvertFactory;
import com.vteba.tx.jdbc.mybatis.converter.internal.TemplateSqlConvertFactory;

/**
 * Thread safe, Spring managed, {@code SqlSession} that works with Spring
 * transaction management to ensure that that the actual SqlSession used is the
 * one associated with the current Spring transaction. In addition, it manages
 * the session life-cycle, including closing, committing or rolling back the
 * session as necessary based on the Spring transaction configuration.
 * <p>
 * The template needs a SqlSessionFactory to create SqlSessions, passed as a
 * constructor argument. It also can be constructed indicating the executor type
 * to be used, if not, the default executor type, defined in the session factory
 * will be used.
 * <p>
 * This template converts MyBatis PersistenceExceptions into unchecked
 * DataAccessExceptions, using, by default, a {@code MyBatisExceptionTranslator}.
 * <p>
 * Because SqlSessionTemplate is thread safe, a single instance can be shared by
 * all DAOs; there should also be a small memory savings by doing this. This
 * pattern can be used in Spring configuration files as follows:
 *
 * <pre class="code">
 * {@code
 * <bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
 *   <constructor-arg ref="sqlSessionFactory" />
 * </bean>
 * }
 * </pre>
 * <p>主要的调整在于，可以从当前线程获取绑定的schema。然后获取对应的SqlSessionFactory和Configuration等。
 * 该SqlSessionTemplate同时持有多个SqlSessionFactory。
 * 事务仍然切在Service层，跨库操作时，会开启两个子事务，如果其中一个事务出现异常，那么两个事务都会回滚的。有测试过。
 * 
 * @author Putthibong Boonbong
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * @author 尹雷
 * 
 * @see SqlSessionFactory
 * @see MyBatisExceptionTranslator
 * @version $Id$
 */
public class SqlSessionTemplate implements SqlSession {
	
//	private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionTemplate.class);
	private static final ConcurrentMap<String, Boolean> NEED_PARSE_CACHE = new ConcurrentHashMap<String, Boolean>();
	
	private Map<String, SqlSessionFactory> proxySqlSessionFactory;

	private SqlSessionFactory sqlSessionFactory;

	private final ExecutorType executorType;

	private final SqlSession sqlSessionProxy;

	private final PersistenceExceptionTranslator exceptionTranslator;

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory}
	 * provided as an argument.
	 *
	 * @param sqlSessionFactory
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this(sqlSessionFactory, sqlSessionFactory.getConfiguration()
				.getDefaultExecutorType());
	}

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory}
	 * provided as an argument and the given {@code ExecutorType}
	 * {@code ExecutorType} cannot be changed once the
	 * {@code SqlSessionTemplate} is constructed.
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory,
			ExecutorType executorType) {
		this(sqlSessionFactory, executorType, new MyBatisExceptionTranslator(
				sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true));
	}

	/**
	 * Constructs a Spring managed {@code SqlSession} with the given
	 * {@code SqlSessionFactory} and {@code ExecutorType}. A custom
	 * {@code SQLExceptionTranslator} can be provided as an argument so any
	 * {@code PersistenceException} thrown by MyBatis can be custom translated
	 * to a {@code RuntimeException} The {@code SQLExceptionTranslator} can also
	 * be null and thus no exception translation will be done and MyBatis
	 * exceptions will be thrown
	 *
	 * @param sqlSessionFactory
	 * @param executorType
	 * @param exceptionTranslator
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory,
			ExecutorType executorType,
			PersistenceExceptionTranslator exceptionTranslator) {

		notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
		notNull(executorType, "Property 'executorType' is required");

		this.sqlSessionFactory = sqlSessionFactory;
		this.executorType = executorType;
		this.exceptionTranslator = exceptionTranslator;
		this.sqlSessionProxy = (SqlSession) newProxyInstance(
				SqlSessionFactory.class.getClassLoader(),
				new Class[] { SqlSession.class }, new SqlSessionInterceptor());
	}

	public SqlSessionFactory getSqlSessionFactory() {
		String schema = SchemaHolder.getSchema();
		if (schema == null) {// 如果没有，返回默认的SqlSessionFactory
			return this.sqlSessionFactory;
		} else {
			return proxySqlSessionFactory.get(schema);
		}
		
//		return this.sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public ExecutorType getExecutorType() {
		return this.executorType;
	}

	public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
		return this.exceptionTranslator;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T selectOne(String statement) {
		// return this.sqlSessionProxy.<T> selectOne(statement);
		return this.selectOne(statement, null);// DefaultSqlSession的实现方式
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T selectOne(String statement, Object parameter) {
		// return this.sqlSessionProxy.<T> selectOne(statement, parameter);
		List<T> list = this.selectList(statement, parameter);
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		//return this.sqlSessionProxy.<K, V> selectMap(statement, mapKey);
		return this.selectMap(statement, null, mapKey, RowBounds.DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 */
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		//return this.sqlSessionProxy.<K, V> selectMap(statement, parameter, mapKey);
		return this.selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 * <p>这个方法时需要被重载的。解析sql语句，获取逻辑包的分区策略。
	 * 替换逻辑表，根据参数路由查询的具体表
	 */
	public <K, V> Map<K, V> selectMap(String statement, Object parameter,
			String mapKey, RowBounds rowBounds) {
		resolveSQL(statement, parameter);
		return this.sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> List<E> selectList(String statement) {
		//return this.sqlSessionProxy.<E> selectList(statement);
		return this.selectList(statement, null, RowBounds.DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 */
	public <E> List<E> selectList(String statement, Object parameter) {
		//return this.sqlSessionProxy.<E> selectList(statement, parameter);
		return this.selectList(statement, parameter, RowBounds.DEFAULT);
	}

	/**
	 * {@inheritDoc}
	 * <p>这个方法时需要被重载的
	 */
	public <E> List<E> selectList(String statement, Object parameter,
			RowBounds rowBounds) {
		resolveSQL(statement, parameter);
		return this.sqlSessionProxy.selectList(statement, parameter, rowBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public void select(String statement, ResultHandler handler) {
		//this.sqlSessionProxy.select(statement, handler);
		this.select(statement, null, RowBounds.DEFAULT, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public void select(String statement, Object parameter, ResultHandler handler) {
		//this.sqlSessionProxy.select(statement, parameter, handler);
		this.select(statement, parameter, RowBounds.DEFAULT, handler);
	}

	/**
	 * {@inheritDoc}
	 * <p>这个方法时需要被重载的
	 */
	public void select(String statement, Object parameter, RowBounds rowBounds,
			ResultHandler handler) {
		resolveSQL(statement, parameter);
		this.sqlSessionProxy.select(statement, parameter, rowBounds, handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(String statement) {
		//return this.sqlSessionProxy.insert(statement);
		return this.insert(statement, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public int insert(String statement, Object parameter) {
		//return this.sqlSessionProxy.insert(statement, parameter);
		return this.update(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public int update(String statement) {
		//return this.sqlSessionProxy.update(statement);
		return this.update(statement, null);
	}

	/**
	 * {@inheritDoc}
	 * <p>这个方法时需要被重载的
	 */
	public int update(String statement, Object parameter) {
		resolveSQL(statement, parameter);
		return this.sqlSessionProxy.update(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public int delete(String statement) {
		//return this.sqlSessionProxy.delete(statement);
		return this.update(statement, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public int delete(String statement, Object parameter) {
		//return this.sqlSessionProxy.delete(statement, parameter);
		return this.update(statement, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T getMapper(Class<T> type) {
		return getConfiguration().getMapper(type, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		throw new UnsupportedOperationException(
				"Manual commit is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit(boolean force) {
		throw new UnsupportedOperationException(
				"Manual commit is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() {
		throw new UnsupportedOperationException(
				"Manual rollback is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback(boolean force) {
		throw new UnsupportedOperationException(
				"Manual rollback is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		throw new UnsupportedOperationException(
				"Manual close is not allowed over a Spring managed SqlSession");
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearCache() {
		this.sqlSessionProxy.clearCache();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>这里的配置需要做切换的。SqlSessionFactory和Configuration是一一对应的。
	 */
	public Configuration getConfiguration() {
		//return this.sqlSessionFactory.getConfiguration();
		return getSqlSessionFactory().getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() {
		return this.sqlSessionProxy.getConnection();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.0.2
	 *
	 */
	public List<BatchResult> flushStatements() {
		return this.sqlSessionProxy.flushStatements();
	}

	/**
	 * Proxy needed to route MyBatis method calls to the proper SqlSession got
	 * from Spring's Transaction Manager It also unwraps exceptions thrown by
	 * {@code Method#invoke(Object, Object...)} to pass a
	 * {@code PersistenceException} to the
	 * {@code PersistenceExceptionTranslator}.
	 */
	private class SqlSessionInterceptor implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			SqlSession sqlSession = getSqlSession(getSqlSessionFactory(), executorType, exceptionTranslator);
			try {
				Object result = method.invoke(sqlSession, args);
				if (!isSqlSessionTransactional(sqlSession, getSqlSessionFactory())) {
					// force commit even on non-dirty sessions because some
					// databases require
					// a commit/rollback before calling close()
					sqlSession.commit(true);
				}
				return result;
			} catch (Throwable t) {
				Throwable unwrapped = unwrapThrowable(t);
				if (exceptionTranslator != null && unwrapped instanceof PersistenceException) {
					// release the connection to avoid a deadlock if the
					// translator is no loaded. See issue #22
					closeSqlSession(sqlSession, getSqlSessionFactory());
					sqlSession = null;
					Throwable translated = exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
					if (translated != null) {
						unwrapped = translated;
					}
				}
				throw unwrapped;
			} finally {
				if (sqlSession != null) {
					closeSqlSession(sqlSession, getSqlSessionFactory());
				}
			}
		}
	}

	public Map<String, SqlSessionFactory> getProxySqlSessionFactory() {
		return proxySqlSessionFactory;
	}

	public void setProxySqlSessionFactory(
			Map<String, SqlSessionFactory> proxySqlSessionFactory) {
		this.proxySqlSessionFactory = proxySqlSessionFactory;
	}

	/**
	 * 解析sql，替换逻辑表名，根据表名的分区策略，组成新的sql语句。
	 * @param mapperId mybatis sql 映射id
	 * @param params sql语句参数
	 */
	public void resolveSQL(String mapperId, Object params) {
		MappedStatement mappedStatement = getConfiguration().getMappedStatement(mapperId);
		BoundSql boundSql = mappedStatement.getBoundSql(params);
		String sql = boundSql.getSql();
       
//        if (LOGGER.isDebugEnabled()) {
//        	LOGGER.debug("Original Sql [" + mapperId + "]:" + sql.replaceAll(" +", " ").replaceAll("\n", ""));
//        }

        SqlConvertFactory factory = TemplateSqlConvertFactory.INSTANCE;
        
        List<String> sqlList = factory.convert(sql, params, mapperId);
//        if (LOGGER.isDebugEnabled()) {
//        	LOGGER.debug("Converted Sql [" + mapperId + "]:" + sql);
//        }
        boundSql.setSql(sqlList.get(0));
        boundSql.setSqlList(sqlList);
        
        mappedStatement.setSqlList(sqlList);
        mappedStatement.setBoundSql(boundSql);
	}
	
	protected boolean needParser(String mapperId) {
        Boolean parse = NEED_PARSE_CACHE.get(mapperId);
        if (parse != null) {
            return parse;
        }
        if (!mapperId.endsWith("!selectKey")) {
            ShardingConfigFactory configHolder = ShardingConfigFactory.getInstance();
            if ((!configHolder.isIgnoreId(mapperId))
                && ((!configHolder.isConfigParseId()) || (configHolder.isParseId(mapperId)))) {
                parse = true;
            }
        }
        if (parse == null) {
            parse = false;
        }
        NEED_PARSE_CACHE.put(mapperId, parse);
        return parse;
    }
}
