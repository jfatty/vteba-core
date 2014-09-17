package com.vteba.tx.jdbc.mybatis.strategy.impl;

import com.vteba.tx.jdbc.mybatis.cache.ShardingTableCache;
import com.vteba.tx.jdbc.mybatis.strategy.ShardingStrategy;
import com.vteba.tx.matrix.info.TableInfo;
import com.vteba.utils.date.DateUtils;

/**
 * 默认的分表策略，按月份分表。
 * @author yinlei
 * @since 2013-12-16 14:56
 */
public class DefaultShardingStrategy implements ShardingStrategy {
    private static final String DELETE = "deleteById";
    private static final String UPDATE = "updateById";
    private static final String GET = "get";
    
    @Override
    public String getTargetTableName(String baseTableName, Object paramObject, String mapperId) {
        return baseTableName + "_" + DateUtils.toDateString("yyyyMM") + "m";
    }
    
    /**
     * insert，直接insert当前表，这个最简单
     * @param baseTableName 原表名
     * @param params sql参数，是参数原始的对象，例如User，Account等POJO
     * @param mapperId mybatis sql映射id
     * @return 分区表名
     */
    public String getInsertTable(String baseTableName, Object params, String mapperId) {
        TableInfo tableInfo = ShardingTableCache.get(baseTableName);
        String tableName = tableInfo.getCurrentTable();
        
        return tableName;
    }
    
    public String getDeleteTable(String baseTableName, Object params, String mapperId) {
        String tableName = null;
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(DELETE)) {
            String id = (String) params;
            String tableSuffix = id.substring(id.lastIndexOf("_"));
            tableName = baseTableName + tableSuffix;
        }
        return tableName;
    }
    
    public String getUpdateTable(String baseTableName, Object params, String mapperId) {
        String tableName = null;
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(UPDATE)) {
            
        }
        return tableName;
    }
    
    public String getSelectTable(String baseTableName, Object params, String mapperId) {
        String tableName = null;
        String methodName = mapperId.substring(mapperId.lastIndexOf(".") + 1);
        if (methodName.equals(GET)) {
//            String id = params.toString();
//            String tableSuffix = id.substring(id.lastIndexOf("_"));
            tableName = getTargetTableName(baseTableName, params, mapperId);
        } else {
            tableName = baseTableName + "_" + DateUtils.toDateString("yyyyMM") + "m";
        }
        return tableName;
    }
    // insert，直接insert当前表，这个最简单
    
    // update，如果是根据id，那么也可以直接定位到当前表，如果批量，可以考虑在各个表中并行执行。
    // 如果是外键更新，是否有好的方法呢？
    
    // delete，处理方式同update
    
    // select，如果是根据id查询，那么直接定位表。如果是批量查询，那么也要在各个表中并行执行，然后合并结果
    
    // 根据分表策略，定时任务创建各个表，在表没有创建完成之前，那么存到上一个表中
    
    public static void main(String[] aa) {
        String a = "asdf.asa";
        System.out.println(a.substring(a.lastIndexOf(".") + 1));
    }
}
