package com.vteba.tx.jdbc.mybatis.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.executor.statement.StatementHandler;
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

import com.vteba.tx.jdbc.mybatis.config.ShardingConfigFactory;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigParser;
import com.vteba.tx.jdbc.mybatis.converter.SqlConverterFactory;

/**
 * 基于Mybatis插件拦截器，实现的分表分片。
 * @author yinlei 
 * @since 2013-12-10
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { java.sql.Connection.class }) })
public class ShardingPluginsInterceptor implements Interceptor {

    public static final String SHARDING_CONFIG = "shardingConfig";
    private static final Log log = LogFactory.getLog(ShardingPluginsInterceptor.class);
    private static final ConcurrentMap<String, Boolean> NEED_PARSE_CACHE = new ConcurrentHashMap<String, Boolean>();
    private static final ConcurrentMap<String, String> SQL_CACHE = new ConcurrentHashMap<String, String>();

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MappedStatement mappedStatement = statementHandler.getMappedStatement();

        String mapperId = mappedStatement.getId();
        if (isNeedParse(mapperId)) {
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();
            String key = sql;
            String alreadyParsedSQL = SQL_CACHE.get(key);
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
            Object params = boundSql.getParameterObject();

            SqlConverterFactory factory = SqlConverterFactory.getInstance();
            
            sql = factory.convert(sql, params, mapperId);
            if (log.isDebugEnabled()) {
                log.debug("Converted Sql [" + mapperId + "]:" + sql);
            }
            SQL_CACHE.put(key, sql);
            boundSql.setSql(sql);
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

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
    
    public static void main(String[] aa) {
        Map<String, String> map = new HashMap<String, String>();
        ConcurrentMap<String, String> curMap = new ConcurrentHashMap<String, String>();
        long d = System.nanoTime();
        map.put("1", "2");
        for (Integer i = 0; i < 10; i++) {
            map.get("1");
        }
        System.out.println(System.nanoTime() - d);
        
        d = System.nanoTime();
        curMap.put("3", "4");
        for (Integer i = 0; i < 10; i++) {
            curMap.get("3");
        }
        System.out.println(System.nanoTime() -d);
    }
}
