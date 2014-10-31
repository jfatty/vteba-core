package com.vteba.tx.jdbc.mybatis.cache;

import com.vteba.tx.matrix.info.ShardsTables;

/**
 * 维护分区表缓存的回调接口
 * @author yinlei 
 * @since 2013-12-17
 */
public interface ShardsTableCache {
    
    public ShardsTables get(String tableName);
    
    public void put(String tableName, ShardsTables tableInfo);
}
