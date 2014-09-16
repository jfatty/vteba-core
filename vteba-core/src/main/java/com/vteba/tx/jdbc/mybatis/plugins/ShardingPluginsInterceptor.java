package com.vteba.tx.jdbc.mybatis.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.vteba.tx.jdbc.mybatis.config.ShardConfigHolder;
import com.vteba.tx.jdbc.mybatis.config.ShardingConfigParser;
import com.vteba.tx.jdbc.mybatis.converter.SqlConverterFactory;

/**
 * 基于Mybatis插件拦截器，实现的分表分片。
 * @author yinlei 
 * @since 2013-12-10
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { java.sql.Connection.class }) })
public class ShardingPluginsInterceptor implements Interceptor {

    private static final Log                                log             = LogFactory.getLog(ShardingPluginsInterceptor.class);
    public static final String                              SHARDING_CONFIG = "shardingConfig";
    private static final ConcurrentMap<String, Boolean> cache           = new ConcurrentHashMap<String, Boolean>();

    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MappedStatement mappedStatement = statementHandler.getMappedStatement();

        String mapperId = mappedStatement.getId();
        if (isShouldParse(mapperId)) {
            String sql = statementHandler.getBoundSql().getSql();
            if (log.isDebugEnabled()) {
                log.debug("Original Sql [" + mapperId + "]:" + sql.replaceAll(" +", " ").replaceAll("\n", ""));
            }
            Object params = statementHandler.getBoundSql().getParameterObject();

            SqlConverterFactory factory = SqlConverterFactory.getInstance();
            sql = factory.convert(sql, params, mapperId);
            if (log.isDebugEnabled()) {
                log.debug("Converted Sql [" + mapperId + "]:" + sql);
            }
            statementHandler.getBoundSql().setSql(sql);
        }
        return invocation.proceed();
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        String config = properties.getProperty(SHARDING_CONFIG, null);
        if ((config == null) || (config.trim().length() == 0)) {
            throw new IllegalArgumentException("property 'shardingConfig' is requested.");
        }
        InputStream input = null;
        try {
            input = Resources.getResourceAsStream(config);
            ShardingConfigParser.parse(input);
            return;
        } catch (IOException e) {
            log.error("Get sharding config file failed.", e);
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            log.error("Parse sharding config file failed.", e);
            throw new IllegalArgumentException(e);
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

    private boolean isShouldParse(String mapperId) {
        Boolean parse = (Boolean) cache.get(mapperId);
        if (parse != null) {
            return parse.booleanValue();
        }
        if (!mapperId.endsWith("!selectKey")) {
            ShardConfigHolder configHolder = ShardConfigHolder.getInstance();
            if ((!configHolder.isIgnoreId(mapperId))
                && ((!configHolder.isConfigParseId()) || (configHolder.isParseId(mapperId)))) {
                parse = Boolean.valueOf(true);
            }
        }
        if (parse == null) {
            parse = Boolean.valueOf(false);
        }
        cache.put(mapperId, parse);
        return parse.booleanValue();
    }
}
