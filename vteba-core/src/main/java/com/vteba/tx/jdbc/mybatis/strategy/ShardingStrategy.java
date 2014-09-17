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
    
    /**
     * insert，直接insert当前表，这个最简单
     * @param baseTableName 原表名
     * @param params sql参数，是参数原始的对象，例如User，Account等POJO
     * @param mapperId mybatis sql映射id
     * @return 分区表名
     */
    public String getInsertTable(String baseTableName, Object params, String mapperId);
    
    public String getDeleteTable(String baseTableName, Object params, String mapperId);
    
    public String getUpdateTable(String baseTableName, Object params, String mapperId);
    
    public String getSelectTable(String baseTableName, Object params, String mapperId);
}
