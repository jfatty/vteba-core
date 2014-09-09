package com.vteba.tx.jdbc.mybatis.strategy;

/**
 * 分表策略
 * @author yinlei 
 * @since 2013-12-9
 */
public interface ShardingStrategy {
    public String getTargetTableName(String baseTableName, Object paramObject, String mapperId);
}
