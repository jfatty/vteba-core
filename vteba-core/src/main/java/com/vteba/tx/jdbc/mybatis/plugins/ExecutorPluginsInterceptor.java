package com.vteba.tx.jdbc.mybatis.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.vteba.tx.jdbc.mybatis.cache.SQLCache;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigParser;
import com.vteba.tx.jdbc.mybatis.converter.SqlConvertFactory;
import com.vteba.tx.jdbc.mybatis.converter.internal.TemplateSqlConvertFactory;

@Deprecated
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class })})
public class ExecutorPluginsInterceptor implements Interceptor {

	public static final String SHARDING_CONFIG = "shardingConfig";
    private static final Log log = LogFactory.getLog(ExecutorPluginsInterceptor.class);
    private static final ConcurrentMap<String, Boolean> NEED_PARSE_CACHE = new ConcurrentHashMap<String, Boolean>();
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//Executor target = (Executor) invocation.getTarget();
		Object[] args = invocation.getArgs();
		MappedStatement mappedStatement = (MappedStatement) args[0];
		Object params = args[1];
		//RowBounds rowBounds = (RowBounds) args[2];
		//ResultHandler resultHandler = (ResultHandler) args[3];
		BoundSql boundSql = mappedStatement.getBoundSql(params);

		String mapperId = mappedStatement.getId();
        if (isNeedParse(mapperId)) {
            String sql = boundSql.getSql();
            String key = sql;
            SQLCache.reset(key);
            String alreadyParsedSQL = SQLCache.get(key);
            if (alreadyParsedSQL != null) {
                boundSql.setSql(alreadyParsedSQL);
                if (log.isDebugEnabled()) {
                    log.debug("MapperId=[" + mapperId + "]的sql已经被解析，从缓存中获取：sql=[" + alreadyParsedSQL + "]。");
                }
                return invocation.proceed();
            }
           
            if (log.isDebugEnabled()) {
                log.debug("Original Sql [" + mapperId + "]:" + sql.replaceAll(" +", " ").replaceAll("\n", ""));
            }

            //JsqlParserConverterFactory factory = JsqlParserConverterFactory.getInstance();
            SqlConvertFactory factory = TemplateSqlConvertFactory.INSTANCE;
            
            List<String> sqlList = factory.convert(sql, params, mapperId);
            if (log.isDebugEnabled()) {
                log.debug("Converted Sql [" + mapperId + "]:" + sql);
            }
            SQLCache.put(key, sqlList.get(0));
            boundSql.setSql(sqlList.get(0));
            boundSql.setSqlList(sqlList);
            
            mappedStatement.setSqlList(sqlList);
            mappedStatement.setBoundSql(boundSql);
            
        }
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		String config = properties.getProperty(SHARDING_CONFIG);
        if (config == null || config.trim().length() == 0) {
            throw new IllegalArgumentException("property 'shardingConfig' is requested.");
        }
        InputStream input = null;
        try {
            input = Resources.getResourceAsStream(config);
            ShardingConfigParser.parse(input);
        } catch (IOException e) {
            throw new IllegalArgumentException("读取mybatis分表配置文件[" + config + "]异常。", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析mybatis分表配置文件[" + config + "]异常。", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        
        // 临时处理的，以后会将配置放到数据库中的
//        ShardsTable tableInfo = new ShardsTable();
//        tableInfo.setCurrentTable("user_201409m");
//        tableInfo.setTableName("user");
//        List<Long> tableIndex = new ArrayList<Long>();
//        tableIndex.add(201409L);
//        tableIndex.add(201410L);
//        tableInfo.setTableIndexList(tableIndex);
//        ShardingTableCache.put("user", tableInfo);

	}

	private boolean isNeedParse(String mapperId) {
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
