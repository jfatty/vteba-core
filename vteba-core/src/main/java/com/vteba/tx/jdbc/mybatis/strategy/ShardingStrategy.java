package com.vteba.tx.jdbc.mybatis.strategy;

/**
 * 分表策略
 * @author yinlei 
 * @since 2013-12-9
 */
public interface ShardingStrategy {
    
    /**
     * 表名变更策略。
     * @param baseTableName 原表名
     * @param paramObject sql参数
     * @param mapperId mybatis sql映射的id
     * @return 新的表名
     */
    public String getTargetTableName(String baseTableName, Object paramObject, String mapperId);
}
