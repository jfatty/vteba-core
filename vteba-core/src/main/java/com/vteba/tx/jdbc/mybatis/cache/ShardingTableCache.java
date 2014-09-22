package com.vteba.tx.jdbc.mybatis.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.vteba.tx.matrix.info.ShardsTable;

/**
 * 维护分区表缓存。这个应该在系统启动的时候维护缓存中的信息。
 * @author yinlei 
 * @since 2013-12-17
 */
public class ShardingTableCache {
    private static final ConcurrentMap<String, ShardsTable> shardingTableCache = new ConcurrentHashMap<String, ShardsTable>();
    private static ShardingTableCache instance = new ShardingTableCache();
    
    private ShardingTableCache() {}
    
    public static ShardingTableCache get() {
        return instance;
    }
    
    public ConcurrentMap<String, ShardsTable> getCache() {
        return shardingTableCache;
    }
    
    public static ShardsTable get(String tableName) {
        return shardingTableCache.get(tableName);
    }
    
    public static void put(String tableName, ShardsTable tableInfo) {
        shardingTableCache.put(tableName, tableInfo);
    }
}
